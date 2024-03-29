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
import java.nio.file.Paths;

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

    public static final String TMP_OUTPUT_FILE1 = "tmpOutputFile1.txt";
    public static final String TMP_OUTPUT_FILE2 = "tmpOutputFile2.txt";
    public static final String TMP_OUTPUT_FILE3 = "tmpOutputFile3.txt";
    public static final String TMP_OUTPUT_FILE4 = "tmpOutputFile4.txt";
    public static final String TMP_OUTPUT_FILE5 = "tmpOutputFile5.txt";
    public static final String TMP_OUTPUT_FILE6 = "tmpOutputFile6.txt";
    public static final String TMP_OUTPUT_FILE7 = "tmpOutputFile7.txt";
    public static final String TMP_OUTPUT_FILE8 = "tmpOutputFile8.txt";
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
    void setUpEach() {
        stdOut = new ByteArrayOutputStream();
    }

    @BeforeAll
    static void setUp() throws IOException {
        shell = new ShellImpl();

        Environment.currentDirectory = ROOT_PATH;

        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_1));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(TEST_PATH + TMP_OUTPUT_FILE3));
        Files.createFile(Paths.get(TEST_PATH + TMP_OUTPUT_FILE4));
        Files.createFile(Paths.get(TEST_PATH + TMP_OUTPUT_FILE5));
        Files.createFile(Paths.get(TEST_PATH + TMP_OUTPUT_FILE8));
        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);
        appendToFile(Paths.get(TEST_PATH + TMP_OUTPUT_FILE3), LINES2);
        appendToFile(Paths.get(TEST_PATH + TMP_OUTPUT_FILE4), LINES2);
        appendToFile(Paths.get(TEST_PATH + TMP_OUTPUT_FILE5), LINES2);
        appendToFile(Paths.get(TEST_PATH + TMP_OUTPUT_FILE8), LINES2);
        Environment.currentDirectory = TEST_PATH;
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testCatTeeParseAndEvaluate_catOneFileAndTee_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat %s | tee %s; cat %s", FILE1_PATH, TMP_OUTPUT_FILE1, TMP_OUTPUT_FILE1);
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE +
                LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_catMultipleFilesAndTee_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat %s %s | tee %s; cat %s", FILE1_PATH, FILE2_PATH, TMP_OUTPUT_FILE2, TMP_OUTPUT_FILE2);
        String partialOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE +
                "THE first file" + STRING_NEWLINE + "THE SECOND liNE" + STRING_NEWLINE + "10" + STRING_NEWLINE;

        String expectedOutput = partialOutput + partialOutput;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_teeInputWithoutAppendFlagAndCat_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("tee %s < %s; cat %s", TMP_OUTPUT_FILE3, FILE1_PATH, TMP_OUTPUT_FILE3);
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE +
                LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE;

        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_teeInputWithoutAppendFlagAndCatFlag_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("tee %s < %s; cat -n %s", TMP_OUTPUT_FILE4, FILE1_PATH, TMP_OUTPUT_FILE4);
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 1) + LINES1_TEXT1 + STRING_NEWLINE + String.format(NUMBER_FORMAT, 2) + LINES1_TEXT2 + STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 3) + LINES1_TEXT3 + STRING_NEWLINE;

        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_teeInputWithAppendFlagAndCat_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("tee -a %s < %s; cat %s", TMP_OUTPUT_FILE5, FILE1_PATH, TMP_OUTPUT_FILE5);
        String expectedOutput = LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE +
                "THE first file" + STRING_NEWLINE + "THE SECOND liNE" + STRING_NEWLINE + "10" + STRING_NEWLINE +
                LINES1_TEXT1 + STRING_NEWLINE + LINES1_TEXT2 + STRING_NEWLINE + LINES1_TEXT3 + STRING_NEWLINE;

        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_catInvalidFilePathAndTee_shouldReturnEmptyTeeFile() throws Exception {
        String commandString = String.format("cat %s | tee %s; cat %s", NE_FILE_NAME, TMP_OUTPUT_FILE6, TMP_OUTPUT_FILE6);
        String partialOutput = "cat: " + NE_FILE_NAME + ": No such file or directory" + STRING_NEWLINE;
        String expectedOutput = partialOutput + partialOutput;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_catFolderAndTee_shouldReturnEmptyTeeFile() throws Exception {
        String commandString = String.format("cat %s | tee %s; cat %s", TEST_PATH + FOLDER_1, TMP_OUTPUT_FILE7, TMP_OUTPUT_FILE7);
        String partialOutput = "cat: " + TEST_PATH + FOLDER_1 + ": Is a directory" + STRING_NEWLINE;
        String expectedOutput = partialOutput + partialOutput;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testCatTeeParseAndEvaluate_teeInputWithInvalidFlagAndCat_shouldThrowTeeException() throws Exception {
        String commandString = String.format("tee -q %s < %s | cat %s", TMP_OUTPUT_FILE8, FILE1_PATH, TMP_OUTPUT_FILE8);
        assertThrows(TeeException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }
}
