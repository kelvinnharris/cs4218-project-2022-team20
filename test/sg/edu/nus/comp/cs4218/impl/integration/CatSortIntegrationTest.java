package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class CatSortIntegrationTest {

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCatSortTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE2_PATH = TEST_PATH + FILE2_NAME;
    public static final String SPACES = "     ";


    public static final String[] LINES1 = {"1", "2", "10"};
    public static final String[] LINES2 = {"a", "ab", "AB", "A"};


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
        deleteDir(new File(TEST_PATH));
    }


    @Test
    void testCatSortParseAndEvaluate_catThenSort_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat %s %s | sort", FILE1_PATH, FILE2_PATH);
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
    void testCatSortParseAndEvaluate_sortThenCat_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("sort %s %s | cat", FILE1_PATH, FILE2_PATH);
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
    void testCatSortParseAndEvaluate_catWithPrefixThenSort_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat -n %s %s | sort", FILE1_PATH, FILE2_PATH);
        String expected = SPACES + "1 1" + STRING_NEWLINE +
                SPACES + "2 2" + STRING_NEWLINE +
                SPACES + "3 10" + STRING_NEWLINE +
                SPACES + "4 a" + STRING_NEWLINE +
                SPACES + "5 ab" + STRING_NEWLINE +
                SPACES + "6 AB" + STRING_NEWLINE +
                SPACES + "7 A" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testCatSortParseAndEvaluate_sortThenCatWithPrefix_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("sort %s %s | cat -n ", FILE1_PATH, FILE2_PATH);
        String expected = SPACES + "1 1" + STRING_NEWLINE +
                SPACES + "2 10" + STRING_NEWLINE +
                SPACES + "3 2" + STRING_NEWLINE +
                SPACES + "4 A" + STRING_NEWLINE +
                SPACES + "5 AB" + STRING_NEWLINE +
                SPACES + "6 a" + STRING_NEWLINE +
                SPACES + "7 ab" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testCatSortParseAndEvaluate_sortThenCatWithPrefixDifferentFiles_shouldReturnCorrectOutputFromSecondFile() throws Exception {
        String commandString = String.format("sort %s | cat -n %s", FILE1_PATH, FILE2_PATH);
        String expected = SPACES + "1 a" + STRING_NEWLINE +
                SPACES + "2 ab" + STRING_NEWLINE +
                SPACES + "3 AB" + STRING_NEWLINE +
                SPACES + "4 A" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testCatSortParseAndEvaluate_sortThenMultipleCatWithPrefix_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("sort %s %s | cat -n | cat -n | cat -n ", FILE1_PATH, FILE2_PATH);
        String expected = SPACES + "1 " + SPACES + "1 " + SPACES + "1 1" + STRING_NEWLINE +
                SPACES + "2 " + SPACES + "2 " + SPACES + "2 10" + STRING_NEWLINE +
                SPACES + "3 " + SPACES + "3 " + SPACES + "3 2" + STRING_NEWLINE +
                SPACES + "4 " + SPACES + "4 " + SPACES + "4 A" + STRING_NEWLINE +
                SPACES + "5 " + SPACES + "5 " + SPACES + "5 AB" + STRING_NEWLINE +
                SPACES + "6 " + SPACES + "6 " + SPACES + "6 a" + STRING_NEWLINE +
                SPACES + "7 " + SPACES + "7 " + SPACES + "7 ab" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }


    @Test
    void testCatSortParseAndEvaluate_sortThenCatWithPrefixThenSort_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("sort %s %s | cat -n | sort %s ", FILE1_PATH, FILE2_PATH, FILE1_PATH);
        String expected = "1" + STRING_NEWLINE +
                "10" + STRING_NEWLINE +
                "2" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }


    @Test
    void testCatSortParseAndEvaluate_sortThenCatWithPrefixAndSort_shouldReturnCorrectOutputFromLastSort() throws Exception {
        String commandString = String.format("sort %s %s | cat -n ; sort %s ", FILE1_PATH, FILE2_PATH, FILE1_PATH);
        String expected = SPACES + "1" + " 1" + STRING_NEWLINE +
                SPACES + "2" + " 10" + STRING_NEWLINE +
                SPACES + "3" + " 2" + STRING_NEWLINE +
                SPACES + "4" + " A" + STRING_NEWLINE +
                SPACES + "5" + " AB" + STRING_NEWLINE +
                SPACES + "6" + " a" + STRING_NEWLINE +
                SPACES + "7" + " ab" + STRING_NEWLINE +
                "1" + STRING_NEWLINE +
                "10" + STRING_NEWLINE +
                "2" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testCatSortParseAndEvaluate_sortErrorThenCat_shouldThrowException() {
        String commandString = String.format("sort -z %s %s | cat", FILE1_PATH, FILE2_PATH);
        assertThrows(SortException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }

}
