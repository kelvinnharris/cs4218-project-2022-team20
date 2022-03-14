package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.CUT_PASTE_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class CutPasteIntegrationTest {

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = CUT_PASTE_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;

    public static final String TMP_INPUT1_FILE = "tmp1.txt";
    public static final String TMP_INPUT2_FILE = "tmp2.txt";
    public static final String TMP_OUTPUT_FILE = "tmpOutputFile.txt";
    public static final String NE_FILE_NAME = "nonExistent.txt";
    public static final String[] LINES1 = {"Hello world", "First line", "!"};
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
        appendToFile(Paths.get(FILE1_PATH), LINES1);
    }

    @AfterAll
    static void tearDown() {
        deleteDir(new File(TEST_PATH));
    }

    // TODO - Observe Cut NULL output
    @Test
    void testCutPasteParseAndEvaluate_cutPasteWithoutSerialFlag_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cut -c 1 %s > %s; cut -c 2 %s > %s; paste %s %s > %s; cut -c 1-3 %s;",
                FILE1_PATH, TMP_INPUT1_FILE, FILE1_PATH, TMP_INPUT2_FILE, TMP_INPUT1_FILE, TMP_INPUT2_FILE, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        String expectedOutput = "H" + STRING_TAB + "e" + STRING_NEWLINE + "F" + STRING_TAB + "i" + STRING_NEWLINE + "!" + STRING_TAB;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    // TODO - Observe Cut NULL output, Paste serial output
    @Test
    void testCutPasteParseAndEvaluate_cutPasteWithSerialFlag_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cut -c 1 %s > %s; cut -c 2 %s > %s; paste -s %s %s > %s; cut -c 1-3 %s;",
                FILE1_PATH, TMP_INPUT1_FILE, FILE1_PATH, TMP_INPUT2_FILE, TMP_INPUT1_FILE, TMP_INPUT2_FILE, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        String expectedOutput = "H" + STRING_TAB + "F" + STRING_TAB + "!" + STRING_NEWLINE + "e" + STRING_TAB + "i" + STRING_TAB;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCutPasteParseCommand_cutPasteWithStdinInputWithoutSerialFlag_shouldReturnCorrectOutput() throws Exception {
        String inputString = "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "3";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String commandString = String.format("cut -c 1 %s > %s; paste %s - > %s; cut -c 1-3 %s;",
                FILE1_PATH, TMP_INPUT1_FILE, TMP_INPUT1_FILE, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        String expectedOutput = "H" + STRING_TAB + "1" + STRING_NEWLINE + "F" + STRING_TAB + "2" + STRING_NEWLINE + "!" + STRING_TAB + "3" + STRING_NEWLINE;
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(input, stdOut);
        final String standardOutput = stdOut.toString();
        assertEquals(expectedOutput, standardOutput);
    }

    // TODO - Observe Paste error
    @Test
    void testCutPasteParseCommand_cutPasteWithStdinInputWithSerialFlag_shouldReturnCorrectOutput() throws Exception {
        String inputString = "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "3";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String commandString = String.format("cut -c 1 %s > %s; paste -s %s - > %s; cut -c 1-3 %s;",
                FILE1_PATH, TMP_INPUT1_FILE, TMP_INPUT1_FILE, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        String expectedOutput = "H" + STRING_TAB + "F" + STRING_TAB + "!" + STRING_NEWLINE + "1" + STRING_TAB + "2" + STRING_TAB + "3" + STRING_NEWLINE;
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(input, stdOut);
        final String standardOutput = stdOut.toString();
        assertEquals(expectedOutput, standardOutput);
    }

    @Test
    void testCutPasteParseAndEvaluate_cutPasteUsingPipeValidCommand_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cut -c 1 %s > %s | paste %s %s > %s | cut -c 1-3 %s",
                FILE1_PATH, TMP_INPUT1_FILE, FILE1_PATH, TMP_INPUT1_FILE, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        String expectedOutput = "Hel" + STRING_NEWLINE + "Fir" + STRING_NEWLINE + "!" + STRING_TAB + "!" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCutPasteParseAndEvaluate_cutPasteUsingPipeInvalidCutCommand_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cut 1 %s > %s | paste %s %s > %s | cut -c 1-3 %s",
                FILE1_PATH, TMP_INPUT1_FILE, FILE1_PATH, TMP_INPUT1_FILE, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        assertThrows(CutException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }

    // TODO - Investigate Paste
    @Test
    void testCutPasteParseAndEvaluate_cutPasteUsingPipeInvalidPasteCommand_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cut -c 1 %s > %s | paste %s %s > %s | cut -c 1-3 %s",
                FILE1_PATH, TMP_INPUT1_FILE, FILE1_PATH, NE_FILE_NAME, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        assertThrows(PasteException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }
}
