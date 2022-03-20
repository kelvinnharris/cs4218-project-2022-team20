package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
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
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class LsCatIntegrationTest {

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpLsCatTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    public static final String FOLDER1 = "folder1";
    public static final String FOLDER1_PATH = TEST_PATH + FOLDER1;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = FOLDER1_PATH + CHAR_FILE_SEP + FILE1_NAME;
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE2_PATH = TEST_PATH + FILE2_NAME;

    private static final String FIRST1 = "first1";
    private static final String FIRST2 = "first2";
    private static final String SECOND1 = "second1";
    private static final String SECOND2 = "second2";

    public static final String[] LINES1 = {FIRST1, FIRST2};
    public static final String[] LINES2 = {SECOND1, SECOND2};


    @BeforeAll
    static void setUp() throws IOException {
        shell = new ShellImpl();
        Environment.currentDirectory = TEST_PATH;
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
        Files.deleteIfExists(Paths.get(FOLDER1_PATH));
        Files.createDirectories(Paths.get(FOLDER1_PATH));
        Files.deleteIfExists(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        appendToFile(Paths.get(FILE1_PATH), LINES1);
        Files.deleteIfExists(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        appendToFile(Paths.get(FILE2_PATH), LINES2);
    }


    @BeforeEach
    void setUpEach() throws IOException {
        stdOut = new ByteArrayOutputStream();
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }


    @Test
    void testLsCatParseAndEvaluate_lsThenCat_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("ls %s | cat ", FOLDER1_PATH);
        String expected = FILE1_NAME + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testLsCatParseAndEvaluate_lsAndCat_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("ls %s ; cat %s", FILE2_PATH, FILE2_PATH);
        String expected = FILE2_NAME + STRING_NEWLINE + SECOND1 + STRING_NEWLINE + SECOND2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testLsCatParseAndEvaluate_catFromLs_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat < `ls %s`", FILE2_PATH);
        String expected = SECOND1 + STRING_NEWLINE + SECOND2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testLsCatParseAndEvaluate_catLs_shouldReturnCorrectOutput() throws Exception {
        String commandString = String.format("cat `ls %s`", FILE2_PATH);
        String expected = SECOND1 + STRING_NEWLINE + SECOND2 + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expected, stdOut.toString());
    }

    @Test
    void testLsCatParseAndEvaluate_catFromLsAnotherDir_shouldThrowException() {
        String commandString = String.format("cat < `ls %s`", FOLDER1_PATH);
        assertThrows(ShellException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }
}
