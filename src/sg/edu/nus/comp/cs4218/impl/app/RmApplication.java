package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.RmInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.parser.RmArgsParser;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NO_PERM;

public class RmApplication implements RmInterface {

    /**
     * Runs application with specified input data and specified output stream.
     *
     * @param args
     * @param stdin
     * @param stdout
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {
        // Format: rm [Options] FILES...
        if (stdout == null) {
            throw new RmException(ERR_NULL_STREAMS);
        }
        RmArgsParser parser = new RmArgsParser();
        try {
            parser.parse(args);
        } catch (InvalidArgsException e) {
            throw new RmException(e.getMessage());
        }

        try {
            remove(parser.isEmptyDir(), parser.isRecursive(), parser.getFiles().toArray(new String[0]));
        } catch (Exception e) {
            throw new RmException(e.getMessage());//NOPMD
        }
    }

    /**
     * Remove the file. (It does not remove folder by default)
     *
     * @param isEmptyFolder Boolean option to delete a folder only if it is empty
     * @param isRecursive   Boolean option to recursively delete the folder contents (traversing
     *                      through all folders inside the specified folder)
     * @param fileNames      Array of String of file names
     * @throws Exception
     */
    @Override
    public void remove(Boolean isEmptyFolder, Boolean isRecursive, String... fileNames) throws Exception {
        if (fileNames == null) {
            throw new Exception(ERR_NULL_ARGS);
        }
        List<String> lines = new ArrayList<>();
        boolean checkRemove;
        for (String file : fileNames) {
            File node = IOUtils.resolveFilePath(file).toFile();
            if (!node.exists()) {
                throw new Exception(ERR_FILE_NOT_FOUND);
            }

            if (isRecursive) {
                checkRemove = removeFile(node);
            } else if (isEmptyFolder) {
                File[] contents = node.listFiles();

                if (contents != null && contents.length != 0) {

                    throw new Exception(ERR_DIR_NOT_EMPTY);
                }
                checkRemove = node.delete();
            } else if (node.isDirectory()) {
                throw new Exception(ERR_IS_DIR);
            } else if (!node.canRead()) {
                throw new Exception(ERR_NO_PERM);
            } else {
                // if it is a file
                checkRemove = node.delete();
            }

            if (!checkRemove) {
                throw new RmException("failed to remove");
            }
        }
    }

    public boolean removeFile(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File currFile : contents) {
                removeFile(currFile);
            }
        }
        return file.delete();
    }
}
