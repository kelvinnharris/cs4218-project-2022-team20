package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.WC_CUT_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.*;

public class WcCutIntegrationTest {

    public static final String INPUT = "New input" + STRING_NEWLINE + "1000" + STRING_NEWLINE;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE3_NAME = "file3.txt";
    public static final String FOLDER1_NAME = "folder1";
    public static final String NE_FILE_NAME = "nonExistent.txt";
    public static final String[] LINES1 = {"abc", "abc", "def", "abc", "abc", "abc"}; // NOPMD - duplicate literals are used once for definition
    public static final String[] LINES2 = {"123", "123", "", ""};
    public static final String[] LINES3 = {FILE1_NAME, FILE1_NAME, FILE2_NAME, FILE1_NAME, FILE1_NAME};

    private static final String TEST_PATH = Environment.currentDirectory + CHAR_FILE_SEP + WC_CUT_FOLDER;
    public static final String FILE1_PATH = TEST_PATH + CHAR_FILE_SEP + FILE1_NAME;
    public static final String FILE2_PATH = TEST_PATH + CHAR_FILE_SEP + FILE2_NAME;
    public static final String FILE3_PATH = TEST_PATH + CHAR_FILE_SEP + FILE3_NAME;
    public static final String FOLDER1_PATH = TEST_PATH + CHAR_FILE_SEP + FOLDER1_NAME;

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    public final InputStream inputStream = new ByteArrayInputStream(INPUT.getBytes());

    @BeforeAll
    static void setUp() {
        shell = new ShellImpl();
    }

    @BeforeEach
    void setUpEach() throws IOException {
        if (new File(TEST_PATH).exists()) {
            deleteDir(new File(TEST_PATH));
        }

        Files.createDirectory(Paths.get(TEST_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(FILE3_PATH));
        Files.createDirectory(Paths.get(FOLDER1_PATH));

        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);
        appendToFile(Paths.get(FILE3_PATH), LINES3);

        Environment.currentDirectory = TEST_PATH;
        stdOut = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() {
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testWcCutParseCommand_cutFromWcOutputValid_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("wc %s | cut -b 26-34", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = FILE1_NAME + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testWcCutParseCommand_cutFromWcOutputOutOfBoundRange_shouldReturnOutputWithNullsForOutOfBoundValues() throws Exception {
        String commandString = String.format("wc %s | cut -b 26-35", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = FILE1_NAME + CHAR_NULL + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testWcCutParseCommand_cutFromWcOutputMultipleFiles_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("wc %s %s | cut -b 23-24", FILE1_NAME, FILE2_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput;
        if (isWindowsSystem()) {
            expectedOutput = "30" + STRING_NEWLINE + "14" + STRING_NEWLINE + "44" + STRING_NEWLINE;
        } else {
            expectedOutput = "24" + STRING_NEWLINE + "10" + STRING_NEWLINE + "34" + STRING_NEWLINE;
        }
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testWcCutParseCommand_cutFromValidWcOutputAsArgument_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cut -b 1 `wc %s | cut -b 26-34`", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = "a" + STRING_NEWLINE + "a" + STRING_NEWLINE + "d" + STRING_NEWLINE + "a" + STRING_NEWLINE
                + "a" + STRING_NEWLINE + "a" + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testWcCutParseCommand_cutFromInvalidWcOutputAsArgument_shouldThrowCutException() throws Exception {
        String commandString = String.format("cut -c 1-3 `wc %s`", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(CutException.class, () -> command.evaluate(inputStream, stdOut));
    }
}
