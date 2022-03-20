package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import sg.edu.nus.comp.cs4218.impl.util.TestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_FILES;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class CatApplicationTest {
    private static CatApplication catApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCatTestFolder" + StringUtils.CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + StringUtils.CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String ERR_IS_DIRECTORY = ": Is a directory";
    private static final String ERR_NOT_FOUND = ": No such file or directory";

    private static final String STDIN = "-";
    private static final String NUMBER_FORMAT = "%6d ";
    private static final String NON_EXISTENT_FILE = "cat";

    private static final String FILE_NAME_1 = "test1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_2 = "test2.txt";
    private static final String FILE_PATH_2 = TEST_PATH + FILE_NAME_2;
    private static final String STD_IN_TXT = "stdIn.txt";
    private static final String FILE_PATH_STDIN = TEST_PATH + STD_IN_TXT;

    private static final String MULT_LINE_MSG = "Multiple Lines: ";
    private static final String WC_FILE_ONE = "This is WC Test file 1";
    private static final String WC_FILE_TWO = "This is WC Test file 2";

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        TestUtils.createFile(FILE_PATH_1, WC_FILE_ONE);

        String sbContentFile2 = WC_FILE_TWO +
                StringUtils.STRING_NEWLINE +
                MULT_LINE_MSG +
                StringUtils.STRING_NEWLINE +
                "a" + StringUtils.STRING_NEWLINE + "b" + StringUtils.STRING_NEWLINE +
                "c" + StringUtils.STRING_NEWLINE + "d";
        TestUtils.createFile(FILE_PATH_2, sbContentFile2);

        String sbContentStdin = "This is from stdIn" + StringUtils.STRING_NEWLINE +
                "This is from stdIn line 2" + StringUtils.STRING_NEWLINE;
        TestUtils.createFile(FILE_PATH_STDIN, sbContentStdin);

    }

    @BeforeEach
    void setUpEach() {
        // Instantiate here because every time there is a call command, a new instance of the application is created
        catApplication = new CatApplication();
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    // command: cat tmpCatTestFolder/test1.txt
    @Test
    void testCatFiles_fileInputWithoutFlag_shouldShowContentsInFile() throws Exception {
        String result = catApplication.catFiles(false, FILE_PATH_1);

        assertEquals(WC_FILE_ONE, result);
    }

    // command: cat tmpCatTestFolder/test1.txt tmpCatTestFolder/test2.txt
    @Test
    void testCatFiles_multipleFilesInputWithoutFlag_shouldShowContentsInAllFiles() throws Exception {
        String result = catApplication.catFiles(false, FILE_PATH_1, FILE_PATH_2);

        String sbExpected = WC_FILE_ONE + StringUtils.STRING_NEWLINE +
                WC_FILE_TWO + StringUtils.STRING_NEWLINE +
                MULT_LINE_MSG + StringUtils.STRING_NEWLINE +
                "a" + StringUtils.STRING_NEWLINE + "b" + StringUtils.STRING_NEWLINE +
                "c" + StringUtils.STRING_NEWLINE + "d";

        assertEquals(sbExpected, result);
    }

    // command: cat
    @Test
    void testCatStdin_noFileArgumentsWithoutFlag_shouldShowContentsInAllFiles() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_1);
        String result;
        try {
            result = catApplication.catStdin(false, inputStream);
        } finally {
            inputStream.close();
        }

        assertEquals(WC_FILE_ONE, result);
    }

    // command: cat -
    @Test
    void testCatFileAndStdin_stdInWithoutFlag_shouldShowContentsInAllFiles() throws Exception {

        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_1);
        String result;
        try {
            result = catApplication.catFileAndStdin(false, inputStream, STDIN);
        } finally {
            inputStream.close();
        }

        assertEquals(WC_FILE_ONE, result);
    }

    // command: cat -n tmpCatTestFolder/test1.txt tmpCatTestFolder/test2.txt
    @Test
    void testCatFiles_multipleFilesInputWithFlag_shouldShowContentsInAllFilesWithNumbers() throws Exception {
        String result = catApplication.catFiles(true, FILE_PATH_1, FILE_PATH_2);

        String sbExpected = String.format(NUMBER_FORMAT, 1) +
                WC_FILE_ONE + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) +
                WC_FILE_TWO + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) +
                MULT_LINE_MSG + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                "a" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 5) +
                "b" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 6) +
                "c" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 7) + "d";

        assertEquals(sbExpected, result);
    }

    // command: cat -n tmpCatTestFolder/test1.txt - tmpCatTestFolder/test2.txt
    @Test
    void testCatFileAndStdin_multipleFilesInputAndStdInWithFlag_shouldShowContentsInAllFilesWithNumbers() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_STDIN);
        String result;
        try {
            result = catApplication.catFileAndStdin(true, inputStream, FILE_PATH_1, STDIN, FILE_PATH_2);
        } finally {
            inputStream.close();
        }

        String sbExpected = String.format(NUMBER_FORMAT, 1) +
                WC_FILE_ONE + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) +
                "This is from stdIn" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) +
                "This is from stdIn line 2" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                WC_FILE_TWO + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 5) +
                MULT_LINE_MSG + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 6) +
                "a" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 7) +
                "b" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 8) +
                "c" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 9) + "d";

        assertEquals(sbExpected, result);
    }

    // command: cat -n cat tmpCatTestFolder/test2.txt
    @Test
    void testCatFiles_multipleFilesWithNonExistentFileWithFlag_shouldShowContentsInAllFilesWithNumbers() throws Exception {
        String result = catApplication.catFiles(true, NON_EXISTENT_FILE, FILE_PATH_1);

        String sbExpected = "cat: " + NON_EXISTENT_FILE + ERR_NOT_FOUND + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 1) +
                WC_FILE_ONE;

        assertEquals(sbExpected, result);
    }

    // command: cat -n cat tmpCatTestFolder/test2.txt
    @Test
    void testCatFiles_multipleFilesWithDirectoryFileWithFlag_shouldShowContentsInAllFilesWithNumbers() throws Exception {
        String result = catApplication.catFiles(true, FILE_PATH_1, TEST_FOLDER_NAME);

        String sbExpected = String.format(NUMBER_FORMAT, 1) +
                WC_FILE_ONE + StringUtils.STRING_NEWLINE +
                "cat: " + TEST_FOLDER_NAME + ERR_IS_DIRECTORY;

        assertEquals(sbExpected, result);
    }

    @Test
    void testCatStdin_nullInputStream_shouldThrowException() {
        assertThrows(CatException.class, () -> catApplication.catStdin(true, null), ERR_NULL_STREAMS);
    }

    @Test
    void testCatFiles_nullFileNames_shouldThrowException() {
        assertThrows(CatException.class, () -> catApplication.catFiles(true, null), ERR_NULL_FILES);
    }

    @Test
    void testCatFileAndStdin_nullFileNamesAndInputStream_shouldThrowException() throws ShellException, IOException {
        assertThrows(CatException.class, () -> catApplication.catFileAndStdin(true, null, new String[]{}), ERR_NULL_STREAMS);
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_1);
        try {
            assertThrows(CatException.class, () -> catApplication.catFileAndStdin(true, inputStream, null), ERR_NULL_FILES);
        } finally {
            inputStream.close();
        }
    }

    @Test
    void testCatRun_nullStdIn_shouldThrowCatException() {
        assertThrows(CatException.class, () -> catApplication.run(new String[]{FILE_PATH_1}, null, System.out), "Should Throw CatException");
    }

    @Test
    void testCatRun_nullStdout_shouldThrowCatException() {
        assertThrows(CatException.class, () -> catApplication.run(new String[]{FILE_PATH_1}, System.in, null), "Should Throw CatException");
    }

    @Test
    void testCatRun_invalidFlag_shouldThrowCatException() {
        assertThrows(CatException.class, () -> catApplication.run(new String[]{FILE_PATH_1, "-z"}, System.in, System.out), "Should Throw CatException");
    }

    @Test
    void testCatRun_correctInputs_shouldNotThrowException() throws CatException {
        catApplication.run(new String[]{FILE_PATH_1}, System.in, System.out);
        List<String> expected = new ArrayList<>();
        expected.add("This is WC Test file 1");
        assertEquals(expected, catApplication.listResult);
    }
}
