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
    public static final String FILE3_NAME = "file3.txt";
    public static final String FILE3_PATH = TEST_PATH + FILE3_NAME;

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
        Files.deleteIfExists(Paths.get(FILE3_PATH));
        Files.createFile(Paths.get(FILE3_PATH));
        appendToFile(Paths.get(FILE3_PATH), LINES);
    }

    @AfterAll
    static void tearDown() {
        deleteDir(new File(TEST_PATH));
    }


    @Test
    void testTeePaste_teeFilesAndPaste_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo %s | tee %s ; paste %s %s > %s", FROM_TEE, FILE1_PATH, FILE1_PATH, FILE1_PATH, FILE2_PATH);
        String expectedStdOut = FROM_TEE + STRING_NEWLINE;
        String expectedContent = FROM_TEE + STRING_TAB +
                FROM_TEE + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(FILE2_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_teeStdOutOnlyAndPaste_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo %s | tee ; paste %s %s > %s", FROM_TEE, FILE1_PATH, FILE1_PATH, FILE2_PATH);
        String expectedStdOut = FROM_TEE + STRING_NEWLINE;
        String expectedContent = STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(FILE2_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_teeStdOutOnlyThenPaste_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("echo %s | tee | paste - %s > %s", FROM_TEE, FILE3_PATH, FILE2_PATH);
        String expectedStdOut = "";
        String expectedContent = FROM_TEE + STRING_TAB +
                FIRST + STRING_NEWLINE +
                STRING_TAB + SECOND + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(FILE2_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_pasteThenTeeAppend_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("paste %s %s > %s; echo %s | tee -a %s;", FILE3_PATH, FILE3_PATH, FILE2_PATH, THIRD, FILE2_PATH);
        String expectedStdOut = THIRD + STRING_NEWLINE;
        String expectedContent = FIRST  + STRING_TAB + FIRST + STRING_NEWLINE +
                SECOND + STRING_TAB + SECOND + STRING_NEWLINE +
                THIRD + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(FILE2_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_pasteThenTeeNoAppend_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("paste %s %s > %s; echo %s | tee %s;", FILE3_PATH, FILE3_PATH, FILE2_PATH, THIRD, FILE2_PATH);
        String expectedStdOut = THIRD + STRING_NEWLINE;
        String expectedContent = THIRD + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(FILE2_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_pasteWithStdinAsFileThenTeeAppend_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("paste - %s < %s > %s; echo %s | tee -a %s;", FILE3_PATH, FILE3_PATH, FILE2_PATH, THIRD, FILE2_PATH);
        String expectedStdOut = THIRD + STRING_NEWLINE;
        String expectedContent = FIRST  + STRING_TAB + FIRST + STRING_NEWLINE +
                SECOND + STRING_TAB + SECOND + STRING_NEWLINE +
                THIRD + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(FILE2_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testTeePaste_pasteErrorThenTeeAppend_shouldThrowError() throws Exception {
        String commandString = String.format("paste -z; echo %s | tee -a %s;", THIRD, FILE2_PATH);
        String expectedStdOut = "paste: invalid option -- 'z'" + STRING_NEWLINE +
                THIRD + STRING_NEWLINE;
        String expectedContent = THIRD + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedStdOut, stdOut.toString());
        String fileContent = readString(Paths.get(FILE2_PATH));
        assertEquals(expectedContent, fileContent);
    }
}
