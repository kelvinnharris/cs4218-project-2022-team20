package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class LsRmIntegrationTest {

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpLsRmTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String FOLDER1_NAME = "folder1";
    public static final String FOLDER1_PATH = TEST_PATH + FOLDER1_NAME;
    private static final String FOLDER2_NAME = "folder2";
    public static final String FOLDER2_PATH = TEST_PATH + FOLDER2_NAME;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;

    @BeforeAll
    static void setUp() throws IOException {
        shell = new ShellImpl();
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
    }


    @BeforeEach
    void setUpEach() throws IOException {
        stdOut = new ByteArrayOutputStream();

        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(FOLDER1_PATH));
        deleteDir(new File(FOLDER2_PATH));
        Files.createDirectories(Paths.get(FOLDER1_PATH));
        Files.createDirectories(Paths.get(FOLDER2_PATH));
        Files.createFile(Paths.get(FOLDER1_PATH + CHAR_FILE_SEP + FILE1_NAME));
        Files.deleteIfExists(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
    }

    @AfterAll
    static void tearDown() {
        deleteDir(new File(TEST_PATH));
    }


    @Test
    void testLsRm_lsThenRm_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("ls %s | rm %s", FILE1_PATH, FILE1_PATH);
        String expected = "";
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testLsRm_lsAndRm_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("ls %s; rm %s", FILE1_PATH, FILE1_PATH);
        String expected = "tmpLsRmTestFolder/file1.txt" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testLsRm_rmThenLs_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("rm %s | ls %s", FILE1_PATH, FILE1_PATH);
        String expected = String.format("ls: cannot access '%s': No such file or directory", TEST_FOLDER_NAME + FILE1_NAME) + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testLsRm_rmAndLs_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("rm %s; ls %s", FILE1_PATH, FILE1_PATH);
        String expected = String.format("ls: cannot access '%s': No such file or directory", TEST_FOLDER_NAME + FILE1_NAME) + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testLsRm_rmNotEmptyDirAndLs_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("rm %s; ls %s", FOLDER1_PATH, FOLDER1_PATH);
        String expected = String.format("rm: cannot remove '%s': Is a directory" + STRING_NEWLINE + "%s", FOLDER1_PATH, FILE1_NAME) + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testLsRm_rmRecursiveAndLs_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("rm -r %s; ls %s", FOLDER1_PATH, FOLDER1_PATH);
        String expected = String.format("ls: cannot access '%s': No such file or directory", TEST_FOLDER_NAME + FOLDER1_NAME) + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testLsRm_LsEmptyDirThenRmEmptyDir_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("ls %s; rm -d %s", FOLDER2_PATH, FOLDER2_PATH);
        String expected = STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }
}
