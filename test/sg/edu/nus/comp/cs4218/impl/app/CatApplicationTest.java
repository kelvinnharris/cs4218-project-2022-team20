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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

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
    private static final String FILE_PATH_1 = TEST_FOLDER_NAME + FILE_NAME_1;
    private static final String FILE_NAME_2 = "test2.txt";
    private static final String FILE_PATH_2 = TEST_FOLDER_NAME + FILE_NAME_2;
    private static final String STD_IN_TXT = "stdIn.txt";
    private static final String FILE_PATH_STDIN = TEST_FOLDER_NAME + STD_IN_TXT;

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        TestUtils.createFile(FILE_PATH_1, "This is WC Test file 1"); // NOPMD

        String sbContentFile2 = "This is WC Test file 2" + StringUtils.STRING_NEWLINE + // NOPMD
                "Multiple Lines: " + StringUtils.STRING_NEWLINE + // NOPMD
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
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    @Test
        // command: cat tmpCatTestFolder/test1.txt
    void testCat_fileInputWithoutFlag_shouldShowContentsInFile() throws Exception {
        String result = catApplication.catFiles(false, FILE_PATH_1);

        assertEquals("This is WC Test file 1", result);
    }

    @Test
        // command: cat tmpCatTestFolder/test1.txt tmpCatTestFolder/test2.txt
    void testCat_multipleFilesInputWithoutFlag_shouldShowContentsInAllFiles() throws Exception {
        String result = catApplication.catFiles(false, FILE_PATH_1, FILE_PATH_2);

        String sbExpected = "This is WC Test file 1" + StringUtils.STRING_NEWLINE +
                "This is WC Test file 2" + StringUtils.STRING_NEWLINE +
                "Multiple Lines: " + StringUtils.STRING_NEWLINE +
                "a" + StringUtils.STRING_NEWLINE + "b" + StringUtils.STRING_NEWLINE +
                "c" + StringUtils.STRING_NEWLINE + "d";

        assertEquals(sbExpected, result);
    }

    @Test
        // command: cat
    void testCat_noFileArgumentsWithoutFlag_shouldShowContentsInAllFiles() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        String result = catApplication.catStdin(false, inputStream);
        IOUtils.closeInputStream(inputStream);

        assertEquals("This is WC Test file 1", result);
    }

    @Test
        // command: cat -
    void testCat_stdInWithoutFlag_shouldShowContentsInAllFiles() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        String result = catApplication.catFileAndStdin(false, inputStream, STDIN);
        IOUtils.closeInputStream(inputStream);

        assertEquals("This is WC Test file 1", result);
    }

    @Test
    // command: cat -n tmpCatTestFolder/test1.txt tmpCatTestFolder/test2.txt
    void testCat_multipleFilesInputWithFlag_shouldShowContentsInAllFilesWithNumbers() throws Exception {
        String result = catApplication.catFiles(true, FILE_PATH_1, FILE_PATH_2);

        String sbExpected = String.format(NUMBER_FORMAT, 1) +
                "This is WC Test file 1" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) +
                "This is WC Test file 2" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) +
                "Multiple Lines: " + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                "a" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 5) +
                "b" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 6) +
                "c" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 7) + "d";

        assertEquals(sbExpected, result);
    }

    @Test
        // command: cat -n tmpCatTestFolder/test1.txt - tmpCatTestFolder/test2.txt
    void testCat_multipleFilesInputAndStdInWithFlag_shouldShowContentsInAllFilesWithNumbers() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(FILE_PATH_STDIN); // NOPMD
        String result = catApplication.catFileAndStdin(true, inputStream, FILE_PATH_1, STDIN, FILE_PATH_2);
        IOUtils.closeInputStream(inputStream);

        String sbExpected = String.format(NUMBER_FORMAT, 1) +
                "This is WC Test file 1" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 2) +
                "This is from stdIn" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) +
                "This is from stdIn line 2" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 4) +
                "This is WC Test file 2" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 5) +
                "Multiple Lines: " + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 6) +
                "a" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 7) +
                "b" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 8) +
                "c" + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 9) + "d";

        assertEquals(sbExpected, result);
    }

    @Test
        // command: cat -n cat tmpCatTestFolder/test2.txt
    void testCat_multipleFilesWithNonExistentFileWithFlag_shouldShowContentsInAllFilesWithNumbers() throws Exception {
        String result = catApplication.catFiles(true, NON_EXISTENT_FILE, FILE_PATH_1);

        String sbExpected = "cat: " + NON_EXISTENT_FILE + ERR_NOT_FOUND + StringUtils.STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 1) +
                "This is WC Test file 1";

        assertEquals(sbExpected, result);
    }

    @Test
        // command: cat -n cat tmpCatTestFolder/test2.txt
    void testCat_multipleFilesWithDirectoryFileWithFlag_shouldShowContentsInAllFilesWithNumbers() throws Exception {
        String result = catApplication.catFiles(true, FILE_PATH_1, TEST_FOLDER_NAME);

        String sbExpected = String.format(NUMBER_FORMAT, 1) +
                "This is WC Test file 1" + StringUtils.STRING_NEWLINE +
                "cat: " + TEST_FOLDER_NAME + ERR_IS_DIRECTORY;

        assertEquals(sbExpected, result);
    }

    @Test
    void testWc_nullInputStream_shouldThrowException(){
        assertThrows(CatException.class, () -> catApplication.catStdin( true, null), ERR_NULL_STREAMS);
    }

    @Test
    void testWc_nullFileNames_shouldThrowException(){
        assertThrows(CatException.class, () -> catApplication.catFiles(true, null), ERR_NULL_FILES);
    }

    @Test
    void testWc_nullFileNamesAndInputStream_shouldThrowException() throws ShellException {
        assertThrows(CatException.class, () -> catApplication.catFileAndStdin(true, null, new String[]{}), ERR_NULL_STREAMS);
        InputStream input = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        assertThrows(CatException.class, () -> catApplication.catFileAndStdin(true, input, null), ERR_NULL_FILES);
        IOUtils.closeInputStream(input);
    }
}
