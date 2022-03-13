package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.RmException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.RM_CD_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class RmCdIntegrationTest {
    /* before each file path:
        > folder1
           > file1.txt
           > folder2
              > file2.txt
        > file3.xml
     */

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = RM_CD_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String NE_FOLDER = "nonExistentFolder";
    private static final String FOLDER_1 = "folder1";
    private static final String FOLDER_2 = "folder2";
    private static final String FILE_1 = "file1.txt";
    private static final String FILE_2 = "file2.txt";
    private static final String FILE_3 = "file3.xml";

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;

    @BeforeEach
    void setUpEach() throws IOException {
        stdOut = new ByteArrayOutputStream();

        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_1));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2 + CHAR_FILE_SEP + FILE_2));
        Files.createFile(Paths.get(TEST_PATH + FILE_3));
        Environment.currentDirectory = TEST_PATH;
    }


    @BeforeAll
    static void setUp() {
        shell = new ShellImpl();
    }

    @AfterAll
    static void tearDown() {
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testRmCdParseAndEvaluate_removeUnrelatedFileAndCdFolder_shouldChangeDirectorySuccessfully() {
        String commandString = "rm " + FILE_3 + "; cd " + FOLDER_1;
        assertDoesNotThrow(() -> shell.parseAndEvaluate(commandString, stdOut));

        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH + FOLDER_1).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testRmCdParseAndEvaluate_removeOnDirectoryAfterCd_shouldRemoveFileSuccessfully() {
        String commandString = "cd " + FOLDER_1 + "; rm " + FILE_1;
        assertDoesNotThrow(() -> shell.parseAndEvaluate(commandString, stdOut));

        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH + FOLDER_1).normalize();
        assertEquals(currentPath, givenPath);

        assertFalse(Files.exists(Paths.get(TEST_PATH + CHAR_FILE_SEP + FOLDER_1 + CHAR_FILE_SEP + FILE_1)));
    }

    @Test
    void testRmCdParseAndEvaluate_cdAfterRemoveEmptyDirectory_shouldCdUnsuccessfully() throws Exception {
        String commandString = String.format("cd %s; rm %s/%s; rm -d %s; cd %s", FOLDER_1, FOLDER_2, FILE_2, FOLDER_2, FOLDER_2);
        String expectedOutput = "cd: " + FOLDER_2 + ": No such file or directory" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());

        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH + FOLDER_1).normalize();
        assertEquals(currentPath, givenPath);

        assertFalse(Files.exists(Paths.get(TEST_PATH + CHAR_FILE_SEP + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2)));
    }

    @Test
    void testRmCdParseAndEvaluate_removeNonEmptyFolderSemicolonCdFolder_shouldReturnRmErrorAndChangeDirectorySuccessfully() throws Exception {
        String commandString = "rm " + FOLDER_1 + "; cd " + FOLDER_1;
        String expectedOutput = "rm: cannot remove '"+ FOLDER_1 +"': Is a directory" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());

        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH + FOLDER_1).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testRmCdParseAndEvaluate_removeNonEmptyFolderPipeCdFolder_shouldThrowRmError() throws Exception {
        String commandString = "rm " + FOLDER_1 + "| cd " + FOLDER_1;
        assertThrows(RmException.class, () -> shell.parseAndEvaluate(commandString, stdOut));

        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testRmCdParseAndEvaluate_removeNonEmptyFolderWithDirectoryFlagPipeCdFolder_shouldThrowRmError() throws Exception {

        Environment.currentDirectory = ROOT_PATH;
        String commandString = "rm " + FOLDER_1 + "| cd " + FOLDER_1;
        assertThrows(RmException.class, () -> shell.parseAndEvaluate(commandString, stdOut));

        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testRmCdParseAndEvaluate_removeNonExistentFolderWithRmFlagsPipeCdFolder_shouldThrowRmError() {
        String commandString = "rm -rd " + NE_FOLDER + "| cd " + FOLDER_1;
        assertThrows(RmException.class, () -> shell.parseAndEvaluate(commandString, stdOut));

        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }
}
