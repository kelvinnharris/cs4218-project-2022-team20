package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.exception.GrepException;
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
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.GREP_CD_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class GrepCdIntegrationTest {

    public static final String INPUT = "25600" + STRING_NEWLINE + "1000" + STRING_NEWLINE;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE3_NAME = "file3.txt";
    public static final String FOLDER1_NAME = "folder1";
    public static final String NE_FILE_NAME = "nonExistent.txt";
    public static final String[] LINES1 = {"abc", "def", "ghi", "jkl", "abc", "mno", "123"}; // NOPMD - duplicate literals are used once for definition
    public static final String[] LINES2 = {"123", "789", "456", "45"};
    public static final String[] LINES3 = {FILE1_NAME, FILE2_NAME};

    private static final String TEST_PATH = Environment.currentDirectory + CHAR_FILE_SEP + GREP_CD_FOLDER;
    public static final String FILE1_PATH = TEST_PATH + CHAR_FILE_SEP + FILE1_NAME;
    public static final String FILE2_PATH = TEST_PATH + CHAR_FILE_SEP + FILE2_NAME;
    public static final String FILE3_PATH = TEST_PATH + CHAR_FILE_SEP + FOLDER1_NAME + CHAR_FILE_SEP + FILE3_NAME;
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
        Files.createDirectory(Paths.get(FOLDER1_PATH));
        Files.createFile(Paths.get(FILE3_PATH));

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
    void testGrepCdParseCommand_cdThenGrep_shouldReturnCorrectLines() throws Exception {
        String commandString = String.format("cd . | grep \"2\" %s", FILE2_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = "123" + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepCdParseCommand_cdIntoFolderThenGrepWithCaseInsensitiveFlag_shouldReturnCorrectLines() throws Exception {
        String commandString = String.format("cd %s; grep \"Fi\" %s -i", FOLDER1_NAME, FILE3_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = FILE1_NAME + STRING_NEWLINE + FILE2_NAME + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepCdParseCommand_cdIntoFolderAndOutThenGrepMultiple_shouldReturnCorrectLines() throws Exception {
        String commandString = String.format("cd %s; cd ..; grep \"a\" %s %s", FOLDER1_NAME, FILE1_NAME, FILE2_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        String expectedOutput = String.format("%s:abc", FILE1_NAME) + STRING_NEWLINE
                + String.format("%s:abc", FILE1_NAME) + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testGrepCdParseCommand_grepOutputOfCd_shouldThrowGrepException() throws Exception {
        String commandString = String.format("grep `cd %s`", FOLDER1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(GrepException.class, () -> command.evaluate(inputStream, stdOut));
    }

    @Test
    void testGrepCdParseCommand_cdIntoGrepManyOutput_shouldThrowCdException() throws Exception {
        String commandString = String.format("cd `grep \"a\" %s`", FOLDER1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(CdException.class, () -> command.evaluate(inputStream, stdOut));
    }
}

