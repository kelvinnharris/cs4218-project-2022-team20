package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.EchoException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.impl.exception.CpException;
import sg.edu.nus.comp.cs4218.impl.parser.CpArgsParser;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;

public class CpApplication implements CpInterface {

    /**
     * Runs application with specified input data and specified output stream.
     *
     * @param args      The arguments representing the files/folders
     * @param stdin     Standard input
     * @param stdout    Standard output
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
            throw new CpException(e.getMessage());
        }

        Boolean isRecursive = parser.isRecursive();
        String[] srcFiles = parser.getSourceFiles();
        String destFile = parser.getDestinationFile();

        Path destAbsPath = getAbsolutePath(destFile);

        if (Files.isDirectory(destAbsPath)) {
            cpFilesToFolder(isRecursive, destFile, srcFiles);
        }

        else {
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
    public void cpSrcFileToDestFile(Boolean isRecursive, String srcFile, String destFile) throws CpException {
        Path srcAbsPath = getAbsolutePath(srcFile);
        Path destAbsPath = getAbsolutePath(destFile);

        if (!Files.isRegularFile(srcAbsPath)) {
            throw new CpException(String.format("Cannot copy content. '%s' is not a file.", srcFile));
        }
        if (srcFile.equals(destFile)) {
            throw new CpException("Cannot copy. Source file cannot be the same as destination file");
        }

        try {
            Files.copy(srcAbsPath, destAbsPath, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new CpException("Cannot copy");
        }
    }

    /**
     * Wrapper function for copying files to destination folder.
     *
     * @param isRecursive   Copy folders (directories) recursively
     * @param destFolder    Name of destination folder in cwd
     * @param fileName      Array of String of file names
     * @throws CpException  Exception related to cp
     */
    @Override
    public void cpFilesToFolder(Boolean isRecursive, String destFolder, String... fileName) throws CpException {
        // Check if all sources exist before copying
        for (String srcFile : fileName) {
            Path srcAbsPath = getAbsolutePath(srcFile);
            if (!srcAbsPath.toFile().exists()) {
                throw new CpException(String.format("Filename '%s' does not exist.", srcAbsPath));
            }
        }

        for (String srcFile : fileName) {
            String destCwd = String.valueOf(getAbsolutePath(destFolder).getParent());
            String srcCwd = String.valueOf(getAbsolutePath(srcFile).getParent());
            String destFolderName = getAbsolutePath(destFolder).toFile().getName();
            String srcFileName = getAbsolutePath(srcFile).toFile().getName();
            cpFilesToFolderImpl(isRecursive, destCwd, srcCwd, destFolderName, srcFileName);
        }
    }

    /**
     * Copy files to destination folder.
     *
     * @param isRecursive   Copy folders (directories) recursively
     * @param destCwd       Current destination directory
     * @param srcCwd        Current source directory
     * @param destFolder    Destination folder to copy to based relative to destCwd
     * @param srcFile       Source file/folder to copy from relative to srcCwd
     * @throws CpException  Exception related to cp
     */
    public void cpFilesToFolderImpl(Boolean isRecursive, String destCwd, String srcCwd, String destFolder,
                                    String srcFile) throws CpException {
        Path destAbsPath = Paths.get(destCwd, destFolder, srcFile); // e.g. ./destFolder/srcFile
        Path srcAbsPath = Paths.get(srcCwd, srcFile); // e.g. ./srcFile

        try {
            Files.copy(srcAbsPath, destAbsPath, REPLACE_EXISTING);

            // Get all file names in that directory and copy recursively
            if (Files.isDirectory(srcAbsPath) && isRecursive) {
                String[] fileNames = listAllFileNamesInPath(srcAbsPath);
                for (String fileName : fileNames) {
                    String nextDestCwd = Paths.get(destCwd, destFolder).toString();
                    String nextSrcCwd = Paths.get(srcCwd, srcFile).toString();
                    cpFilesToFolderImpl(isRecursive, nextDestCwd, nextSrcCwd, srcFile, fileName);
                }
            }
        } catch (DirectoryNotEmptyException dne){
            throw new CpException("Cannot overwrite folder as it is non-empty");
        } catch (Exception e) {
            throw new CpException(e.getMessage());
        }
    }

    public String[] listAllFileNamesInPath(Path srcAbsPath) {
        return Arrays.stream(Objects.requireNonNull(srcAbsPath.toFile().listFiles()))
                .map(File::getName)
                .collect(Collectors.toList()).toArray(new String[0]);
    }

    public Path getAbsolutePath(String fileName) {
        String cwd = Environment.currentDirectory;
        Path absPath = Paths.get(cwd, fileName).normalize();
        return absPath;
    }

}
