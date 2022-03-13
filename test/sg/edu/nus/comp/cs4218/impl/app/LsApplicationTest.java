package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.LS_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

class LsApplicationTest {
    /* before each file path:
        > folder1
           > file1.txt
           > file2.iml
           > folder2
        > folder3
           > folder4
              > file3.txt
        > file4.xml
     */

    private static LsApplication lsApplication;
    private static final String NEW_LINE = System.lineSeparator();
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = LS_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FOLDER_1 = "folder1";
    private static final String FOLDER_2 = "folder2";
    private static final String FOLDER_3 = "folder3";
    private static final String FOLDER_4 = "folder4";
    private static final String FILE_1 = "file1.txt";
    private static final String FILE_2 = "file2.iml";
    private static final String FILE_3 = "file3.txt";
    private static final String FILE_4 = "file4.xml";

    @BeforeAll
    static void setUp() throws IOException {
        lsApplication = new LsApplication();
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_1));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_2));
        Files.createFile(Paths.get(TEST_PATH + FILE_4));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4 + CHAR_FILE_SEP + FILE_3));
    }

    @BeforeEach
    void setUpEach() {
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() throws IOException {
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void listFolderContent_emptyPath_shouldReturnFileNotFound() throws LsException {
        String lsOutput = lsApplication.listFolderContent(false, false, "");
        String expectedOutput = "ls: cannot access '': No such file or directory";
        assertEquals(expectedOutput, lsOutput);
    }

    @Test
    void listFolderContent_fullPath_shouldReturnAllFilesAndDirectories() throws LsException {
        String lsOutput = lsApplication.listFolderContent(false, false, TEST_PATH);
        String expectedOutput = FILE_4 + StringUtils.STRING_NEWLINE + FOLDER_1 + StringUtils.STRING_NEWLINE + FOLDER_3;
        assertEquals(expectedOutput, lsOutput);
    }

    @Test
    void listFolderContent_pathNoArg_shouldReturnAllFilesAndDirectories() throws LsException {
        Environment.currentDirectory = TEST_PATH;
        String lsOutput = lsApplication.listFolderContent(false, false, ".");
        String expectedOutput = FILE_4 + StringUtils.STRING_NEWLINE + FOLDER_1 + StringUtils.STRING_NEWLINE + FOLDER_3;
        assertEquals(expectedOutput, lsOutput);
    }

    @Test
    void listFolderContent_relativePath_shouldReturnAllFilesAndDirectories() throws LsException {
        String path = "." + CHAR_FILE_SEP + TEST_FOLDER_NAME + FOLDER_3 + CHAR_FILE_SEP + ".." + CHAR_FILE_SEP + FOLDER_1 + CHAR_FILE_SEP + ".";
        String lsOutput = lsApplication.listFolderContent(false, false, path);
        String expectedOutput = FILE_1 + StringUtils.STRING_NEWLINE + FILE_2 + StringUtils.STRING_NEWLINE + FOLDER_2;
        assertEquals(expectedOutput, lsOutput);
    }

    @Test
    void listFolderContent_invalidPath_shouldReturnInvalidPathStringError() throws LsException {
        String path = TEST_FOLDER_NAME + "invalidFile.txt";
        String lsOutput = lsApplication.listFolderContent(false, false, path);
        String expectedOutput = "ls: cannot access '" + TEST_FOLDER_NAME + "invalidFile.txt': No such file or directory";
        assertEquals(expectedOutput, lsOutput);
    }

    @Test
    void listFolderContent_validMultiplePath_shouldReturnValidPathString() throws LsException {
        String validPath1 = TEST_FOLDER_NAME + FOLDER_1;
        String validPath2 = TEST_FOLDER_NAME + FOLDER_1 + CHAR_FILE_SEP + FILE_1;
        String validPath3 = TEST_FOLDER_NAME + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4;
        String lsOutput = lsApplication.listFolderContent(false, false, validPath1, validPath2, validPath3);
        String expectedOutput = TEST_FOLDER_NAME + FOLDER_1 + ":" + NEW_LINE + FILE_1 + NEW_LINE + FILE_2 + NEW_LINE + FOLDER_2 + NEW_LINE + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_1 + CHAR_FILE_SEP + FILE_1 + NEW_LINE + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4 + ":" + NEW_LINE + FILE_3;
        assertEquals(expectedOutput, lsOutput);
    }

    @Test
    void listFolderContent_validInvalidMultiplePath_shouldReturnValidInvalidPathString() throws LsException {
        String invalidPath1 = TEST_FOLDER_NAME + "folder5";
        String invalidPath2 = TEST_FOLDER_NAME + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2 + CHAR_FILE_SEP + "hello.txt";
        String validPath3 = TEST_FOLDER_NAME + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4 + CHAR_FILE_SEP + FILE_3;
        String lsOutput = lsApplication.listFolderContent(false, false, invalidPath1, invalidPath2, validPath3);
        String expectedOutput = "ls: cannot access '" + TEST_FOLDER_NAME + "folder5': No such file or directory" + NEW_LINE +
                "ls: cannot access '" + TEST_FOLDER_NAME + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2 + CHAR_FILE_SEP + "hello.txt': No such file or directory" + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4 + CHAR_FILE_SEP + FILE_3;
        assertEquals(expectedOutput, lsOutput);
    }

    @Test
    void listFolderContent_fileWithLongPath_shouldReturnChosenFiles() throws LsException {
        String validPath = TEST_FOLDER_NAME + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4 + CHAR_FILE_SEP + FILE_3;
        String lsOutput = lsApplication.listFolderContent(false, false, validPath);
        String expectedOutput = TEST_FOLDER_NAME + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4 + CHAR_FILE_SEP + FILE_3;
        assertEquals(expectedOutput, lsOutput);
    }

    @Test
    void listFolderContent_fileOnCurrentDirectory_shouldReturnChosenFiles() throws LsException {
        Environment.currentDirectory = TEST_PATH;
        String lsOutput = lsApplication.listFolderContent(false, false, FILE_4);
        String expectedOutput = FILE_4;
        assertEquals(expectedOutput, lsOutput);
    }

    @Test
    void listFolderContent_recursiveOnly_shouldReturnFilesAndDirectoriesRecursively() throws LsException {
        String recurOnlyOutput = lsApplication.listFolderContent(true, false, TEST_PATH);
        String expectedOutput = LS_FOLDER + ":" + NEW_LINE + FILE_4 + NEW_LINE + FOLDER_1 + NEW_LINE + FOLDER_3 + NEW_LINE + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_1 + ":" + NEW_LINE + FILE_1 + NEW_LINE + FILE_2 + NEW_LINE + FOLDER_2 + NEW_LINE + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2 + ":" + NEW_LINE + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_3 + ":" + NEW_LINE + FOLDER_4 + NEW_LINE + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4 + ":" + NEW_LINE + FILE_3;
        assertEquals(expectedOutput, recurOnlyOutput);
    }

    @Test
    void listFolderContent_recursiveOnlyFileNames_shouldReturnFiles() throws LsException {
        String recurOnlyOutput = lsApplication.listFolderContent(true, false, TEST_FOLDER_NAME + FILE_4);
        String expectedOutput = TEST_FOLDER_NAME + FILE_4;
        assertEquals(expectedOutput, recurOnlyOutput);
    }

    @Test
    void listFolderContent_recursiveOnlyEmptyFolder_shouldNotRecurseInfinitely() throws LsException {
        assertDoesNotThrow(() -> lsApplication.listFolderContent(true, false));
    }

    @Test
    void listFolderContent_recursiveSort_shouldSortAndReturnFilesAndDirectoriesRecursively() throws LsException {
        String recurSortOutput = lsApplication.listFolderContent(true, true, TEST_PATH);
        String expectedOutput = "tmpLsTestFolder:" + NEW_LINE + FOLDER_1 + NEW_LINE + FOLDER_3 + NEW_LINE + FILE_4 + NEW_LINE + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_1 + ":" + NEW_LINE + FOLDER_2 + NEW_LINE + FILE_2 + NEW_LINE + FILE_1 + NEW_LINE + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2 + ":" + NEW_LINE + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_3 + ":" + NEW_LINE + FOLDER_4 + NEW_LINE + NEW_LINE +
                TEST_FOLDER_NAME + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4 + ":" + NEW_LINE + FILE_3;
        assertEquals(expectedOutput, recurSortOutput);
    }

    @Test
    void run_passNullStdin_shouldPassed() {
        String[] emptyArgs = new String[]{};
        assertDoesNotThrow(() -> lsApplication.run(emptyArgs, null, System.out));
    }

    @Test
    void run_passNullArgs_shouldThrowLsException() {
        assertThrows(LsException.class, () -> lsApplication.run(null, System.in, System.out));
    }

    @Test
    void run_passValidArgs_shouldThrowLsException() {
        String[] emptyArgs = new String[]{TEST_PATH, "-RX"};
        assertDoesNotThrow(() -> lsApplication.run(emptyArgs, System.in, System.out));
    }

    @Test
    void run_passInvalidArgs_shouldThrowLsException() {
        String[] emptyArgs = new String[]{TEST_PATH, "-abc"};
        assertThrows(LsException.class, () -> lsApplication.run(emptyArgs, System.in, System.out));
    }

    @Test
    void run_passNullStdout_shouldThrowLsException() {
        String[] emptyArgs = new String[]{};
        assertThrows(LsException.class, () -> lsApplication.run(emptyArgs, System.in, null));
    }
}