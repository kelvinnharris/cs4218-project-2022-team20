package sg.edu.nus.comp.cs4218.impl.Hackathon;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.RM_CD_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class HackathonBugs2 {
    /* before each file path:
        > folder1
           > file1.txt
           > folder2
              > file2.txt
        > file3.xml
     */

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = RM_CD_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FOLDER_1 = "folder1";
    private static final String FOLDER_2 = "folder2";
    private static final String FOLDER_3 = "folder3";
    private static final String FILE_1 = "file1.txt";
    private static final String FILE_2 = "file2.txt";
    private static final String FILE_3 = "file3.xml";

    private static ShellImpl shell;
    private static ByteArrayOutputStream stdOut;

    @BeforeEach
    void setUpEach() throws IOException {
        stdOut = new ByteArrayOutputStream();

        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_3));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_1));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2 + CHAR_FILE_SEP + FILE_2));
        Files.createFile(Paths.get(TEST_PATH + FILE_3));
        Environment.currentDirectory = TEST_PATH;
    }


    @BeforeAll
    static void setUp() {
        shell = new ShellImpl();
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testCdLsParseAndEvaluateFromP4_cdThroughPipe_shouldNotChangeDirectory() throws Exception { // fromP_4
        String commandString = "cd \"" + FOLDER_1 + "\" | ls";
        String expectedOutput = FILE_3 + STRING_NEWLINE + FOLDER_1 + STRING_NEWLINE + FOLDER_3;
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    @Test
    void testLsParseAndEvaluateFromP5_lsOnCurrentEmptyDirectory_shouldNotReturnNewline() throws Exception { // fromP_5
        Environment.currentDirectory = TEST_PATH + CHAR_FILE_SEP + FOLDER_3;
        String commandString = "ls";
        String expectedOutput = "";
        shell.parseAndEvaluate(commandString, stdOut);
        assertEquals(expectedOutput, stdOut.toString());
    }

    // From CS4218 Project description: If the PATH does not exist, raise exception
    @Test
    void testCdParseAndEvaluateFromP16_cdWithNoPathExists_shouldNotReturnNewline() throws Exception { // fromP_16
        String commandString = "cd";
        assertThrows(CdException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }

    // From CS4218 Project description: If the PATH does not exist, raise exception
    @Test
    void testCdParseAndEvaluateFromS7_cdWithNoPathExists_shouldNotReturnNewline() throws Exception { // fromS_7
        String commandString = "cd";
        assertThrows(CdException.class, () -> shell.parseAndEvaluate(commandString, stdOut));
    }
}
