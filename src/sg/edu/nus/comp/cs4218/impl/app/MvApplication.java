package sg.edu.nus.comp.cs4218.impl.app;

import sg.edu.nus.comp.cs4218.app.MvInterface;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.MvException;

import java.io.InputStream;
import java.io.OutputStream;

public class MvApplication implements MvInterface {

    @Override
    public void run(String[] args, InputStream stdin, OutputStream stdout) throws AbstractApplicationException {

    }

    @Override
    public void mvSrcFileToDestFile(Boolean isOverwrite, String srcFile, String destFile) throws MvException {

    }

    @Override
    public void mvFilesToFolder(Boolean isOverwrite, String destFolder, String... fileName) throws MvException {

    }
}
