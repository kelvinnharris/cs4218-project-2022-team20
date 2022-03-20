package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.ECHO_GREP_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class EchoGrepIntegrationTest {

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = ECHO_GREP_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;
    public static final String FILE2_PATH = TEST_PATH + FILE2_NAME;

    public static final String NE_FILE_NAME = "nonExistent.txt";
    public static final String LINES1_TEXT1 = "The first file";
    public static final String LINES1_TEXT2 = "Thee second line";
    public static final String LINES1_TEXT3 = "1000";
    public static final String[] LINES1 = {LINES1_TEXT1, LINES1_TEXT2, LINES1_TEXT3};
    public static final String[] LINES2 = {"The first part", "THE SECOND parts", "10"};
    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;


    @BeforeEach
    void setUpEach() {
        stdOut = new ByteArrayOutputStream();
        Environment.currentDirectory = TEST_PATH;
    }


    @BeforeAll
    static void setUp() throws IOException {
        deleteDir(new File(TEST_PATH));
        shell = new ShellImpl();

        Files.createDirectories(Paths.get(TEST_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }


    @Test
    void testEchoGrepParseAndEvaluate_echoOutputOfGrepInsensitive_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo \"`grep \"The\" %s`\"", FILE1_PATH);
        String expectedOutput = "The first file" + STRING_NEWLINE + "Thee second line" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_echoOutputOfGrepSensitive_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo \"`grep \"The\" %s`\"", FILE2_PATH);
        String expectedOutput = "The first part" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_grepOnInvalidFileNameEchoed_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo \"`grep \"The\" %s`\"", NE_FILE_NAME);
        String expectedOutput = "grep: " + NE_FILE_NAME + ": No such file or directory" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_echoEmptyOutputOfGrep_shouldReturnNewline() throws Exception {
        String commandString = String.format("echo \"`grep \"12345\" %s`\"", FILE1_PATH);
        String expectedOutput = STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_echoOutputOfGrepRegex_shouldReturnNewline() throws Exception {
        String commandString = "echo \"`grep \"^[0-9]*$\" " + FILE1_PATH + "`\"";
        String expectedOutput = LINES1_TEXT3 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_grepFromEchoAsStdinMatch_shouldReturnCorrectOutput() throws Exception {
        String commandString = "echo \"The first file\" | grep \"The\"";
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_grepFromEchoAsStdinNoMatch_shouldReturnEmptyString() throws Exception {
        String commandString = "echo|grep \"The\"";
        String expectedOutput = "";
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_echoAndGrepSeparately_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo \"Hello world\"; grep \"The\" %s", FILE1_PATH);
        String expectedOutput = "Hello world" + STRING_NEWLINE + LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_echoDoesNotAcceptStdin_shouldReturnEmptyString() throws Exception {
        String commandString = String.format("grep \"The\" %s > echo", FILE1_PATH);
        String expectedOutput = "";
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_grepParamFromInputRedirectionEcho_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("grep -icH \"tH*\" %s < echo %s", FILE1_PATH, FILE2_PATH);
        String expectedOutput = FILE1_PATH + CHAR_COLON + "2" + STRING_NEWLINE + FILE2_PATH + CHAR_COLON + "2" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_grepParamFromValidInput_shouldReturnPartialCorrectOutput() throws Exception {
        String commandString = "grep `echo \"The\"` `echo \"file1.txt\"`;";
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_echoAndGrepSeparatelyInvalid_shouldReturnPartialCorrectOutput() throws Exception {
        String commandString = "echo \"Hello world\"; grep";
        String expectedOutput = "Hello world" + STRING_NEWLINE + String.format("grep: %s", ERR_SYNTAX) + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrepParseAndEvaluate_grepOnInvalidInputFromEcho_shouldReturnError() {
        String commandString = "grep `echo \"\"`;";
        assertThrows(GrepException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }
}

