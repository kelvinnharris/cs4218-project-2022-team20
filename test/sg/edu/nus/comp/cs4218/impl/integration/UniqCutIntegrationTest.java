package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.UniqException;
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
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.UNIQ_CUT_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class UniqCutIntegrationTest {
    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = UNIQ_CUT_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String FILE_NAME_1 = "file1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_2 = "file2.txt";
    private static final String FILE_PATH_2 = TEST_PATH + FILE_NAME_2;

    private static final String CUT_EXCEPTN_MSG = "Should throw CutException";
    private static final String UNIQ_EXCEPTN_MSG = "Should throw UniqException";

    private static final String ALICE = "Alice";
    private static final String BOB = "Bob";

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
        String fileContent = "Hello World" + STRING_NEWLINE
                + "Hello World" + STRING_NEWLINE
                + ALICE + STRING_NEWLINE
                + ALICE + STRING_NEWLINE
                + BOB + STRING_NEWLINE
                + ALICE + STRING_NEWLINE
                + BOB + STRING_NEWLINE
                + "bOb";
        TestUtils.createFile(FILE_PATH_1, fileContent);
        TestUtils.createFile(FILE_PATH_2, "b" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D");
    }

    @BeforeEach
    void setUpEach() {
        Environment.currentDirectory = TEST_PATH;
        myOut = new ByteArrayOutputStream();
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testUniqCutParseCommand_forwardUniqToCut_testPassed() throws Exception {
        String inputString = "uniq file1.txt | cut -c 1,2";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = "He" + STRING_NEWLINE +
                "Al" + STRING_NEWLINE +
                "Bo" + STRING_NEWLINE +
                "Al" + STRING_NEWLINE +
                "Bo" + STRING_NEWLINE +
                "bO";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testUniqCutParseCommand_forwardCutToUniq_testPassed() throws Exception {
        String inputString = "cut -c 1-6 file1.txt | uniq";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = "Hello " + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                "Bob" + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                "Bob" + STRING_NEWLINE +
                "bOb";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testUniqCutParseCommand_forwardUniqWithAllDuplicateOptionToCut_testPassed() throws Exception {
        String inputString = "uniq -D file1.txt | cut -c 1,2";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = "He" + STRING_NEWLINE +
                "He" + STRING_NEWLINE +
                "Al" + STRING_NEWLINE +
                "Al";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testUniqCutParseCommand_forwardCutWithMultipleFilesToUniqWithCountAndDuplicatesOnly_testPassed() throws Exception {
        String inputString = "cut -c 1 file1.txt file2.txt | uniq -cd";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = "2 H" + STRING_NEWLINE +
                "2 A" + STRING_NEWLINE +
                "2 b";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testUniqCutParseCommand_forwardCutWithInvalidFileToUniqWithCount_throwsException() throws Exception {
        String inputString = "cut -c 1 blabla.txt | uniq -c";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(CutException.class, () -> command.evaluate(System.in, myOut), CUT_EXCEPTN_MSG);
    }

    @Test
    void testUniqCutParseCommand_forwardCutNoOptionsToUniqWithCount_throwsException() throws Exception {
        String inputString = "cut blabla.txt | uniq -c";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(CutException.class, () -> command.evaluate(System.in, myOut), CUT_EXCEPTN_MSG);
    }

    @Test
    void testUniqCutParseCommand_forwardUniqWithInvalidOptionToCut_throwsException() throws Exception {
        String inputString = "uniq -z file1.txt | cut -c 1,2";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(UniqException.class, () -> command.evaluate(System.in, myOut), UNIQ_EXCEPTN_MSG);
    }

    @Test
    void testUniqCutParseCommand_forwardUniqWithInvalidFileToCut_throwsException() throws Exception {
        String inputString = "uniq -D blabla.txt | cut -c 1,2";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(UniqException.class, () -> command.evaluate(System.in, myOut), UNIQ_EXCEPTN_MSG);
    }
}
