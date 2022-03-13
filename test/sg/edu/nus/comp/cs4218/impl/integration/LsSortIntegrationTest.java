package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;
import sg.edu.nus.comp.cs4218.exception.SortException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.impl.util.TestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.LS_SORT_FOLDER;

public class LsSortIntegrationTest {

    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = LS_SORT_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    static final String STRING_FORMAT = " %s";
    private static final String CAT_NUMBER_FORMAT = "%6d ";

    private static final String FILE_NAME_1 = "a.txt";
    private static final String FILE_PATH_1 = TEST_FOLDER_NAME + FILE_NAME_1;
    private static final String FILE_NAME_2 = "b.txt";
    private static final String FILE_PATH_2 = TEST_FOLDER_NAME + FILE_NAME_2;
    private static final String FILE_NAME_3 = "a";
    private static final String FILE_PATH_3 = TEST_FOLDER_NAME + FILE_NAME_3;
    private static final String FILE_NAME_4 = "b";
    private static final String FILE_PATH_4 = TEST_FOLDER_NAME + FILE_NAME_4;
    private static final String FILE_NAME_5 = "AB.txt";
    private static final String FILE_PATH_5 = TEST_FOLDER_NAME + FILE_NAME_5;
    private static final String FOLDER_NAME_1= "folder1" + CHAR_FILE_SEP;
    private static final String FILE_NAME_6 = "a.txt";
    private static final String FILE_NAME_7 = "b.txt";
    private static final String FILE_PATH_6 = TEST_FOLDER_NAME + FOLDER_NAME_1 + FILE_NAME_6;
    private static final String FILE_PATH_7 = TEST_FOLDER_NAME + FOLDER_NAME_1 + FILE_NAME_7;

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_NAME_1));

        String file_content = "file content can be anything";
        TestUtils.createFile(FILE_PATH_1, file_content);
        TestUtils.createFile(FILE_PATH_2, file_content);
        TestUtils.createFile(FILE_PATH_3, file_content);
        TestUtils.createFile(FILE_PATH_4, file_content);
        TestUtils.createFile(FILE_PATH_5, file_content);
        TestUtils.createFile(FILE_PATH_6, file_content);
        TestUtils.createFile(FILE_PATH_7, file_content);
    }

    @BeforeEach
    void setUpEach() {
        Environment.currentDirectory = TEST_PATH;
        myOut = new ByteArrayOutputStream();
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    @Test
    void testLsSort_forwardLsToSort_testPassed() throws Exception {
        String inputString = "ls | sort";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "AB.txt" + STRING_NEWLINE + "a" + STRING_NEWLINE + "a.txt" + STRING_NEWLINE + "b" +
                STRING_NEWLINE + "b.txt" + STRING_NEWLINE + "folder1";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testLsSort_forwardLsToSortInReverse_testPassed() throws Exception {
        String inputString = "ls | sort -r";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "folder1" + STRING_NEWLINE + "b.txt" + STRING_NEWLINE + "b" + STRING_NEWLINE +
                "a.txt" + STRING_NEWLINE + "a" + STRING_NEWLINE + "AB.txt";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testLsSort_forwardLsWithTxtFilesToSort_testPassed() throws Exception {
        String inputString = "ls AB.txt a.txt b.txt | sort -f";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "a.txt" + STRING_NEWLINE + "AB.txt" + STRING_NEWLINE + "b.txt";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testLsSort_forwardLsInRecursiveAndAlphabeticallyByFileExtToSort_testPassed() throws Exception {
        String inputString = "ls -R -X | sort";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected =  STRING_NEWLINE + "." + CHAR_FILE_SEP + ":" + STRING_NEWLINE + "AB.txt" + STRING_NEWLINE + "a" + STRING_NEWLINE +
                "a.txt" + STRING_NEWLINE + "a.txt" + STRING_NEWLINE + "b" + STRING_NEWLINE + "b.txt" + STRING_NEWLINE +
                "b.txt" + STRING_NEWLINE + "folder1" + STRING_NEWLINE + "folder1:";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testLsSort_forwardLsWithFoldersToSort_testPassed() throws Exception {
        String inputString = "ls folder1 | sort";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "a.txt" + STRING_NEWLINE + "b.txt";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testLsSort_forwardLsWithNonExistentFileToSort_testPassed() throws Exception {
        String inputString = "ls blabla.txt | sort";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "ls: cannot access 'blabla.txt': No such file or directory";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testLsSort_forwardLsWithInvalidOptionToSort_testPassed() throws Exception {
        String inputString = "ls -Z | sort";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(LsException.class, () -> command.evaluate(System.in, myOut), "Should throw LsException");
    }

    @Test
    void testLsSort_forwardLsToSortWithInvalidOption_testPassed() throws Exception {
        String inputString = "ls | sort -Z";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(SortException.class, () -> command.evaluate(System.in, myOut), "Should throw SortException");
    }
}
