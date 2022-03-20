package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.TEE_UNIQ_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

@Isolated("ResourceSharing")
public class TeeUniqIntegrationTest {

    public static final String WORD1 = "word1";
    public static final String WORD2 = "word2";
    public static final String WORD3 = "abc";
    public static final String WORD4 = "def";
    public static final String WORD5 = "123";
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE3_NAME = "file3.txt";
    public static final String FILE4_NAME = "file4.txt";
    public static final String UNWR_FILE_NAME = "unwritable.txt";
    public static final String FOLDER1_NAME = "folder1";
    public static final String NE_FILE_NAME = "nonExistent.txt";
    public static final String[] LINES1 = {WORD3, WORD3, WORD4, WORD3, WORD3, WORD3};
    public static final String[] LINES2 = {WORD5, WORD5, "", ""};
    public static final String[] LINES3 = {FILE1_NAME, FILE1_NAME, FILE2_NAME};

    public static final String INPUT1 = WORD1 + STRING_NEWLINE + WORD1 + STRING_NEWLINE + WORD2 + STRING_NEWLINE
            + WORD1 + STRING_NEWLINE + WORD1 + STRING_NEWLINE;
    public static final String INPUT2 = FILE1_NAME + STRING_NEWLINE + FILE1_NAME + STRING_NEWLINE + FILE2_NAME + STRING_NEWLINE
            + FILE1_NAME + STRING_NEWLINE + FILE1_NAME + STRING_NEWLINE;

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEE_UNIQ_FOLDER;
    public static final String FILE1_PATH = TEST_PATH + CHAR_FILE_SEP + FILE1_NAME;
    public static final String FILE2_PATH = TEST_PATH + CHAR_FILE_SEP + FILE2_NAME;
    public static final String FILE3_PATH = TEST_PATH + CHAR_FILE_SEP + FILE3_NAME;
    public static final String FILE4_PATH = TEST_PATH + CHAR_FILE_SEP + FILE4_NAME;
    public static final String NE_FILE_PATH = TEST_PATH + CHAR_FILE_SEP + NE_FILE_NAME;
    public static final String FOLDER1_PATH = TEST_PATH + CHAR_FILE_SEP + FOLDER1_NAME;
    private static final String UNWR_FILE_PATH = TEST_PATH + CHAR_FILE_SEP + UNWR_FILE_NAME;
    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    public InputStream inputStream;

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
        Files.createFile(Paths.get(FILE4_PATH));
        Files.createFile(Paths.get(UNWR_FILE_PATH));
        Files.createDirectory(Paths.get(FOLDER1_PATH));
        Paths.get(UNWR_FILE_PATH).toFile().setReadOnly();

        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);
        appendToFile(Paths.get(FILE3_PATH), LINES3);
        appendToFile(Paths.get(FILE4_PATH), UNWR_FILE_NAME, STRING_NEWLINE);

        Environment.currentDirectory = TEST_PATH;
        stdOut = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDownEach() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testTeeUniqParseCommand_teeIntoUniqOutputNoAppend_shouldTeeIntoFile() throws Exception {
        inputStream = new ByteArrayInputStream(INPUT1.getBytes());
        String commandString = String.format("tee `uniq %s`", FILE3_PATH);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String file1ContentAfter = readString(Paths.get(FILE1_PATH));
        String file2ContentAfter = readString(Paths.get(FILE2_PATH));
        assertEquals(INPUT1, file1ContentAfter);
        assertEquals(INPUT1, file2ContentAfter);
    }

    @Test
    void testTeeUniqParseCommand_teeIntoUniqOutputAppend_shouldAppendToFile() throws Exception {
        inputStream = new ByteArrayInputStream(INPUT1.getBytes());
        String commandString = String.format("tee -a `uniq %s`", FILE3_PATH);
        String file1ContentBfr = readString(Paths.get(FILE1_PATH));
        String file2ContentBfr = readString(Paths.get(FILE2_PATH));
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String file1ContentAfter = readString(Paths.get(FILE1_PATH));
        String file2ContentAfter = readString(Paths.get(FILE2_PATH));
        assertEquals(file1ContentBfr + INPUT1, file1ContentAfter);
        assertEquals(file2ContentBfr + INPUT1, file2ContentAfter);
    }

    @Test
    void testTeeUniqParseCommand_TeeUniqOutputWithDuplicateFlag_shouldReturnDuplicateLinesAndFileContainsUniqOutput()
            throws Exception {
        inputStream = new ByteArrayInputStream(INPUT1.getBytes());
        String commandString = String.format("uniq -d | tee %s", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String file1Content = readString(Paths.get(FILE1_PATH));
        assertEquals(WORD1 + STRING_NEWLINE + WORD1 + STRING_NEWLINE, file1Content);
        assertEquals(WORD1 + STRING_NEWLINE + WORD1 + STRING_NEWLINE, stdOut.toString());
    }

    @Test
    void testTeeUniqParseCommand_uniqTeeOutputWithDuplicateFlag_shouldReturnDuplicateLinesAndFileContainsInputFromTee()
            throws Exception {
        inputStream = new ByteArrayInputStream(INPUT1.getBytes());
        String commandString = String.format("tee %s | uniq -d", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String file1Content = readString(Paths.get(FILE1_PATH));
        assertEquals(INPUT1, file1Content);
        assertEquals(WORD1 + STRING_NEWLINE + WORD1 + STRING_NEWLINE, stdOut.toString());
    }

    @Test
    void testTeeUniqParseCommand_teeUniqOutputUnwritableFiles_shouldNotWriteIntoFile() throws Exception {
        inputStream = new ByteArrayInputStream(INPUT2.getBytes());
        String commandString = String.format("tee `uniq %s`", FILE4_PATH);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        assertEquals(String.format("%s: Permission denied", UNWR_FILE_NAME) + STRING_NEWLINE + INPUT2, stdOut.toString());
    }

    @Test
    void testTeeUniqParseCommand_teeUniqOutputFileNotFound_shouldThrowException() throws Exception {
        inputStream = new ByteArrayInputStream(INPUT2.getBytes());
        String commandString = String.format("tee `uniq %s`", NE_FILE_PATH);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(Exception.class, () -> command.evaluate(inputStream, stdOut));
    }
}
