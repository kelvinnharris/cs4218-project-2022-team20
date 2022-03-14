package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.CAT_TEE_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class CatTeeIntegrationTest {

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = CAT_TEE_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    public static final String FOLDER_1 = "folder1";
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;
    public static final String FILE2_PATH = TEST_PATH + FILE2_NAME;

    public static final String TMP_OUTPUT_FILE = "tmpOutputFile.txt";
    public static final String NE_FILE_NAME = "nonExistent.txt";
    public static final String LINES1_TEXT1 = "The first file";
    public static final String LINES1_TEXT2 = "Thee second line";
    public static final String LINES1_TEXT3 = "1000";
    public static final String[] LINES1 = {LINES1_TEXT1, LINES1_TEXT2, LINES1_TEXT3};
    public static final String[] LINES2 = {"THE first file", "THE SECOND liNE", "10"};
    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    private static final String NUMBER_FORMAT = "%6d ";


    @BeforeEach
    void setUpEach() throws IOException {
        stdOut = new ByteArrayOutputStream();
        Environment.currentDirectory = ROOT_PATH;

        deleteDir(new File(TEST_PATH));

        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_1));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);
        Environment.currentDirectory = TEST_PATH;
    }

    @BeforeAll
    static void setUp() {
        shell = new ShellImpl();
    }

    @AfterAll
    static void tearDown() {
        deleteDir(new File(TEST_PATH));
    }

    // TODO - Investigate semicolon
    @Test
    void testCatTeeParseAndEvaluate_catOneFileAndTee_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat %s | tee %s; cat %s", FILE1_PATH, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    // TODO - Investigate semicolon
    @Test
    void testCatTeeParseAndEvaluate_catMultipleFilesAndTee_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat %s %s | tee %s; cat %s", FILE1_PATH, FILE2_PATH, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE +
                "THE first file" + STRING_NEWLINE + "THE SECOND liNE" + STRING_NEWLINE + "10" + STRING_NEWLINE;

        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_teeInputWithoutAppendFlagAndCat_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("tee %s < %s; cat %s", FILE2_PATH, FILE1_PATH, FILE2_PATH);
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE +
                LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE;

        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_teeInputWithoutAppendFlagAndCatFlag_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("tee %s < %s; cat -n %s", FILE2_PATH, FILE1_PATH, FILE2_PATH);
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 1) + LINES1_TEXT1 + STRING_NEWLINE + String.format(NUMBER_FORMAT, 2) + LINES1_TEXT2 + STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) + LINES1_TEXT3 + STRING_NEWLINE;

        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_teeInputWithAppendFlagAndCat_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("tee -a %s < %s; cat %s", FILE2_PATH, FILE1_PATH, FILE2_PATH);
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE +
                "THE first file" + STRING_NEWLINE + "THE SECOND liNE" + STRING_NEWLINE + "10" + STRING_NEWLINE +
                LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE;

        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    // TODO - Investigate semicolon
    @Test
    void testCatTeeParseAndEvaluate_catInvalidFilePathAndTee_shouldReturnEmptyTeeFile() throws Exception {
        String commandString = String.format("cat %s | tee %s; cat %s", NE_FILE_NAME, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        String expectedOutput = "" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    // TODO - Investigate semicolon
    @Test
    void testCatTeeParseAndEvaluate_catFolderAndTee_shouldReturnEmptyTeeFile() throws Exception {
        String commandString = String.format("cat %s | tee %s; cat %s", TEST_PATH + FOLDER_1, TMP_OUTPUT_FILE, TMP_OUTPUT_FILE);
        String expectedOutput = "" + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_teeInputWithInvalidFlagAndCat_shouldThrowTeeException() throws Exception {
        String commandString = String.format("tee -q %s < %s | cat %s", FILE2_PATH, FILE1_PATH, FILE2_PATH);
        assertThrows(TeeException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }
}
