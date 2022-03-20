package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.PasteException;
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
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.GREP_PASTE_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class GrepPasteIntegrationTest {
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = GREP_PASTE_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    public static final String FILE1 = "file1";
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String NE_FILE_NAME = "nonexistent.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;
    public static final String FILE2_PATH = TEST_PATH + FILE2_NAME;

    public static final String[] LINES1 = {FILE1_NAME, FILE2_NAME, FILE1, NE_FILE_NAME};
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
    void testGrepPasteParseAndEvaluate_grepFileNamePipeToPaste_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("grep \"file1\" %s | paste", FILE1_PATH);
        String expectedOutput = FILE1_NAME + STRING_NEWLINE + FILE1 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepPasteParseAndEvaluate_grepPasteOutputAndCountOccurences_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("paste %s | grep -c \"file1\"", FILE1_PATH);
        String expectedOutput = "2" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepPasteParseAndEvaluate_grepPasteOutputssAndCountOccurences_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("paste %s | grep -H \"file1\"", FILE1_PATH);
        String expectedOutput = "(standard input):file1.txt" + STRING_NEWLINE + "(standard input):file1" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepPasteParseAndEvaluate_pasteOutputOfGrepInsensitive_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("paste \"`grep \"file1.txt\" %s`\"", FILE1_PATH);
        String expectedOutput = String.join(STRING_NEWLINE, LINES1) + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepPasteParseAndEvaluate_pasteOutputOfGrepNoSuchFile_shouldReturnNoSuchFileOrDirectory() throws Exception {
        String commandString = String.format("paste \"`grep \"file4.txt\" %s`\"", FILE1_PATH);
        assertThrows(PasteException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }

    @Test
    void testGrepPasteParseAndEvaluate_grepInvalidFlag_shouldThrowGrepException() {
        String commandString = String.format("paste \"`grep \"??!\" %s`\"", FILE1_PATH);
        assertThrows(GrepException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }

    @Test
    void testGrepPasteParseAndEvaluate_pasteOutputOfGrepNonExistent_shouldThrowPasteException() {
        String commandString = String.format("paste \"`grep \"nonexistent.txt\" %s`\"", FILE1_PATH);
        assertThrows(PasteException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }
}
