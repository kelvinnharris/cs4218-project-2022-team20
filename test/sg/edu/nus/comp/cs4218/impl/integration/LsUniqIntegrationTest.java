package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.LS_UNIQ_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class LsUniqIntegrationTest {

    public static final String INPUT = "New input" + STRING_NEWLINE + "1000" + STRING_NEWLINE;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE3_NAME = "file3.txt";
    public static final String FOLDER1_NAME = "folder1";
    public static final String NE_FILE_NAME = "nonExistent.txt";

    public static final String ABC = "abc";
    public static final String DEF = "def";
    public static final String WORD123 = "123";
    public static final String EMPTY_STRING = "";

    public static final String[] LINES1 = {ABC, ABC, DEF, ABC, ABC, ABC};
    public static final String[] LINES2 = {WORD123, WORD123, EMPTY_STRING, EMPTY_STRING};
    public static final String[] LINES3 = {FILE1_NAME, FILE1_NAME, FILE2_NAME, FILE1_NAME, FILE1_NAME};

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + LS_UNIQ_FOLDER;
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
    void testLsUniqParseCommand_uniqFromLsFiles_shouldReturnCorrectUniqLines() throws Exception {
        String commandString = String.format("uniq `ls %s`", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String uniqResult1 = ABC + STRING_NEWLINE + DEF + STRING_NEWLINE + ABC + STRING_NEWLINE;
        assertEquals(uniqResult1, stdOut.toString());
    }

    @Test
    void testLsUniqParseCommand_uniqDuplicateFromLsFiles_shouldReturnCorrectDuplicateLines() throws Exception {
        String commandString = String.format("uniq -d `ls %s`", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String uniqResult1 = ABC + STRING_NEWLINE + ABC + STRING_NEWLINE;
        assertEquals(uniqResult1, stdOut.toString());
    }

    @Test
    void testLsUniqParseCommand_uniqDuplicateOutputOfLsAsInput_shouldReturnNoOutput() throws Exception {
        String commandString = String.format("ls %s | uniq -d", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        assertEquals(EMPTY_STRING, stdOut.toString());
    }

    @Test
    void testLsUniqParseCommand_LsFilenamesReturnedByUniq_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("ls `uniq %s`", FILE3_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);

        String expectedOutput = FILE1_NAME + STRING_NEWLINE + FILE2_NAME + STRING_NEWLINE + FILE1_NAME + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testLsUniqParseCommand_UniqOutputOfLsFromUniq_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("ls `uniq %s` | uniq -c", FILE3_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = "1" + CHAR_SPACE + FILE1_NAME + STRING_NEWLINE
                + "1" + CHAR_SPACE + FILE2_NAME + STRING_NEWLINE
                + "1" + CHAR_SPACE + FILE1_NAME + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testLsUniq_UniqDirOutputOfLs_shouldThrowUniqException() throws Exception {
        String commandString = String.format("ls `uniq %s`", FOLDER1_PATH);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(UniqException.class, () -> command.evaluate(inputStream, stdOut));
    }
}
