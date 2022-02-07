package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.CpInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

import java.io.InputStream;
import java.io.OutputStream;

public class CpApplication implements CpInterface {

    /**
     * Runs application with specified input data and specified output stream.
     *
     * @param args
     * @param stdin
     * @param stdout
     */
    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

    }

    /**
     * copy content of source file to destination file
     *
     * @param isRecursive Copy folders (directories) recursively
     * @param srcFile     of path to source file
     * @param destFile    of path to destination file
     * @throws Exception
     */
    @Override
    public void cpSrcFileToDestFile(Boolean isRecursive, String srcFile, String destFile) throws Exception {

    }

    /**
     * copy files to destination folder
     *
     * @param isRecursive Copy folders (directories) recursively
     * @param destFolder  of path to destination folder
     * @param fileName    Array of String of file names
     * @throws Exception
     */
    @Override
    public void cpFilesToFolder(Boolean isRecursive, String destFolder, String... fileName) throws Exception {

    }
}
