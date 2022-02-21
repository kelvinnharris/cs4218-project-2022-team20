package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.app.LsInterface;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.impl.parser.LsArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_CURR_DIR;

public class LsApplication implements LsInterface {

    private final static String PATH_CURR_DIR = STRING_CURR_DIR + CHAR_FILE_SEP;

    @Override
    public String listFolderContent(Boolean isFoldersOnly, Boolean isRecursive, Boolean isSortByExt,
                                    String... folderName) throws LsException {
        if (folderName.length == 0 && !isRecursive) {
            return listCwdContent(isFoldersOnly, isSortByExt);
        }

        List<Path> paths;
        if (folderName.length == 0) {
            String[] directories = new String[1];
            directories[0] = Environment.currentDirectory;
            paths = resolvePaths(directories);
        } else {
            paths = resolvePaths(folderName);
        }

        return buildResult(paths, isFoldersOnly, isRecursive, isSortByExt, false);
    }

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout)
            throws LsException {
        if (args == null) {
            throw new LsException(ERR_NULL_ARGS);
        }

        if (stdout == null) {
            throw new LsException(ERR_NO_OSTREAM);
        }

        LsArgsParser parser = new LsArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new LsException(e.getMessage());
        }

        Boolean foldersOnly = parser.isFoldersOnly();
        Boolean recursive = parser.isRecursive();
        Boolean sortByExt = parser.isSortByExt();
        String[] directories = parser.getDirectories()
                .toArray(new String[parser.getDirectories().size()]);
        String result = listFolderContent(foldersOnly, recursive, sortByExt, directories);

        try {
            stdout.write(result.getBytes());
            stdout.write(StringUtils.STRING_NEWLINE.getBytes());
        } catch (Exception e) {
            throw new LsException(ERR_WRITE_STREAM);
        }
    }

    /**
     * Lists only the current directory's content and RETURNS. This does not account for recursive
     * mode in cwd.
     *
     * @param isFoldersOnly - is folders output only
     * @param isSortByExt - is sort by extension
     * @return String
     */
    private String listCwdContent(Boolean isFoldersOnly, Boolean isSortByExt) throws LsException {
        String cwd = Environment.currentDirectory;
        try {
            return formatContents(getContents(Paths.get(cwd), isFoldersOnly), isSortByExt, false);
        } catch (InvalidDirectoryException e) {
            throw new LsException("Unexpected error occurred!");
        }
    }

    /**
     * Builds the resulting string to be written into the output stream.
     * <p>
     * NOTE: This is recursively called if user wants recursive mode.
     *
     * @param paths         - list of java.nio.Path objects to list
     * @param isFoldersOnly - only list the folder contents
     * @param isRecursive   - recursive mode, repeatedly ls the child directories
     * @param isSortByExt - sorts folder contents alphabetically by file extension (characters after the last ‘.’ (without quotes)). Files with no extension are sorted first.
     * @return String to be written to output stream.
     */
    private String buildResult(List<Path> paths, Boolean isFoldersOnly, Boolean isRecursive, Boolean isSortByExt, Boolean hasRecurse) {
        StringBuilder result = new StringBuilder();
        boolean isSinglePathTraced = paths.size() == 1;

        for (Path path : paths) {
            try {
                List<Path> contents;

                if (path.equals(Path.of (""))) {
                    throw new InvalidDirectoryException(path.toString());
                }

                if (Files.isDirectory(path) || (isRecursive && !path.getParent().toString().equals(Environment.currentDirectory))) {
                    contents = getContents(path, isFoldersOnly);
                } else if (Files.exists(path)) {
                    contents = new ArrayList<>();
                    contents.add(path);
                } else {
                    throw new InvalidDirectoryException(getRelativeToCwd(path).toString());
                }

                // if we only list down one folder no need a folder name indicator on output
                if (Files.isDirectory(path) && (isRecursive || !isSinglePathTraced || hasRecurse)) {
                    String relativePath = getRelativeToCwd(path).toString();
                    result.append(StringUtils.isBlank(relativePath) ? PATH_CURR_DIR : relativePath);

                    if (Files.isRegularFile(Path.of(relativePath))) {
                        result.append(StringUtils.STRING_NEWLINE);
                    } else {
                        result.append(":" + StringUtils.STRING_NEWLINE);
                    }
                }

                Boolean printFullPath = !Files.isDirectory(path);
                String formatted = formatContents(contents, isSortByExt, printFullPath);
                result.append(formatted);

                if (!formatted.isEmpty()) {
                    // Empty directories should not have an additional new line
                    result.append(StringUtils.STRING_NEWLINE);
                }
                result.append(StringUtils.STRING_NEWLINE);

                // RECURSE!
                if (isRecursive && contents.size() != 0) {
                    String oldResult = buildResult(contents, isFoldersOnly, isRecursive, isSortByExt, true);
                    result.append(oldResult);
                    if (!oldResult.equals("")) {
                        result.append(StringUtils.STRING_NEWLINE);
                        result.append(StringUtils.STRING_NEWLINE);
                    }
                }
            } catch (InvalidDirectoryException e) {
                // NOTE: This is pretty hackish IMO - we should find a way to change this
                // If the user is in recursive mode, and if we resolve a file that isn't a directory
                // we should not spew the error message.
                //
                // However, the user might have written a command like `ls invalid1 valid1 -R`, what
                // do we do then?
                if (!isRecursive) {
                    result.append(e.getMessage());
                    result.append(StringUtils.STRING_NEWLINE);
                }
            }
        }

        return result.toString().trim();
    }

    /**
     * Formats the contents of a directory into a single string.
     *
     * @param contents    - list of items in a directory
     * @param isSortByExt - sorts folder contents alphabetically by file extension (characters after the last ‘.’ (without quotes)). Files with no extension are sorted first.
     * @return String
     */
    private String formatContents(List<Path> contents, Boolean isSortByExt, Boolean printFullPath) {
        List<String> fileNames = new ArrayList<>();
        for (Path path : contents) {
            if (printFullPath) {
                fileNames.add(getRelativeToCwd(path).toString());
            } else {
                fileNames.add(path.getFileName().toString());
            }
        }

        if (isSortByExt) {
            fileNames.sort((a, b) -> {
                String[] first = a.split("\\.");
                String[] second = b.split("\\.");

                if (first.length == 1 && second.length == 1) {
                    return a.compareTo(b);
                } else if (first.length == 1) {
                    return -1;
                } else if (second.length == 1) {
                    return 1;
                } else {
                    return first[first.length - 1].compareTo(second[second.length - 1]);
                }
            });
        }


        StringBuilder result = new StringBuilder();
        for (String fileName : fileNames) {
            result.append(fileName);
            result.append(StringUtils.STRING_NEWLINE);
        }

        return result.toString().trim();
    }

    /**
     * Gets the contents in a single specified directory.
     *
     * @param directory - directory
     * @return List of files + directories in the passed directory.
     */
    private List<Path> getContents(Path directory, Boolean isFoldersOnly)
            throws InvalidDirectoryException {
        if (!Files.exists(directory)) {
            throw new InvalidDirectoryException(getRelativeToCwd(directory).toString());
        }

        if (!Files.isDirectory(directory)) {
            throw new InvalidDirectoryException(getRelativeToCwd(directory).toString());
        }

        List<Path> result = new ArrayList<>();
        File pwd = directory.toFile();
        File[] files = pwd.listFiles();
        if (files == null) {
            throw new InvalidDirectoryException(getRelativeToCwd(directory).toString());
        }

        for (File f : files) {
            if (isFoldersOnly && !f.isDirectory()) {
                continue;
            }

            if (!f.isHidden()) {
                result.add(f.toPath());
            }
        }

        Collections.sort(result);

        return result;
    }

    /**
     * Resolve all paths given as arguments into a list of Path objects for easy path management.
     *
     * @param directories - directories
     * @return List of java.nio.Path objects
     */
    private List<Path> resolvePaths(String... directories) throws LsException {
        List<Path> paths = new ArrayList<>();
        for (String directory : directories) {
            if (directory.equals("")) {
                paths.add(Path.of(directory));
            } else {
                paths.add(resolvePath(directory));
            }
        }

        return paths;
    }

    /**
     * Converts a String into a java.nio.Path objects. Also resolves if the current path provided
     * is an absolute path.
     *
     * @param directory - directory
     * @return Path
     */
    private Path resolvePath(String directory) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            if (directory.length() > 2 && directory.charAt(1) == ':' && directory.charAt(2) == '\\') {
                return Paths.get(directory);
            }
        } else {
            if (directory.charAt(0) == '/') {
                // This is an absolute path
                return Paths.get(directory);
            }
        }

        return Paths.get(Environment.currentDirectory, directory);
    }

    /**
     * Converts a path to a relative path to the current directory.
     *
     * @param path - path
     * @return Path
     */
    private Path getRelativeToCwd(Path path) {
        return Paths.get(Environment.currentDirectory).relativize(path);
    }

    private class InvalidDirectoryException extends Exception {
        InvalidDirectoryException(String directory) {
            super(String.format("ls: cannot access '%s': No such file or directory", directory));
        }

        InvalidDirectoryException(String directory, Throwable cause) {
            super(String.format("ls: cannot access '%s': No such file or directory", directory),
                    cause);
        }
    }
}
