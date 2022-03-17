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
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class EchoRmIntegrationTest {

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpEchoRmTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    public static final String FOLDER1 = "folder1.txt";
    public static final String FOLDER1_PATH = TEST_PATH + FOLDER1;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE2_PATH = TEST_PATH + FILE2_NAME;

    private static final String FIRST = "first";
    private static final String SECOND = "second";

    public static final String[] LINES1 = {FIRST};
    public static final String[] LINES2 = {SECOND};


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
        Files.deleteIfExists(Paths.get(FOLDER1_PATH));
        Files.createDirectories(Paths.get(FOLDER1_PATH));
        Files.deleteIfExists(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        appendToFile(Paths.get(FILE1_PATH), LINES1);
        Files.deleteIfExists(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        appendToFile(Paths.get(FILE2_PATH), LINES2);
    }

    @AfterAll
    static void tearDown() {
        deleteDir(new File(TEST_PATH));
    }


    @Test
    void testEchoRmParseAndEvaluate_echoRm_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo \"`rm %s`\"", FILE1_PATH);
        String expected = "" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
        File tempFile = new File(FILE1_PATH);
        assertFalse(tempFile.exists());
    }

    @Test
    void testEchoRmParseAndEvaluate_echoRmIsDir_shouldThrowException() {
        String commandString = String.format("echo \"`rm %s`\"", FOLDER1_PATH);
        assertThrows(RmException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
        File tempFile = new File(FOLDER1_PATH);
        assertTrue(tempFile.exists());
    }

    @Test
    void testEchoRmParseAndEvaluate_echoAndRm_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo %s; rm %s", FILE2_PATH, FILE2_PATH);
        String expected = FILE2_PATH + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
        File tempFile = new File(FILE2_PATH);
        assertFalse(tempFile.exists());
    }

    @Test
    void testEchoRmParseAndEvaluate_rmAndEcho_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("rm %s; echo %s", FILE2_PATH, FILE2_PATH);
        String expected = FILE2_PATH + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
        File tempFile = new File(FILE2_PATH);
        assertFalse(tempFile.exists());
    }

    @Test
    void testEchoRmParseAndEvaluate_rmFromEcho_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("rm \"`echo %s`\"", FILE2_PATH);
        String expected = "";
        File tempFile = new File(FILE2_PATH);
        assertTrue(tempFile.exists());
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
        assertFalse(tempFile.exists());
    }

}
