package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class EchoCatIntegrationTest {

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpEchoCatTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE2_PATH = TEST_PATH + FILE2_NAME;

    private static final String FIRST1 = "first1";
    private static final String FIRST2 = "first2";
    private static final String SECOND1 = "second1";
    private static final String SECOND2 = "second2";
    public static final String SPACES = "     ";


    public static final String[] LINES1 = {FIRST1, FIRST2};
    public static final String[] LINES2 = {SECOND1, SECOND2};


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
        Files.deleteIfExists(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        appendToFile(Paths.get(FILE1_PATH), LINES1);
        Files.deleteIfExists(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        appendToFile(Paths.get(FILE2_PATH), LINES2);
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }


    @Test
    void testEchoCatParseAndEvaluate_echoCatWithDoubleQuotes_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo \"`cat %s %s`\"", FILE1_PATH, FILE2_PATH);
        String expected = FIRST1 + STRING_NEWLINE + FIRST2 + STRING_NEWLINE + SECOND1 + STRING_NEWLINE + SECOND2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testEchoCatParseAndEvaluate_echoCatWithoutDoubleQuotes_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo `cat %s %s`", FILE1_PATH, FILE2_PATH);
        String expected = FIRST1 + " " + FIRST2 + " " + SECOND1 + " " + SECOND2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testEchoCatParseAndEvaluate_echoCatWithPrefixWithDoubleQuotes_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo \"`cat -n %s %s`\"", FILE1_PATH, FILE2_PATH);
        String expected = SPACES + "1 " + FIRST1 + STRING_NEWLINE + SPACES + "2 " + FIRST2 + STRING_NEWLINE + SPACES + "3 " + SECOND1 + STRING_NEWLINE + SPACES + "4 " + SECOND2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testEchoCatParseAndEvaluate_echoCatWithPrefixWithoutDoubleQuotes_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo `cat -n %s %s`", FILE1_PATH, FILE2_PATH);
        String expected = "1 " + FIRST1 + " " + "2 " + FIRST2 + " " + "3 " + SECOND1 + " " + "4 " + SECOND2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testEchoCatParseAndEvaluate_catFromEcho_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat < `echo %s`", FILE1_PATH);
        String expected = FIRST1 + STRING_NEWLINE + FIRST2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testEchoCatParseAndEvaluate_echoThenCat_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo %s %s| cat", FILE1_PATH, FILE2_PATH);
        String expected = FILE1_PATH + " " + FILE2_PATH + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testEchoCatParseAndEvaluate_echoEmptyThenCat_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo | cat");
        String expected = STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testEchoCatParseAndEvaluate_echoAndCat_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo %s %s; cat %s %s", FILE1_PATH, FILE2_PATH, FILE1_PATH, FILE2_PATH);
        String expected = FILE1_PATH + " " + FILE2_PATH + STRING_NEWLINE + FIRST1 + STRING_NEWLINE + FIRST2 + STRING_NEWLINE + SECOND1 + STRING_NEWLINE + SECOND2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testEchoCatParseAndEvaluate_catErrorAndEcho_shouldReturnPartialCorrectOutput() throws Exception {
        String commandString = String.format("cat -z ; echo %s", FILE1_PATH);
        String expected = "cat: invalid option -- 'z'" + STRING_NEWLINE + FILE1_PATH + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testEchoCatParseAndEvaluate_echoThenCatError_shouldReturnPartialCorrectOutput() throws Exception {
        String commandString = String.format("echo %s | cat -z", FILE1_PATH);
        assertThrows(CatException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }
}
