package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class EchoGrepIntegrationTest {

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    private static final String ROOT_PATH = Environment.currentDirectory;

    public static final String INPUT = "The first file" + STRING_NEWLINE + "The second line" + STRING_NEWLINE + "1000" + STRING_NEWLINE; // NOPMD
    public final InputStream inputStream = new ByteArrayInputStream(INPUT.getBytes());

    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = ROOT_PATH + CHAR_FILE_SEP + FILE1_NAME;
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE2_PATH = ROOT_PATH + CHAR_FILE_SEP + FILE2_NAME;

    public static final String NE_FILE_NAME = "nonExistent.txt";

    public static final String[] LINES1 = {"The first file", "The second line", "1000"};
    public static final String[] LINES2 = {"The second file", "The second line", "10"};
    public static final String PATTERN1 = "The second";
    public static final String PATTERN1_INSEN = "THE SECoND";

    @BeforeAll
    static void setUp() {
        shell = new ShellImpl();
    }

    @BeforeEach
    void setUpEach() throws IOException {
        stdOut = new ByteArrayOutputStream();

        Environment.currentDirectory = ROOT_PATH;
        Files.deleteIfExists(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.deleteIfExists(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(FILE2_PATH));

        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.delete(Paths.get(FILE1_PATH));
        Files.delete(Paths.get(FILE2_PATH));
    }

    static void appendToFile(Path file, String... lines) throws IOException {
        for (String line : lines) {
            Files.write(file, (line + STRING_NEWLINE).getBytes(), APPEND);
        }
    }

    @Test
    void testEchoGrep_echoOutputOfGrep_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo \"`grep \"The\" %s`\"", FILE1_PATH);
        String expectedOutput = "The first file The second line" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrep_echoEmptyOutputOfGrep_shouldReturnNewline() throws Exception {
        String commandString = String.format("echo \"`grep \"12345\" %s`\"", FILE1_PATH);
        String expectedOutput = STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrep_grepFromEchoAsStdinMatch_shouldReturnCorrectOutput() throws Exception {
        String commandString = "echo \"The first file\" | grep \"The\"";
        String expectedOutput = "The first file" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrep_grepFromEchoAsStdinNoMatch_shouldReturnEmptyString() throws Exception {
        String commandString = "echo|grep \"The\"";
        String expectedOutput = "";
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrep_echoAndGrepSeparately_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo \"Hello world\"; grep \"The\" %s", FILE1_PATH);
        String expectedOutput = "Hello world" + STRING_NEWLINE + "The first file" + STRING_NEWLINE + "The second line" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testEchoGrep_echoAndGrepSeparatelyInvalid_shouldReturnPartialCorrectOutput() throws Exception {
        String commandString = "echo \"Hello world\"; grep";
        String expectedOutput = "Hello world" + STRING_NEWLINE + String.format("grep: %s", ERR_SYNTAX) + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }
}

