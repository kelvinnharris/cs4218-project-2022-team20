package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.GREP_SORT_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class GrepSortIntegrationTest {

    public static final String INPUT = "25600" + STRING_NEWLINE + "1000" + STRING_NEWLINE;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE3_NAME = "file3.txt";
    public static final String ABC = "abc";
    public static final String DEF = "def";
    public static final String GHI = "ghi";
    public static final String JKL = "jkl";
    public static final String MNO = "mno";
    public static final String WORD123 = "123";
    public static final String WORD456 = "456";
    public static final String WORD789 = "789";
    public static final String WORD45 = "45";
    public static final String FOLDER1_NAME = "folder1";
    public static final String NE_FILE_NAME = "nonExistent.txt";
    public static final String[] LINES1 = {ABC, DEF, GHI, JKL, ABC, MNO, WORD123};
    public static final String[] LINES2 = {WORD123, WORD789, WORD456, WORD45};
    public static final String[] LINES3 = {FILE1_NAME, FILE2_NAME};
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + GREP_SORT_FOLDER;
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
    void tearDownEach() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testGrepSortParseCommand_grepOutputOfSortValid_shouldReturnCorrectLines() throws Exception {
        String commandString = String.format("sort -n %s | grep \"2\"", FILE2_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = "123" + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepSortParseCommand_sortOutputOfGrepValid_shouldReturnCorrectLines() throws Exception {
        String commandString = String.format("grep \"Ab\" %s -i | sort", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = "abc" + STRING_NEWLINE + "abc" + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepSortParseCommand_sortOutputOfGrepFromStdin_shouldReturnCorrectLines() throws Exception {
        String commandString = "grep \"00\" - | sort -n";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = "1000" + STRING_NEWLINE + "25600" + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepSortParseCommand_sortEmptyOutputOfGrep_shouldReturnCorrectLines() throws Exception {
        String commandString = String.format("grep \"hello\" %s | sort", FILE2_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        assertEquals("", stdOut.toString());
    }

    @Test
    void testGrepSortParseCommand_sortGrepOutputFromMultipleFilesAndStdin_shouldReturnCorrectLines() throws Exception {
        String commandString = String.format("grep \"1\" %s %s - | sort", FILE1_NAME, FILE2_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = "(standard input):1000" + STRING_NEWLINE + "file1.txt:123" + STRING_NEWLINE
                + "file2.txt:123" + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepSortParseCommand_sortErrorOutputThenGrepPattern_shouldReturnCorrectLines() throws Exception {
        String commandString = String.format("sort -z %s | grep \"f\"", FILE3_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(SortException.class, () -> command.evaluate(inputStream, stdOut));
    }
}
