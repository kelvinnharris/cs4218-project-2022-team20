package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;
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
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_SPACE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.CAT_UNIQ_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class CatUniqIntegrationTest {

    public static final String INPUT = "New input" + STRING_NEWLINE + "1000" + STRING_NEWLINE;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE3_NAME = "file3.txt";
    public static final String FOLDER1_NAME = "folder1";
    public static final String NE_FILE_NAME = "nonExistent.txt";

    public static final String ABC = "abc";
    public static final String DEF = "def";
    public static final String TEXT_123 = "123";
    public static final String[] LINES1 = {ABC, ABC, DEF, ABC, ABC, ABC};
    public static final String[] LINES2 = {TEXT_123, TEXT_123, "", ""};
    public static final String[] LINES3 = {FILE1_NAME, FILE1_NAME, FILE2_NAME, FILE1_NAME, FILE1_NAME};

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + CAT_UNIQ_FOLDER;
    public static final String FILE1_PATH = TEST_PATH + CHAR_FILE_SEP + FILE1_NAME;
    public static final String FILE2_PATH = TEST_PATH + CHAR_FILE_SEP + FILE2_NAME;
    public static final String FILE3_PATH = TEST_PATH + CHAR_FILE_SEP + FILE3_NAME;
    public static final String FOLDER1_PATH = TEST_PATH + CHAR_FILE_SEP + FOLDER1_NAME;

    private static ByteArrayOutputStream stdOut;
    public final InputStream inputStream = new ByteArrayInputStream(INPUT.getBytes());

    @BeforeAll
    static void setUp() throws IOException {
        deleteDir(new File(TEST_PATH));

        Files.createDirectory(Paths.get(TEST_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(FILE3_PATH));
        Files.createDirectory(Paths.get(FOLDER1_PATH));

        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);
        appendToFile(Paths.get(FILE3_PATH), LINES3);
    }

    @BeforeEach
    void setUpEach() throws IOException {
        stdOut = new ByteArrayOutputStream();
        Environment.currentDirectory = TEST_PATH;
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testCatUniqParseCommand_uniqFromCatFiles_shouldReturnCorrectUniqLines() throws Exception {
        String commandString = String.format("cat %s | uniq", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String uniqResult1 = ABC + STRING_NEWLINE + DEF + STRING_NEWLINE + ABC + STRING_NEWLINE;
        assertEquals(uniqResult1, stdOut.toString());
    }

    @Test
    void testCatUniqParseCommand_uniqOneDuplicateFromCatFiles_shouldReturnCorrectDuplicateLines() throws Exception {
        String commandString = String.format("cat %s | uniq -d", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String uniqResult1 = ABC + STRING_NEWLINE + ABC + STRING_NEWLINE;
        assertEquals(uniqResult1, stdOut.toString());
    }

    @Test
    void testCatUniqParseCommand_uniqAllDuplicateFromCatFiles_shouldReturnCorrectDuplicateLines() throws Exception {
        String commandString = String.format("cat %s | uniq -D", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String uniqResult1 = ABC + STRING_NEWLINE + ABC + STRING_NEWLINE + ABC + STRING_NEWLINE + ABC + STRING_NEWLINE + ABC + STRING_NEWLINE;
        assertEquals(uniqResult1, stdOut.toString());
    }

    @Test
    void testCatUniqParseCommand_CatFilenamesReturnedByUniq_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat `uniq %s`", FILE3_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);

        String expectedOutput = String.join(STRING_NEWLINE, LINES1) + STRING_NEWLINE + String.join(STRING_NEWLINE, LINES2) + STRING_NEWLINE + String.join(STRING_NEWLINE, LINES1) + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatUniqParseCommand_UniqOutputOfCatFromUniq_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat %s %s | uniq -c", FILE1_NAME, FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = "2" + CHAR_SPACE + ABC + STRING_NEWLINE
                + "1" + CHAR_SPACE + DEF + STRING_NEWLINE
                + "5" + CHAR_SPACE + ABC + STRING_NEWLINE
                + "1" + CHAR_SPACE + DEF + STRING_NEWLINE
                + "3" + CHAR_SPACE + ABC + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatUniqParseCommand_invalidCatFlagFromUniqParameter_shouldThrowCatException() throws Exception {
        String commandString = String.format("cat -p `uniq %s`", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(CatException.class, () -> command.evaluate(inputStream, stdOut));
    }

    @Test
    void testCatUniqParseCommand_UniqDirOutputOfCat_shouldThrowUniqException() throws Exception {
        String commandString = String.format("cat `uniq %s`", FOLDER1_PATH);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(UniqException.class, () -> command.evaluate(inputStream, stdOut));
    }

    @Test
    void testCatUniqParseCommand_UniqDirInvalidPathOutputOfCat_shouldThrowUniqException() throws Exception {
        String commandString = String.format("cat `uniq %s`", NE_FILE_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(UniqException.class, () -> command.evaluate(inputStream, stdOut));
    }
}
