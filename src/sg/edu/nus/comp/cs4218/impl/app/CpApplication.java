package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.parser.CpArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CpApplication implements CpInterface {

    /**
     * Runs application with specified input data and specified output stream.
     *
     * @param args   The arguments representing the files/folders
     * @param stdin  Standard input
     * @param stdout Standard output
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        if (args == null) {
            throw new CpException(ERR_NULL_ARGS);
        }
        if (args.length < 2) {
            throw new CpException(ERR_NO_ARGS);
        }

        CpArgsParser parser = new CpArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new CpException(e);
        }

        Boolean isRecursive = parser.isRecursive();
        String[] srcFiles = parser.getSourceFiles();
        String destFile = parser.getDestinationFile();

        Path destAbsPath = IOUtils.resolveFilePath(destFile);

        if (srcFiles.length == 0) {
            throw new CpException(String.format("missing destination file operand after '%s'", destFile));
        }

        if (!Files.exists(destAbsPath)) {
            if (srcFiles.length > 1) {
                throw new CpException(ERR_TOO_MANY_ARGS);
            }

            // create new file/dir and copy
            if (Files.isRegularFile(Paths.get(srcFiles[0]))) {
                try {
                    Files.createFile(destAbsPath);
                    cpSrcFileToDestFile(isRecursive, srcFiles[0], destFile);
                } catch (IOException ioe) {
                    throw new CpException(ioe.getMessage());
                }
            } else if (Files.isDirectory(Paths.get(srcFiles[0]))) {
                try {
                    Files.createDirectories(destAbsPath);
                    cpFilesToFolder(isRecursive, destFile, srcFiles);
                } catch (IOException ioe) {
                    throw new CpException(ioe.getMessage());
                }
            }
        } else if (Files.isDirectory(destAbsPath)) {
            cpFilesToFolder(isRecursive, destFile, srcFiles);

        } else {
            if (srcFiles.length > 1) {
                throw new CpException(ERR_TOO_MANY_ARGS);
            }
            cpSrcFileToDestFile(isRecursive, srcFiles[0], destFile);
        }
    }

    /**
     * Copy content of source file to destination file.
     *
     * @param isRecursive Copy folders (directories) recursively
     * @param srcFile     Name of source file in cwd
     * @param destFile    Name of destination file in cwd
     */
    @Override
    public String cpSrcFileToDestFile(Boolean isRecursive, String srcFile, String destFile) throws CpException {
        Path srcAbsPath = IOUtils.resolveFilePath(srcFile);
        Path destAbsPath = IOUtils.resolveFilePath(destFile);

        if (!Files.exists(srcAbsPath)) {
            throw new CpException(String.format("cannot stat '%s': No such file or directory", srcFile));
        }
        if (srcAbsPath.toString().equals(destAbsPath.toString())) {
            throw new CpException(String.format("'%s' and '%s' are the same file", srcFile, destFile));
        }
        if (Files.isDirectory(srcAbsPath)) {
            if (isRecursive) {
                throw new CpException(String.format("cannot overwrite non-directory '%s' with directory '%s'", destFile, srcFile));
            } else {
                throw new CpException(String.format("-r not specified; omitting directory '%s'", srcFile));
            }
        }

        try {
            Files.copy(srcAbsPath, destAbsPath, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new CpException(e);
        }
        return null;
    }

    /**
     * Wrapper function for copying files to destination folder.
     *
     * @param isRecursive Copy folders (directories) recursively
     * @param destFolder  Name of destination folder in cwd
     * @param fileName    Array of String of file names
     * @throws CpException Exception related to cp
     */
    @Override
    public String cpFilesToFolder(Boolean isRecursive, String destFolder, String... fileName) throws CpException {
        // Check if all sources exist before copying
        for (String srcFile : fileName) {
            Path srcAbsPath = IOUtils.resolveFilePath(srcFile);
            if (!srcAbsPath.toFile().exists()) {
                throw new CpException(String.format("cannot stat '%s': No such file or directory", srcFile));
            }
        }

        for (String srcFile : fileName) {
            String destCwd = String.valueOf(IOUtils.resolveFilePath(destFolder).getParent());
            String srcCwd = String.valueOf(IOUtils.resolveFilePath(srcFile).getParent());
            String destFolderName = IOUtils.resolveFilePath(destFolder).toFile().getName();
            String srcFileName = IOUtils.resolveFilePath(srcFile).toFile().getName();
            cpFilesToFolderImpl(isRecursive, destCwd, srcCwd, destFolderName, srcFileName, destFolder, srcFile, false);
        }

        return null;
    }

    /**
     * Copy files to destination folder.
     *
     * @param isRecursive   Copy folders (directories) recursively
     * @param destCwd       Current destination directory
     * @param srcCwd        Current source directory
     * @param destFolder    Destination folder to copy to based relative to destCwd
     * @param srcFile       Source file/folder to copy from relative to srcCwd
     * @param destFolderArg Original argument provided in input for destination folder
     * @param srcFileArg    Original argument provided in input for source file
     * @throws CpException Exception related to cp
     */
    public void cpFilesToFolderImpl(Boolean isRecursive, String destCwd, String srcCwd, String destFolder,
                                    String srcFile, String destFolderArg, String srcFileArg,
                                    Boolean isCopiedOnce) throws CpException {
        Path destAbsPath = Paths.get(destCwd, destFolder, srcFile); // e.g. ./destFolder/srcFile
        Path srcAbsPath = Paths.get(srcCwd, srcFile); // e.g. ./srcFile

        try {
            // Get all file names in that directory and copy recursively
            if (Files.isDirectory(srcAbsPath)) {
                if (isRecursive) {
                    // To prevent infinite loop e.g. cp -r a a/b, or cp -r a a
                    if (destAbsPath.startsWith(srcAbsPath) && isCopiedOnce) {
                        throw new CpException(String.format("cannot copy a directory, '%s', into itself, '%s'", srcFileArg,
                                destFolderArg + "/" + srcFile));
                    }

                    isCopiedOnce = true;

                    // Copy the directory itself
                    if (!Files.exists(destAbsPath)) {
                        Files.copy(srcAbsPath, destAbsPath, REPLACE_EXISTING);
                    }

                    // Copy the contents in directory recursively
                    String[] fileNames = listAllFileNamesInPath(srcAbsPath);
                    for (String fileName : fileNames) {
                        String nextDestCwd = Paths.get(destCwd, destFolder).toString();
                        String nextSrcCwd = Paths.get(srcCwd, srcFile).toString();
                        cpFilesToFolderImpl(isRecursive, nextDestCwd, nextSrcCwd, srcFile, fileName, destFolderArg, srcFileArg, isCopiedOnce);
                    }
                } else {
                    throw new CpException(String.format("-r not specified; omitting directory '%s'", srcFile));
                }

            } else if (Files.isRegularFile(srcAbsPath)) { // just copy if file type
                if (srcAbsPath.toString().equals(destAbsPath.toString())) {
                    throw new CpException(String.format("'%s' and '%s' are the same file", srcFileArg, destFolderArg + "/" + srcFile));
                }
                Files.copy(srcAbsPath, destAbsPath, REPLACE_EXISTING);
            }
        } catch (CpException e) {
            throw e;
        } catch (Exception e) {
            throw new CpException(e);
        }
    }

    public String[] listAllFileNamesInPath(Path srcAbsPath) {
        return Arrays.stream(Objects.requireNonNull(srcAbsPath.toFile().listFiles()))
                .map(File::getName)
                .collect(Collectors.toList()).toArray(new String[0]);
    }
}
