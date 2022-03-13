package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class GrepCutIntegrationTest {

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpGrepCutTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FIRST_LINE = "first line";
    private static final String SECOND_LINE = "second line";
    private static final String LINE = "line";
    private static final String LINE_CASE_INSENSITIVE = "LiNe";
    private static final String LINES = "lines";






    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;


    public static final String[] LINES1 = {FIRST_LINE, SECOND_LINE, LINE, LINE_CASE_INSENSITIVE, LINES};

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
    }

    @AfterAll
    static void tearDown() {
        deleteDir(new File(TEST_PATH));
    }


    @Test
    void testGrepCut_grepThenCut_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("grep line %s | cut -b 1-15", FILE1_PATH);
        String expected = FIRST_LINE + STRING_NEWLINE +
                SECOND_LINE + STRING_NEWLINE +
                LINE + STRING_NEWLINE +
                LINES + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testGrepCut_grepCaseInsensitiveThenCut_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("grep -i line %s | cut -b 1-15", FILE1_PATH);
        String expected = FIRST_LINE + STRING_NEWLINE +
                SECOND_LINE + STRING_NEWLINE +
                LINE + STRING_NEWLINE +
                LINE_CASE_INSENSITIVE + STRING_NEWLINE +
                LINES + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testGrepCut_grepCountThenCut_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("grep -c line %s | cut -b 1-15", FILE1_PATH);
        String expected = "4" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testGrepCut_grepWithFileNameThenCut_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("grep -H line %s | cut -c 1-1000", FILE1_PATH);
        String expected = FILE1_PATH + ": " + FIRST_LINE + STRING_NEWLINE +
                FILE1_PATH + ": " + SECOND_LINE + STRING_NEWLINE +
                FILE1_PATH + ": " + LINE + STRING_NEWLINE +
                FILE1_PATH + ": " + LINES + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testGrepCut_grepThenCutWithNoIndex_shouldThrowException() {
        String commandString = String.format("grep line %s | cut -c", FILE1_PATH);
        assertThrows(CutException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
        String expected = "";
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testGrepCut_cutWithNoIndexThenGrep_shouldThrowExceptionAndTerminate() {
        String commandString = String.format("cut -c | grep line %s", FILE1_PATH);
        assertThrows(CutException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
        String expected = "";
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testGrepCut_cutWithNoIndexAndGrep_shouldReturnExceptionMessageAndOutput() throws FileNotFoundException, AbstractApplicationException, ShellException {
        String commandString = String.format("cut -c ; grep line %s", FILE1_PATH);
        shell.parseAndEvaluate(commandString, stdOut);
        String expected = "cut: option requires an argument -- 'c'" + STRING_NEWLINE +
                FIRST_LINE + STRING_NEWLINE +
                SECOND_LINE + STRING_NEWLINE +
                LINE + STRING_NEWLINE +
                LINES + STRING_NEWLINE;
        assertEquals(expected, stdOut.toString());
    }
}
