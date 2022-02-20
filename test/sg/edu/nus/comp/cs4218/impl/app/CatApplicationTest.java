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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CatApplicationTest {
    private static CatApplication catApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCatTestFolder/";
    private static final String TEST_PATH = ROOT_PATH + "/" + TEST_FOLDER_NAME;

    private static String stdIn = "-";

    private static final String NUMBER_FORMAT = "%6d ";

    private static final String nonExistentFile = "cat";

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

        TestUtils.createFile(filePath1, "This is WC Test file 1");

        StringBuilder sbContentFile2 = new StringBuilder();
        sbContentFile2.append("This is WC Test file 2").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("Multiple Lines: ").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("a").append(StringUtils.STRING_NEWLINE).append("b").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("c").append(StringUtils.STRING_NEWLINE).append("d");
        TestUtils.createFile(filePath2, sbContentFile2.toString());

        StringBuilder sbContentFileStdIn = new StringBuilder();
        sbContentFileStdIn.append("This is from stdIn").append(StringUtils.STRING_NEWLINE);
        sbContentFileStdIn.append("This is from stdIn line 2").append(StringUtils.STRING_NEWLINE);
        TestUtils.createFile(filePathStdIn, sbContentFileStdIn.toString());

    }

    @BeforeEach
    void setUpEach() {
        // Instantiate here because every time there is a call command, a new instance of the application is created
        catApplication = new CatApplication();
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() {
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    @Test
        // command: cat tmpCatTestFolder/test1.txt
    void testCat_fileInputWithoutFlag_shouldShowContentsInFile() throws Exception {
        String result = catApplication.catFiles(false, filePath1);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("This is WC Test file 1");

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: cat tmpCatTestFolder/test1.txt tmpCatTestFolder/test2.txt
    void testCat_multipleFilesInputWithoutFlag_shouldShowContentsInAllFiles() throws Exception {
        String result = catApplication.catFiles(false, filePath1, filePath2);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("This is WC Test file 1").append(StringUtils.STRING_NEWLINE);

        sbExpected.append("This is WC Test file 2").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("Multiple Lines: ").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("a").append(StringUtils.STRING_NEWLINE).append("b").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("c").append(StringUtils.STRING_NEWLINE).append("d");

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: cat
    void testCat_noFileArgumentsWithoutFlag_shouldShowContentsInAllFiles() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(filePath1);
        String result = catApplication.catStdin(false, inputStream);
        IOUtils.closeInputStream(inputStream);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("This is WC Test file 1");

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: cat -
    void testCat_stdInWithoutFlag_shouldShowContentsInAllFiles() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(filePath1);
        String result = catApplication.catFileAndStdin(false, inputStream, stdIn);
        IOUtils.closeInputStream(inputStream);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("This is WC Test file 1");

        assertEquals(sbExpected.toString(), result);
    }

    @Test
    // command: cat -n tmpCatTestFolder/test1.txt tmpCatTestFolder/test2.txt
    void testCat_multipleFilesInputWithFlag_shouldShowContentsInAllFilesWithNumbers() throws Exception {
        String result = catApplication.catFiles(true, filePath1, filePath2);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 1));
        sbExpected.append("This is WC Test file 1").append(StringUtils.STRING_NEWLINE);

        sbExpected.append(String.format(NUMBER_FORMAT, 2));
        sbExpected.append("This is WC Test file 2").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 3));
        sbExpected.append("Multiple Lines: ").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 4));
        sbExpected.append("a").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 5));
        sbExpected.append("b").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 6));
        sbExpected.append("c").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 7)).append("d");

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: cat -n tmpCatTestFolder/test1.txt - tmpCatTestFolder/test2.txt
    void testCat_multipleFilesInputAndStdInWithFlag_shouldShowContentsInAllFilesWithNumbers() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(filePathStdIn);
        String result = catApplication.catFileAndStdin(true, inputStream, filePath1, stdIn, filePath2);
        IOUtils.closeInputStream(inputStream);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 1));
        sbExpected.append("This is WC Test file 1").append(StringUtils.STRING_NEWLINE);

        sbExpected.append(String.format(NUMBER_FORMAT, 2));
        sbExpected.append("This is from stdIn").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 3));
        sbExpected.append("This is from stdIn line 2").append(StringUtils.STRING_NEWLINE);

        sbExpected.append(String.format(NUMBER_FORMAT, 4));
        sbExpected.append("This is WC Test file 2").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 5));
        sbExpected.append("Multiple Lines: ").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 6));
        sbExpected.append("a").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 7));
        sbExpected.append("b").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 8));
        sbExpected.append("c").append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 9)).append("d");

        assertEquals(sbExpected.toString(), result);
    }
}
