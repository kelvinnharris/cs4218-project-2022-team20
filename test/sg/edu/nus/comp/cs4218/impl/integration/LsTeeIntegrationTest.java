package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.LS_TEE_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class LsTeeIntegrationTest {

    public static final String INPUT = "New input" + STRING_NEWLINE + "1000" + STRING_NEWLINE;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String FOLDER1_NAME = "folder1";
    public static final String NE_FILE_NAME = "nonExistent.txt";
    public static final String[] LINES1 = {"The first file", "The second line", "1000"};
    public static final String[] LINES2 = {"The second file", "The second line", "10"};
    public static final String PATTERN1 = "The second";
    public static final String PATTERN1_INSEN = "THE SECoND";
    private static final String TEST_PATH = Environment.currentDirectory + CHAR_FILE_SEP + LS_TEE_FOLDER;
    public static final String FILE1_PATH = TEST_PATH + CHAR_FILE_SEP + FILE1_NAME;
    public static final String FILE2_PATH = TEST_PATH + CHAR_FILE_SEP + FILE2_NAME;
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

        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);

        Environment.currentDirectory = TEST_PATH;
        stdOut = new ByteArrayOutputStream();
    }

    @AfterEach
    void tearDown() {
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testLsTee_teeAppendIntoFileOfLs_shouldAppendIntoFile() throws Exception {
        String commandString = String.format("tee -a `ls %s`", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        assertEquals(INPUT, stdOut.toString());

        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(LINES1).forEach(line -> stringBuilder.append(line).append(STRING_NEWLINE));
        stringBuilder.append(INPUT);
        String expectedContent = stringBuilder.toString();
        String actualContent = Files.readString(Paths.get(FILE1_PATH));

        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testLsTee_teeAppendIntoFilesOfLs_shouldAppendIntoFiles() throws Exception {
        String commandString = "tee -a `ls *.txt`";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        assertEquals(INPUT, stdOut.toString());

        StringBuilder stringBuilder1 = new StringBuilder();
        Arrays.stream(LINES1).forEach(line -> stringBuilder1.append(line).append(STRING_NEWLINE));
        stringBuilder1.append(INPUT);
        String expectedContent1 = stringBuilder1.toString();
        String actualContent1 = Files.readString(Paths.get(FILE1_PATH));
        assertEquals(expectedContent1, actualContent1);

        StringBuilder stringBuilder2 = new StringBuilder();
        Arrays.stream(LINES2).forEach(line -> stringBuilder2.append(line).append(STRING_NEWLINE));
        stringBuilder2.append(INPUT);
        String expectedContent2 = stringBuilder2.toString();
        String actualContent2 = Files.readString(Paths.get(FILE2_PATH));
        assertEquals(expectedContent2, actualContent2);
    }

    @Test
    void testLsTee_teeNonAppendIntoFilesOfLs_shouldTeeIntoFile() throws Exception {
        String commandString = String.format("tee `ls %s`", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);
        assertEquals(INPUT, stdOut.toString());

        String actualFileContent = Files.readString(Paths.get(FILE1_PATH));
        assertEquals(INPUT, actualFileContent);
    }

    @Test
    void testLsTee_teeOutputOfLsIntoFiles_shouldAppendFilenames() throws Exception {
        String commandString = String.format("ls %s | tee -a %s", FILE1_NAME, FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);

        String expectedOutput = FILE1_NAME + STRING_NEWLINE;
        assertEquals(expectedOutput, stdOut.toString());

        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(LINES1).forEach(line -> stringBuilder.append(line).append(STRING_NEWLINE));
        stringBuilder.append(FILE1_NAME).append(STRING_NEWLINE);
        String expectedContent = stringBuilder.toString();
        String actualContent = Files.readString(Paths.get(FILE1_PATH));

        assertEquals(expectedContent, actualContent);
    }

    @Test
    void testLsTee_teeAndLsMultiplePipes_shouldTeeFilenamesRespectively() throws Exception {
        String commandString = String.format("ls %s | tee -a %s | tee %s", FILE1_NAME, FILE1_NAME, FILE2_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(inputStream, stdOut);

        StringBuilder stringBuilder1 = new StringBuilder();
        Arrays.stream(LINES1).forEach(line -> stringBuilder1.append(line).append(STRING_NEWLINE));
        stringBuilder1.append(FILE1_NAME).append(STRING_NEWLINE);
        String expectedContent1 = stringBuilder1.toString();
        String actualContent1 = Files.readString(Paths.get(FILE1_PATH));
        assertEquals(expectedContent1, actualContent1);

        String actualContent2 = Files.readString(Paths.get(FILE2_PATH));
        assertEquals(stdOut.toString(), actualContent2);
    }

    @Test
    void testLsTee_lsTeeOutputInvalidArgs_shouldThrowTeeException() throws Exception {
        String commandString = String.format("tee `ls %s` -d", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(TeeException.class, () -> command.evaluate(inputStream, stdOut));
    }
}
