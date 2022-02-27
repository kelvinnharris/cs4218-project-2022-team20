package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import sg.edu.nus.comp.cs4218.impl.util.TestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PasteApplicationTest {
    private static PasteApplication pasteApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpPasteTestFolder/";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String ERR_IS_DIRECTORY = ": Is a directory";
    private static final String ERR_NOT_FOUND = ": No such file or directory";

    private static final String STD_IN = "-";

    private static final String NONEXISTENTFILE = "paste";

    private static final String FILE_NAME_1 = "test1.txt";
    private static final String FILE_PATH_1 = TEST_FOLDER_NAME + FILE_NAME_1;
    private static final String FILE_NAME_2 = "test2.txt";
    private static final String FILE_PATH_2 = TEST_FOLDER_NAME + FILE_NAME_2;
    private static final String FILE_STD_IN = "stdIn.txt";
    private static final String FILE_PATH_STDIN = TEST_FOLDER_NAME + FILE_STD_IN;

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        String sbContentFile1 = "1" + StringUtils.STRING_NEWLINE +
                "2" + StringUtils.STRING_NEWLINE +
                "3" + StringUtils.STRING_NEWLINE +
                "4" + StringUtils.STRING_NEWLINE +
                "5";
        TestUtils.createFile(FILE_PATH_1, sbContentFile1);

        String sbContentFile2 = "A" + StringUtils.STRING_NEWLINE +
                "B" + StringUtils.STRING_NEWLINE +
                "C" + StringUtils.STRING_NEWLINE +
                "D" + StringUtils.STRING_NEWLINE +
                "E";
        TestUtils.createFile(FILE_PATH_2, sbContentFile2);

        String sbFileStdIn = "This is from stdIn" + StringUtils.STRING_NEWLINE +
                "This is stdIn line 2" + StringUtils.STRING_NEWLINE;
        TestUtils.createFile(FILE_PATH_STDIN, sbFileStdIn);
    }

    @BeforeEach
    void setUpEach() {
        // Instantiate here because every time there is a call command, a new instance of the application is created
        pasteApplication = new PasteApplication();
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() {
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    @Test
        // command: paste tmpPasteTestFolder/test1.txt tmpPasteTestFolder/test2.txt
    void testPaste_fileInputWithoutFlag_shouldShowMergedContentsInParallel() throws Exception {
        pasteApplication.setCurrentOperation(pasteApplication.getFileOperation());
        String result = pasteApplication.mergeFile(false, FILE_PATH_1, FILE_PATH_2);

        String sbExpected = "1" + StringUtils.STRING_TAB + "A" + StringUtils.STRING_NEWLINE +
                "2" + StringUtils.STRING_TAB + "B" + StringUtils.STRING_NEWLINE +
                "3" + StringUtils.STRING_TAB + "C" + StringUtils.STRING_NEWLINE +
                "4" + StringUtils.STRING_TAB + "D" + StringUtils.STRING_NEWLINE +
                "5" + StringUtils.STRING_TAB + "E";

        assertEquals(sbExpected, result);
    }

    @Test
        // command: paste -s tmpPasteTestFolder/test1.txt tmpPasteTestFolder/test2.txt
    void testPaste_fileInputWithFlag_shouldShowMergedContentsSerially() throws Exception {
        pasteApplication.setCurrentOperation(pasteApplication.getFileOperation());
        String result = pasteApplication.mergeFile(true, FILE_PATH_1, FILE_PATH_2);

        String sbExpected = "1" + StringUtils.STRING_TAB + "2" + StringUtils.STRING_TAB +
                "3" + StringUtils.STRING_TAB + "4" + StringUtils.STRING_TAB +
                "5" + StringUtils.STRING_NEWLINE +
                "A" + StringUtils.STRING_TAB + "B" + StringUtils.STRING_TAB +
                "C" + StringUtils.STRING_TAB + "D" + StringUtils.STRING_TAB +
                "E";

        assertEquals(sbExpected, result);
    }

    @Test
        // command: paste tmpPasteTestFolder/test1.txt tmpPasteTestFolder/test2.txt
    void testPaste_stdInWithoutFlag_shouldShowMergedContentsInParallel() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_2); // NOPMD
        String result = pasteApplication.mergeStdin(false, inputStream);
        IOUtils.closeInputStream(inputStream);

        String sbExpected = "A" + StringUtils.STRING_NEWLINE +
                "B" + StringUtils.STRING_NEWLINE +
                "C" + StringUtils.STRING_NEWLINE +
                "D" + StringUtils.STRING_NEWLINE +
                "E";

        assertEquals(sbExpected, result);
    }

    @Test
        // command: paste tmpPasteTestFolder/test1.txt tmpPasteTestFolder/test2.txt
    void testPaste_stdInWithFlag_shouldShowMergedContentsSerially() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_2); // NOPMD
        String result = pasteApplication.mergeStdin(true, inputStream);
        IOUtils.closeInputStream(inputStream);

        String sbExpected = "A" + StringUtils.STRING_TAB +
                "B" + StringUtils.STRING_TAB +
                "C" + StringUtils.STRING_TAB +
                "D" + StringUtils.STRING_TAB +
                "E";

        assertEquals(sbExpected, result);
    }

    @Test
        // command: paste - tmpPasteTestFolder/test1.txt -
    void testPaste_fileInputAndStdIntWithoutFlag_shouldShowMergedContentsInParallel() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_2); // NOPMD
        String result = pasteApplication.mergeFileAndStdin(false, inputStream, STD_IN, FILE_PATH_1, STD_IN);
        IOUtils.closeInputStream(inputStream);

        String sbExpected = "A" + StringUtils.STRING_TAB + "1" + StringUtils.STRING_TAB + "B" + StringUtils.STRING_NEWLINE +
                "C" + StringUtils.STRING_TAB + "2" + StringUtils.STRING_TAB + "D" + StringUtils.STRING_NEWLINE +
                "E" + StringUtils.STRING_TAB + "3" + StringUtils.STRING_TAB + "" + StringUtils.STRING_NEWLINE +
                "" + StringUtils.STRING_TAB + "4" + StringUtils.STRING_TAB + "" + StringUtils.STRING_NEWLINE +
                "" + StringUtils.STRING_TAB + "5" + StringUtils.STRING_TAB + "";

        assertEquals(sbExpected, result);
    }

    @Test
        // command: paste -s - tmpPasteTestFolder/test2.txt -
    void testPaste_fileInputAndStdIntWithFlag_shouldShowMergedContentsSerially() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        String result = pasteApplication.mergeFileAndStdin(true, inputStream, STD_IN, FILE_PATH_2, STD_IN);
        IOUtils.closeInputStream(inputStream);

        String sbExpected = "1" + StringUtils.STRING_TAB + "2" + StringUtils.STRING_TAB +
                "3" + StringUtils.STRING_TAB + "4" + StringUtils.STRING_TAB +
                "5" + StringUtils.STRING_NEWLINE +
                "A" + StringUtils.STRING_TAB + "B" + StringUtils.STRING_TAB +
                "C" + StringUtils.STRING_TAB + "D" + StringUtils.STRING_TAB +
                "E" + StringUtils.STRING_NEWLINE;

        assertEquals(sbExpected, result);
    }

    @Test
        // command: paste - tmpPasteTestFolder/test1.txt tmpPasteTestFolder/
    void testPaste_fileInputAndStdInAndDirectoryWithoutFlag_shouldShowMergedContentsInParallel() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_2); // NOPMD
        String result = pasteApplication.mergeFileAndStdin(false, inputStream, STD_IN, FILE_PATH_1, TEST_FOLDER_NAME);
        IOUtils.closeInputStream(inputStream);

        String sbExpected = "A" + StringUtils.STRING_TAB + "1" + StringUtils.STRING_TAB + "paste: " + TEST_FOLDER_NAME + ERR_IS_DIRECTORY + STRING_NEWLINE +
                StringUtils.STRING_NEWLINE +
                "B" + StringUtils.STRING_TAB + "2" + StringUtils.STRING_TAB + StringUtils.STRING_NEWLINE +
                "C" + StringUtils.STRING_TAB + "3" + StringUtils.STRING_TAB + StringUtils.STRING_NEWLINE +
                "D" + StringUtils.STRING_TAB + "4" + StringUtils.STRING_TAB + StringUtils.STRING_NEWLINE +
                "E" + StringUtils.STRING_TAB + "5" + StringUtils.STRING_TAB;

        assertEquals(sbExpected, result);
    }

    @Test
        // command: paste - tmpPasteTestFolder/test1.txt paste
    void testPaste_fileInputAndStdInAndNonExistentFileWithoutFlag_shouldShowMergedContentsInParallel() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_2); // NOPMD
        String result = pasteApplication.mergeFileAndStdin(false, inputStream, STD_IN, FILE_PATH_1, NONEXISTENTFILE);
        IOUtils.closeInputStream(inputStream);

        assertEquals("paste: " + NONEXISTENTFILE + ": " + ERR_NOT_FOUND, result);
    }
}
