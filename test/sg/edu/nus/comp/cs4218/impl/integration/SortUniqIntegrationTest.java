package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.UniqException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.SORT_UNIQ_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class SortUniqIntegrationTest {
    public static final String INPUT = "New input" + STRING_NEWLINE + "1000" + STRING_NEWLINE;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE3_NAME = "file3.txt";
    public static final String FOLDER1_NAME = "folder1";
    public static final String NE_FILE_NAME = "nonExistent.txt";

    public static final String ABC = "abc";
    public static final String DEF = "def";
    public static final String TEXT_100 = "100";
    public static final String TEXT_20 = "20";
    public static final String[] LINES1 = {ABC, ABC, DEF, ABC, ABC, ABC};
    public static final String[] LINES2 = {TEXT_100, TEXT_20, TEXT_20, "", ""};
    public static final String[] LINES3 = {FILE1_NAME, FILE1_NAME, FILE2_NAME, FILE1_NAME, FILE1_NAME};

    private static final String TEST_PATH = Environment.currentDirectory + CHAR_FILE_SEP + SORT_UNIQ_FOLDER;
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
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testSortUniqParseCommand_uniqFromSortedFiles_shouldReturnCorrectUniqLines() throws Exception {
        String commandString = String.format("sort %s | uniq", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String uniqResult1 = ABC + STRING_NEWLINE + DEF + STRING_NEWLINE;
        assertEquals(uniqResult1, stdOut.toString());
    }

    @Test
    void testSortUniqParseCommand_uniqOneDuplicateFromSortedFiles_shouldReturnCorrectDuplicateLines() throws Exception {
        String commandString = String.format("sort %s | uniq -d", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String uniqResult1 = ABC + STRING_NEWLINE;
        assertEquals(uniqResult1, stdOut.toString());
    }

    @Test
    void testSortUniqParseCommand_uniqAllDuplicateFromSortedFiles_shouldReturnCorrectDuplicateLines() throws Exception {
        String commandString = String.format("sort %s | uniq -D", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String uniqResult1 = ABC + STRING_NEWLINE + ABC + STRING_NEWLINE + ABC + STRING_NEWLINE + ABC + STRING_NEWLINE + ABC + STRING_NEWLINE;
        assertEquals(uniqResult1, stdOut.toString());
    }

    @Test
    void testSortUniqParseCommand_reversedUnsortedIndexReturnedByUniq_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("sort -rf `uniq %s` | uniq", FILE3_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = DEF + STRING_NEWLINE + ABC + STRING_NEWLINE + TEXT_20 + STRING_NEWLINE + TEXT_100 + STRING_NEWLINE + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testSortUniqParseCommand_reversedSortedIndexReturnedByUniq_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("sort -nrf `uniq %s` | uniq", FILE3_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = DEF + STRING_NEWLINE + ABC + STRING_NEWLINE + TEXT_100 + STRING_NEWLINE + TEXT_20 + STRING_NEWLINE + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testSortUniq_UniqDirOutputOfSort_shouldThrowUniqException() throws Exception {
        String commandString = String.format("sort `uniq %s`", FOLDER1_PATH);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(UniqException.class, () -> command.evaluate(inputStream, stdOut));
    }

    @Test
    void testSortUniq_UniqDirInvalidPathOutputOfSort_shouldThrowUniqException() throws Exception {
        String commandString = String.format("sort `uniq %s`", NE_FILE_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(UniqException.class, () -> command.evaluate(inputStream, stdOut));
    }
}