package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_FILES;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class WcApplicationTest {

    private static WcApplication wcApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpWcTestFolder" + StringUtils.CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + StringUtils.CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String STDIN = "-";

    static final String NUMBER_FORMAT = " %7d";
    static final String STRING_WC = "wc: ";
    static final String STRING_FORMAT = " %s";
    static final String TOTAL = "total";

    private static final String NON_EXISTENT_FILE = "wc";

    private static final String FILE_NAME_1 = "test1.txt";
    private static final String FILE_PATH_1 = TEST_FOLDER_NAME + FILE_NAME_1;
    private static final String FILE_NAME_2 = "test2.txt";
    private static final String FILE_PATH_2 = TEST_FOLDER_NAME + FILE_NAME_2;
    private static final String FILE_NAME_3 = "test3.txt";
    private static final String FILE_PATH_3 = TEST_FOLDER_NAME + FILE_NAME_3;
    private static final String FILE_NAME_STAR1 = "testStar1.txt";
    private static final String FILE_NAME_STAR2 = "testStar2.txt";

    private static final String ERR_IS_DIRECTORY = ": Is a directory";
    private static final String ERR_NOT_FOUND = ": No such file or directory";

    @BeforeAll
    static void setUp() throws IOException {
        wcApplication = new WcApplication();
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        TestUtils.createFile(FILE_PATH_1, "This is WC Test file 1" + StringUtils.STRING_NEWLINE);
        TestUtils.createFile(FILE_PATH_2, "This is WC Test file 2" + StringUtils.STRING_NEWLINE + " Test for second line" + StringUtils.STRING_NEWLINE);
        TestUtils.createFile(FILE_PATH_3, "This is WC Test file 3" + StringUtils.STRING_NEWLINE
                + " Test for second line" + StringUtils.STRING_NEWLINE
                + " Test for third line" + StringUtils.STRING_NEWLINE);

        TestUtils.createFile(FILE_NAME_STAR1, "This is test star 1" + StringUtils.STRING_NEWLINE);
        TestUtils.createFile(FILE_NAME_STAR2, "This is test star 2" + StringUtils.STRING_NEWLINE
                + "line 2" + StringUtils.STRING_NEWLINE);
    }

    @BeforeEach
    void setUpEach() {
        // Instantiate here because every time there is a call command, a new instance of the application is created
        wcApplication = new WcApplication();
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() {
        TestUtils.deleteDir(new File(TEST_PATH));
        TestUtils.deleteDir(new File(FILE_NAME_STAR1));
        TestUtils.deleteDir(new File(FILE_NAME_STAR2));
    }

    @Test
    // command: wc tmpWcTestFolder/test1.txt
    void testWc_fileInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, FILE_PATH_1);

        String sbExpected = String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 24) +
                String.format(STRING_FORMAT, FILE_PATH_1);

        assertEquals(sbExpected, result);
    }

    @Test
    // command: wc
    void testWc_noFileArgumentsWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        String result = wcApplication.countFromStdin(true, true, true, input);
        IOUtils.closeInputStream(input);

        assertEquals(String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 24), result);
    }

    @Test
    // command: wc -
    void testWc_stdInFileArgumentWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        String result = wcApplication.countFromFileAndStdin(true, true, true, input, STDIN);
        IOUtils.closeInputStream(input);

        String sbExpected = String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 24) +
                String.format(STRING_FORMAT, STDIN);

        assertEquals(sbExpected, result);
    }

    @Test
    // command: wc tmpWcTestFolder/test1.txt tmpWcTestFolder/test2.txt
    void testWc_multipleFilesFromSameDirectoryInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, new String[]{FILE_PATH_1, FILE_PATH_2});

        String sbExpected = String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 24) +
                String.format(STRING_FORMAT, FILE_PATH_1) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) + String.format(NUMBER_FORMAT, 10) + String.format(NUMBER_FORMAT, 47) +
                String.format(STRING_FORMAT, FILE_PATH_2) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) + String.format(NUMBER_FORMAT, 16) + String.format(NUMBER_FORMAT, 71) +
                String.format(STRING_FORMAT, TOTAL);

        assertEquals(sbExpected, result);
    }

    @Test
        // command: wc tmpWcTestFolder/test1.txt tmpWcTestFolder/test2.txt -
    void testWc_multipleFilesFromSameDirectoryAndStandardInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_3); // NOPMD
        String result = wcApplication.countFromFileAndStdin(true, true, true, input, FILE_PATH_1, FILE_PATH_2, STDIN);
        IOUtils.closeInputStream(input);

        String sbExpected = String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 24) +
                String.format(STRING_FORMAT, FILE_PATH_1) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) + String.format(NUMBER_FORMAT, 10) + String.format(NUMBER_FORMAT, 47) +
                String.format(STRING_FORMAT, FILE_PATH_2) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) + String.format(NUMBER_FORMAT, 14) + String.format(NUMBER_FORMAT, 69) +
                String.format(STRING_FORMAT, STDIN) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 30) + String.format(NUMBER_FORMAT, 140) +
                String.format(STRING_FORMAT, TOTAL);

        assertEquals(sbExpected, result);
    }

    @Test
        // command: wc tmpWcTestFolder/test1.txt wc -
    void testWc_singleFileAndNonExistentFilesAndStandardInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_3); // NOPMD
        String result = wcApplication.countFromFileAndStdin(true, true, true, input, FILE_PATH_1, NON_EXISTENT_FILE, STDIN);
        IOUtils.closeInputStream(input);

        String sbExpected = String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 24) +
                String.format(STRING_FORMAT, FILE_PATH_1) + StringUtils.STRING_NEWLINE +
                STRING_WC + NON_EXISTENT_FILE + ERR_NOT_FOUND + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) + String.format(NUMBER_FORMAT, 14) + String.format(NUMBER_FORMAT, 69) +
                String.format(STRING_FORMAT, STDIN) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) + String.format(NUMBER_FORMAT, 20) + String.format(NUMBER_FORMAT, 93) +
                String.format(STRING_FORMAT, TOTAL);

        assertEquals(sbExpected, result);
    }

    @Test
        // command: wc - - - < tmpWcTestFolder/test1.txt
    void testWc_InputRedirectionWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        String result = wcApplication.countFromFileAndStdin(true, true, true, input, STDIN, STDIN, STDIN);
        IOUtils.closeInputStream(input);

        String sbExpected = String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 24) +
                String.format(STRING_FORMAT, STDIN) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) + String.format(NUMBER_FORMAT, 0) + String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, STDIN) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) + String.format(NUMBER_FORMAT, 0) + String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, STDIN) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 24) +
                String.format(STRING_FORMAT, TOTAL);

        assertEquals(sbExpected, result);
    }

    @Test
        // command: wc tmpWcTestFolder wc - tmpWcTestFolder/test1.txt
    void testWc_argumentsFromDirectoryNonExistentFileStdInAndSingleFileWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        String result = wcApplication.countFromFileAndStdin(true, true, true, input, TEST_FOLDER_NAME, NON_EXISTENT_FILE, STDIN, FILE_PATH_2);
        IOUtils.closeInputStream(input);

        String sbExpected = STRING_WC + TEST_FOLDER_NAME + ERR_IS_DIRECTORY + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) + String.format(NUMBER_FORMAT, 0) + String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, TEST_FOLDER_NAME) + StringUtils.STRING_NEWLINE +
                STRING_WC + NON_EXISTENT_FILE + ERR_NOT_FOUND + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 24) +
                String.format(STRING_FORMAT, STDIN) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) + String.format(NUMBER_FORMAT, 10) + String.format(NUMBER_FORMAT, 47) +
                String.format(STRING_FORMAT, FILE_PATH_2) + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) + String.format(NUMBER_FORMAT, 16) + String.format(NUMBER_FORMAT, 71) +
                String.format(STRING_FORMAT, TOTAL);

        assertEquals(sbExpected, result);
    }

    @Test
        // command: wc tmpWcTestFolder/test1.txt -l
    void testWc_fileInputWithLineFlag_shouldShowLinesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(false, true, false, FILE_PATH_1);

        String sbExpected = String.format(NUMBER_FORMAT, 1) +
                String.format(STRING_FORMAT, FILE_PATH_1);

        assertEquals(sbExpected, result);
    }

    @Test
        // command: wc tmpWcTestFolder/test1.txt -w
    void testWc_fileInputWithWordFlag_shouldShowWordsWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(true, false, false, FILE_PATH_1);

        String sbExpected = String.format(NUMBER_FORMAT, 24) +
                String.format(STRING_FORMAT, FILE_PATH_1);

        assertEquals(sbExpected, result);
    }

    @Test
        // command: wc tmpWcTestFolder/test1.txt -c
    void testWc_fileInputWithByteFlag_shouldShowBytesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(false, false, true, FILE_PATH_1);

        String sbExpected = String.format(NUMBER_FORMAT, 6) +
                String.format(STRING_FORMAT, FILE_PATH_1);

        assertEquals(sbExpected, result);
    }

    @Test
        // command: wc tmpWcTestFolder
    void testWc_inputFileIsDirectory_shouldDisplayIsDirectoryError() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, TEST_FOLDER_NAME);

        String sbExpected = STRING_WC + TEST_FOLDER_NAME + ERR_IS_DIRECTORY + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) + String.format(NUMBER_FORMAT, 0) + String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, TEST_FOLDER_NAME);

        assertEquals(sbExpected, result);
    }

    @Test
        // command: wc nonExistentName
    void testWc_inputNonExistentFileOrDirectory_shouldDisplayNoSuchFileOrDirectory() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, NON_EXISTENT_FILE);

        assertEquals(STRING_WC + NON_EXISTENT_FILE + ERR_NOT_FOUND, result);
    }

    @Test
    void testWc_nullInputStream_shouldThrowException(){
        assertThrows(WcException.class, () -> wcApplication.countFromStdin(true, true, true, null), ERR_NULL_STREAMS);
    }

    @Test
    void testWc_nullFileNames_shouldThrowException(){
        assertThrows(WcException.class, () -> wcApplication.countFromFiles(true, true, true, null), ERR_NULL_FILES);
    }

    @Test
    void testWc_nullFileNamesAndInputStream_shouldThrowException() throws ShellException {
        assertThrows(WcException.class, () -> wcApplication.countFromFileAndStdin(true, true, true, null, new String[]{}), ERR_NULL_STREAMS);
        InputStream input = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        assertThrows(WcException.class, () -> wcApplication.countFromFileAndStdin(true, true, true, input, null), ERR_NULL_FILES);
        IOUtils.closeInputStream(input);
    }

    // TODO:
    //  test redirection
    //  test with different flags combination (many duplicates, single ones)
    //  test with globing
    //  test with multiple stdIn arguments
}
