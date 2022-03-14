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
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class CutSortIntegrationTest {

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCutSortTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;


    public static final String[] LINES1 = {"1", "2", "10", "a", "ab", "AB", "A"};

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
    void testCutSort_cutThenSort_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cut -b 1-2 %s | sort", FILE1_PATH);
        String expected = "1" + STRING_NEWLINE +
                "10" + STRING_NEWLINE +
                "2" + STRING_NEWLINE +
                "A" + STRING_NEWLINE +
                "AB" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "ab" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testCutSort_SortThenCut_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("sort %s | cut -b 1-2", FILE1_PATH);
        String expected = "1" + STRING_NEWLINE +
                "10" + STRING_NEWLINE +
                "2" + STRING_NEWLINE +
                "A" + STRING_NEWLINE +
                "AB" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "ab" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testCutSort_cutAndSortSeparately_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cut -b 1 %s; sort %s", FILE1_PATH, FILE1_PATH);
        String expected = "1" + STRING_NEWLINE +
                "2" + STRING_NEWLINE +
                "1" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "A" + STRING_NEWLINE +
                "A" + STRING_NEWLINE +
                "1" + STRING_NEWLINE +
                "10" + STRING_NEWLINE +
                "2" + STRING_NEWLINE +
                "A" + STRING_NEWLINE +
                "AB" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "ab" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testCutSort_sortAndCutSeparately_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("sort %s; cut -b 1 %s", FILE1_PATH, FILE1_PATH);
        String expected = "1" + STRING_NEWLINE +
                "10" + STRING_NEWLINE +
                "2" + STRING_NEWLINE +
                "A" + STRING_NEWLINE +
                "AB" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "ab" + STRING_NEWLINE  +
                "1" + STRING_NEWLINE +
                "2" + STRING_NEWLINE +
                "1" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "A" + STRING_NEWLINE +
                "A" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testCutSort_sortInvalidAndCutValid_shouldReturnPartialCorrectOutput() throws Exception {
        String commandString = String.format("sort -s %s; cut -b 1 %s", FILE1_PATH, FILE1_PATH);
        String expected = "sort: invalid option -- 's'" + STRING_NEWLINE +
                "1" + STRING_NEWLINE +
                "2" + STRING_NEWLINE +
                "1" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "A" + STRING_NEWLINE +
                "A" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testCutSort_multipleCutThenMultipleSort_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cut -b 1-2 %s | cut -b 1 | sort | sort -nrf", FILE1_PATH);
        String expected = "a" + STRING_NEWLINE +
                "a" + STRING_NEWLINE +
                "A" + STRING_NEWLINE +
                "A" + STRING_NEWLINE +
                "2" + STRING_NEWLINE +
                "1" + STRING_NEWLINE +
                "1" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }
}
