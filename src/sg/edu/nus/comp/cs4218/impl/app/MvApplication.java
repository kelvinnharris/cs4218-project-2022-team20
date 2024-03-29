package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.parser.MvArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
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
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class MvApplication implements MvInterface { //NOPMD - suppressed GodClass - Some of the methods are private to Mv and make more sense to put it inside the class

    /**
     * Run application with specified input data and specified output stream.
     *
     * @param args   The arguments representing the files/folders
     * @param stdin  Standard input
     * @param stdout Standard output
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        if (args == null) {
            throw new MvException(ERR_NULL_ARGS);
        }
        if (args.length < 2) {
            throw new MvException(ERR_NO_ARGS);
        }

        MvArgsParser parser = new MvArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new MvException(e);
        }

        Boolean isOverwrite = parser.isOverWrite();
        String[] srcFiles = parser.getSourceFiles();
        String destFile = parser.getDestinationFile();

        Path destAbsPath = IOUtils.resolveFilePath(destFile);

        if (srcFiles.length == 0) {
            throw new MvException(String.format("missing destination file operand after '%s'", destFile));
        }

        if (Files.isDirectory(destAbsPath)) {
            mvFilesToFolder(isOverwrite, destFile, srcFiles);

        } else {
            if (srcFiles.length > 1) {
                throw new MvException(String.format("target '%s' is not a directory" , destFile));
            }
            mvSrcFileToDestFile(isOverwrite, srcFiles[0], destFile);
        }
    }

    /**
     * Move or rename source file as destination file.
     *
     * @param isOverwrite Boolean option to perform overwriting
     * @param srcFile     of path to source file
     * @param destFile    of path to destination file
     * @return null
     * @throws MvException Exception related to mv
     */
    @Override
    public String mvSrcFileToDestFile(Boolean isOverwrite, String srcFile, String destFile) throws MvException {
        Path srcAbsPath = IOUtils.resolveFilePath(srcFile);
        Path destAbsPath = IOUtils.resolveFilePath(destFile);


        if (!Files.exists(srcAbsPath)) {
            throw new MvException(String.format("cannot stat '%s': No such file or directory", srcFile));
        }
        if (srcAbsPath.toString().equals(destAbsPath.toString())) {
            throw new MvException(String.format("'%s' and '%s' are the same file", srcFile, destFile));
        }

        // if overwrite and src exists and dest exists, don't move
        if (!isOverwrite && srcAbsPath.toFile().exists() && destAbsPath.toFile().exists()) {
            return null; // no exception thrown
        }

        try {
            // if dest doesn't exists, create and rename, regardless of flag
            if (!Files.exists(destAbsPath)) {
                if (Files.isRegularFile(srcAbsPath)) {
                    Files.createFile(destAbsPath);
                } else if (Files.isDirectory(srcAbsPath)) {
                    Files.createDirectories(destAbsPath);
                }
            }
            Files.move(srcAbsPath, destAbsPath, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new MvException(e);
        }
        return null;
    }

    /**
     * Wrapper function for moving files to destination folder.
     *
     * @param isOverwrite Boolean option to perform overwriting
     * @param destFolder  of path to destination folder
     * @param fileName    Array of String of file names
     * @return null
     * @throws MvException Exception related to mv
     */
    @Override
    public String mvFilesToFolder(Boolean isOverwrite, String destFolder, String... fileName) throws MvException {
        for (String srcFile : fileName) {
            Path srcAbsPath = IOUtils.resolveFilePath(srcFile);
            if (!srcAbsPath.toFile().exists()) {
                throw new MvException(String.format("cannot stat '%s': No such file or directory", srcFile));
            }

            String destCwd = String.valueOf(IOUtils.resolveFilePath(destFolder).getParent());
            String srcCwd = String.valueOf(IOUtils.resolveFilePath(srcFile).getParent());
            String destFolderName = IOUtils.resolveFilePath(destFolder).toFile().getName();
            String srcFileName = IOUtils.resolveFilePath(srcFile).toFile().getName();

            Path destAbsPath = IOUtils.resolveFilePath(Paths.get(destCwd, destFolderName, srcFileName).toString());
            if (isOverwrite || !destAbsPath.toFile().exists()) {
                // copy and delete original files
                cpFilesToFolderImpl(isOverwrite, destCwd, srcCwd, destFolderName, srcFileName, destFolder, srcFile, false);
                srcAbsPath = IOUtils.resolveFilePath(srcFile);
                if (Files.isRegularFile(srcAbsPath)) {
                    srcAbsPath.toFile().delete();
                } else {
                    deleteDir(srcAbsPath.toFile());
                }
            }
        }

        return null;
    }

    /**
     * Copy files to destination folder.
     *
     * @param isOverwrite   Move and overwrite previous files
     * @param destCwd       Current destination directory
     * @param srcCwd        Current source directory
     * @param destFolder    Destination folder to copy to based relative to destCwd
     * @param srcFile       Source file/folder to copy from relative to srcCwd
     * @param destFolderArg Original argument provided in input for destination folder
     * @param srcFileArg    Original argument provided in input for source file
     * @throws MvException Exception related to mv
     */
    public void cpFilesToFolderImpl(Boolean isOverwrite, String destCwd, String srcCwd, String destFolder,
                                    String srcFile, String destFolderArg, String srcFileArg,
                                    Boolean isCopiedOnce) throws MvException {
        Path destAbsPath = Paths.get(destCwd, destFolder, srcFile); // e.g. ./destFolder/srcFile
        Path srcAbsPath = Paths.get(srcCwd, srcFile); // e.g. ./srcFile

        try {
            // Get all file names in that directory and copy recursively
            if (Files.isDirectory(srcAbsPath)) {
                // To prevent infinite loop e.g. cp -r a a/b, or cp -r a a
                if (destAbsPath.startsWith(srcAbsPath) && isCopiedOnce) {
                    throw new MvException(String.format("cannot move a directory, '%s', into itself, '%s'", srcFileArg,
                            destFolderArg + "/" + srcFile));
                }

                Boolean isCopied = true;

                // Copy the directory itself
                if (!Files.exists(destAbsPath)) {
                    Files.copy(srcAbsPath, destAbsPath, REPLACE_EXISTING);
                }

                // Copy the contents in directory recursively
                String[] fileNames = listAllFileNamesInPath(srcAbsPath);
                for (String fileName : fileNames) {
                    String nextDestCwd = Paths.get(destCwd, destFolder).toString();
                    String nextSrcCwd = Paths.get(srcCwd, srcFile).toString();
                    cpFilesToFolderImpl(isOverwrite, nextDestCwd, nextSrcCwd, srcFile, fileName, destFolderArg, srcFileArg, isCopied);
                }

            } else if (Files.isRegularFile(srcAbsPath)) { // just copy if file type
                if (srcAbsPath.toString().equals(destAbsPath.toString())) {
                    throw new MvException(String.format("'%s' and '%s' are the same file", srcFileArg, destFolderArg + "/" + srcFile));
                }
                Files.copy(srcAbsPath, destAbsPath, REPLACE_EXISTING);
            }
        } catch (MvException e) {
            throw e;
        } catch (Exception e) {
            throw new MvException(e);
        }
    }

    public String[] listAllFileNamesInPath(Path srcAbsPath) {
        return Arrays.stream(Objects.requireNonNull(srcAbsPath.toFile().listFiles()))
                .map(File::getName)
                .collect(Collectors.toList()).toArray(new String[0]);
    }
}
