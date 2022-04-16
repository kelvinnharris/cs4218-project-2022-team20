package sg.edu.nus.comp.cs4218.impl.Hackathon;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class HackathonBugs {
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpHackathonTestFolder" + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FOLDER_1 = "folder1";
    private static final String FILE_1 = "file1.txt";
    private static final String FILE_2 = "file2.txt";
    private static final String FILE_3 = "file3.txt";
    private static final String FILE_4 = "file4.txt";
    private static final String FILE_5 = "file5.txt";
    private static final String FILE_6 = "file6.txt";
    private static final String FILE_7 = "file7.txt";

    public static final String[] LINES = {"Alice", "Alice"};
    public static final String[] LINES4 = {"", ""};



    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;

    @BeforeEach
    void setUpEach() throws IOException {
        stdOut = new ByteArrayOutputStream();

        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_1));
        Files.createFile(Paths.get(TEST_PATH + FILE_1));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_2));
        Files.createFile(Paths.get(TEST_PATH + FILE_4));
        Files.createFile(Paths.get(TEST_PATH + FILE_6));
        appendToFile(Paths.get(TEST_PATH + FILE_1), LINES);
        appendToFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_2), LINES);
        appendToFile(Paths.get(TEST_PATH + FILE_4), LINES4);
        Environment.currentDirectory = TEST_PATH;
    }

    @BeforeAll
    static void setUp() {
        shell = new ShellImpl();
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testUniqParseAndEvaluateFromP6_uniqOutputFileNoLineBreak_shouldReturnNoLineBreakInStdOut() {
        String commandString = "uniq " + FILE_1 + CHAR_SPACE + FILE_2;
        assertDoesNotThrow(() -> shell.parseAndEvaluate(commandString, stdOut));
        File outputFile = new File(TEST_PATH + FILE_2);
        assertTrue(outputFile.exists());
        assertEquals("", stdOut.toString());
    }

    @Test
    void testCdUniqParseAndEvaluateFromP7_uniqOutputFileRelativePath_shouldCreateOutputFile() {
        String commandString = "cd " + FOLDER_1 + CHAR_SEMICOLON + " uniq " + FILE_2 + CHAR_SPACE + FILE_3;
        assertDoesNotThrow(() -> shell.parseAndEvaluate(commandString, stdOut));
        File outputFile = new File(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_3);
        assertTrue(outputFile.exists());
    }

    @Test
    void testCutParseAndEvaluateFromP15_cutWithoutExplicitEndIndex_shouldCutUntilEnd() {
        String commandString = "cut -b 1- " + FILE_1;
        assertDoesNotThrow(() -> shell.parseAndEvaluate(commandString, stdOut));
        assertEquals("Alice" + STRING_NEWLINE + "Alice" + STRING_NEWLINE, stdOut.toString());
    }

    @Test
    void testUniqParseAndEvaluateFromP43_uniqThreeOrMoreArgs_shouldThrowError() {
        String commandString = "uniq - - - -";
        assertThrows(UniqException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }

    @Test
    void testSortParseAndEvaluateFromS11_sortFromFileWithNewline_shouldReturnNewline() {
        String commandString = "sort " + FILE_4;
        assertDoesNotThrow(() -> shell.parseAndEvaluate(commandString, stdOut));
        assertEquals(STRING_NEWLINE + STRING_NEWLINE, stdOut.toString());
    }

    @Test
    void testRmParseAndEvaluateFromS12_rmInvalidFileThenValidFile_shouldRemoveValidFile() throws Exception {
        String commandString = "rm " + FILE_5 + CHAR_SPACE + FILE_6;
        shell.parseAndEvaluate(commandString, stdOut);
        File outputFile = new File(TEST_PATH + FILE_6);
        assertFalse(outputFile.exists());
    }

    @Test
    void testUniqParseAndEvaluateFromS13_uniqOutputFileNoLineBreak_shouldReturnNoLineBreakInStdOut() {
        String commandString = "uniq " + FILE_1 + CHAR_SPACE + FILE_7;
        assertDoesNotThrow(() -> shell.parseAndEvaluate(commandString, stdOut));
        File outputFile = new File(TEST_PATH + FILE_7);
        assertTrue(outputFile.exists());
        assertEquals("", stdOut.toString());
    }

    @Test
    void testCdUniqParseAndEvaluateFromS14_uniqOutputFileRelativePath_shouldCreateOutputFile() {
        String commandString = "cd " + FOLDER_1 + CHAR_SEMICOLON + " uniq " + FILE_2 + CHAR_SPACE + FILE_4;
        assertDoesNotThrow(() -> shell.parseAndEvaluate(commandString, stdOut));
        File outputFile = new File(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_4);
        assertTrue(outputFile.exists());
    }

    @Test
    void testUniqParseAndEvaluateFromS15_uniqThreeOrMoreArgs_shouldThrowError() {
        String commandString = "uniq - - - -";
        assertThrows(UniqException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }

    @Test
    void testUniqParseAndEvaluateFromS16_uniqOutputFile_shouldReturnNoExtraNewLine() throws IOException {
        String commandString = "uniq " + FILE_1 + CHAR_SPACE + FILE_3;
        assertDoesNotThrow(() -> shell.parseAndEvaluate(commandString, stdOut));
        File outputFile = new File(TEST_PATH + FILE_3);
        assertTrue(outputFile.exists());
        String actual = Files.readString(Paths.get(TEST_PATH + FILE_3));
        assertEquals("Alice" + STRING_NEWLINE, actual);
    }


}
