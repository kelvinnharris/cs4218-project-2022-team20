package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;


public class PasteApplicationTest {
    private static PasteApplication pasteApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpPasteTestFolder/";
    private static final String TEST_PATH = ROOT_PATH + "/" + TEST_FOLDER_NAME;

    private static String stdIn = "-";

    private static final String nonExistentFile = "paste";

    private static final String fileName1 = "test1.txt";
    private static final String filePath1 = TEST_FOLDER_NAME + fileName1;
    private static final String fileName2 = "test2.txt";
    private static final String filePath2 = TEST_FOLDER_NAME + fileName2;
    private static final String fileStdIn = "stdIn.txt";
    private static final String filePathStdIn = TEST_FOLDER_NAME + fileStdIn;

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        StringBuilder sbContentFile1 = new StringBuilder();
        sbContentFile1.append("1").append(StringUtils.STRING_NEWLINE);
        sbContentFile1.append("2").append(StringUtils.STRING_NEWLINE);
        sbContentFile1.append("3").append(StringUtils.STRING_NEWLINE);
        sbContentFile1.append("4").append(StringUtils.STRING_NEWLINE);
        sbContentFile1.append("5");
        TestUtils.createFile(filePath1, sbContentFile1.toString());

        StringBuilder sbContentFile2 = new StringBuilder();
        sbContentFile2.append("A").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("B").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("C").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("D").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("E");
        TestUtils.createFile(filePath2, sbContentFile2.toString());

        StringBuilder sbContentFileStdIn = new StringBuilder();
        sbContentFileStdIn.append("This is from stdIn").append(StringUtils.STRING_NEWLINE);
        sbContentFileStdIn.append("This is stdIn line 2").append(StringUtils.STRING_NEWLINE);
        TestUtils.createFile(filePathStdIn, sbContentFileStdIn.toString());
    }
}
