package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class TeePasteIntegrationTest {

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpTeePasteTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE2_PATH = TEST_PATH + FILE2_NAME;
    public static final String OUTPUT1_NAME = "output1.txt";
    public static final String OUTPUT1_PATH = TEST_PATH + OUTPUT1_NAME;
    public static final String OUTPUT2_NAME = "output2.txt";
    public static final String OUTPUT2_PATH = TEST_PATH + OUTPUT2_NAME;
    public static final String OUTPUT3_NAME = "output3.txt";
    public static final String OUTPUT3_PATH = TEST_PATH + OUTPUT3_NAME;
    public static final String OUTPUT4_NAME = "output4.txt";
    public static final String OUTPUT4_PATH = TEST_PATH + OUTPUT4_NAME;
    public static final String OUTPUT5_NAME = "output5.txt";
    public static final String OUTPUT5_PATH = TEST_PATH + OUTPUT5_NAME;
    public static final String OUTPUT6_NAME = "output6.txt";
    public static final String OUTPUT6_PATH = TEST_PATH + OUTPUT6_NAME;
    public static final String OUTPUT7_NAME = "output7.txt";
    public static final String OUTPUT7_PATH = TEST_PATH + OUTPUT7_NAME;


    public static final String FIRST = "FIRST";
    public static final String SECOND = "SECOND";
    public static final String THIRD = "THIRD";
    public static final String FROM_TEE = "FROM TEE";



    public static final String[] LINES = {FIRST, SECOND};

    @BeforeAll
    static void setUp() throws IOException {
        shell = new ShellImpl();
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
    }


    @BeforeEach
    void setUpEach() throws IOException {
        stdOut = new ByteArrayOutputStream();

        Environment.currentDirectory = ROOT_PATH;
        Files.deleteIfExists(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.deleteIfExists(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        appendToFile(Paths.get(FILE2_PATH), LINES);
        Files.deleteIfExists(Paths.get(OUTPUT1_PATH));
        Files.createFile(Paths.get(OUTPUT1_PATH));
        Files.deleteIfExists(Paths.get(OUTPUT2_PATH));
        Files.createFile(Paths.get(OUTPUT2_PATH));
        Files.deleteIfExists(Paths.get(OUTPUT3_PATH));
        Files.createFile(Paths.get(OUTPUT3_PATH));
        Files.deleteIfExists(Paths.get(OUTPUT4_PATH));
        Files.createFile(Paths.get(OUTPUT4_PATH));
        Files.deleteIfExists(Paths.get(OUTPUT5_PATH));
        Files.createFile(Paths.get(OUTPUT5_PATH));
        Files.deleteIfExists(Paths.get(OUTPUT6_PATH));
        Files.createFile(Paths.get(OUTPUT6_PATH));
        Files.deleteIfExists(Paths.get(OUTPUT7_PATH));
        Files.createFile(Paths.get(OUTPUT7_PATH));
    }

    @AfterAll
    static void tearDown() {
        deleteDir(new File(TEST_PATH));
    }


    @Test
    void testTeePaste_teeFilesAndPaste_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo %s | tee %s ; paste %s %s > %s", FROM_TEE, FILE1_PATH, FILE1_PATH, FILE1_PATH, OUTPUT1_PATH);
        String expectedStdOut = FROM_TEE + STRING_NEWLINE;
        String expectedContent = FROM_TEE + STRING_TAB +
                FROM_TEE + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(OUTPUT1_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_teeStdOutOnlyAndPaste_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo %s | tee ; paste %s %s > %s", FROM_TEE, FILE1_PATH, FILE1_PATH, OUTPUT2_PATH);
        String expectedStdOut = FROM_TEE + STRING_NEWLINE;
        String expectedContent = STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(OUTPUT2_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_teeStdOutOnlyThenPaste_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo %s | tee | paste - %s > %s", FROM_TEE, FILE2_PATH, OUTPUT3_PATH);
        String expectedStdOut = "";
        String expectedContent = FROM_TEE + STRING_TAB +
                FIRST + STRING_NEWLINE +
                STRING_TAB + SECOND + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(OUTPUT3_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_pasteThenTeeAppend_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("paste %s %s > %s; echo %s | tee -a %s;", FILE2_PATH, FILE2_PATH, OUTPUT4_PATH, THIRD, OUTPUT4_PATH);
        String expectedStdOut = THIRD + STRING_NEWLINE;
        String expectedContent = FIRST  + STRING_TAB + FIRST + STRING_NEWLINE +
                SECOND + STRING_TAB + SECOND + STRING_NEWLINE +
                THIRD + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(OUTPUT4_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_pasteThenTeeNoAppend_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("paste %s %s > %s; echo %s | tee %s;", FILE2_PATH, FILE2_PATH, OUTPUT5_PATH, THIRD, OUTPUT5_PATH);
        String expectedStdOut = THIRD + STRING_NEWLINE;
        String expectedContent = THIRD + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(OUTPUT5_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_pasteWithStdinAsFileThenTeeAppend_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("paste - %s < %s > %s; echo %s | tee -a %s;", FILE2_PATH, FILE2_PATH, OUTPUT6_PATH, THIRD, OUTPUT6_PATH);
        String expectedStdOut = THIRD + STRING_NEWLINE;
        String expectedContent = FIRST  + STRING_TAB + FIRST + STRING_NEWLINE +
                SECOND + STRING_TAB + SECOND + STRING_NEWLINE +
                THIRD + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(OUTPUT6_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_pasteErrorThenTeeAppend_shouldThrowError() throws Exception {
        String commandString = String.format("paste -z; echo %s | tee -a %s;", THIRD, OUTPUT7_PATH);
        String expectedStdOut = "paste: invalid option -- 'z'" + STRING_NEWLINE +
                THIRD + STRING_NEWLINE;
        String expectedContent = THIRD + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(OUTPUT7_PATH));
        assertEquals(expectedContent, fileContent);
    }
}
