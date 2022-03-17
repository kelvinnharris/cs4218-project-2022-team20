package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.CutException;
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
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.CAT_CUT_FOLDER;

public class CatCutIntegrationTest {

    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = CAT_CUT_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String CAT_NUMBER_FORMAT = "%6d ";

    private static final String FILE_NAME_1 = "file1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_2 = "file2.txt";
    private static final String FILE_PATH_2 = TEST_PATH + FILE_NAME_2;

    private static final String CAT_EXCEPTION_MSG = "Should throw CutException";
    private static final String CUT_EXCEPTION_MSG = "Should throw CutException";

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        String contentOne = "This is the content of the first file";
        TestUtils.createFile(FILE_PATH_1, contentOne);
        String contentTwo = "Second file";
        TestUtils.createFile(FILE_PATH_2, contentTwo);
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
    void testCatCutParseCommand_forwardCatToCut_testPassed() throws Exception {
        String inputString = "cat file1.txt | cut -c 1,2";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "Th";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCatCutParseCommand_forwardCutToCat_testPassed() throws Exception {
        String inputString = "cut -c 1,2 file1.txt | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "Th";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCatCutParseCommand_forwardCatWithNumberOptionToCutWithRange_testPassed() throws Exception {
        String inputString = "cat -n file1.txt | cut -c 1-8";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(CAT_NUMBER_FORMAT, 1)).append('T');
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCatCutParseCommand_forwardCutWithRangeToCat_testPassed() throws Exception {
        String inputString = "cut -c 1-8 file1.txt | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "This is ";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCatCutParseCommand_forwardCatWithNumberAndMultipleFilesToCutWithBytes_testPassed() throws Exception {
        String inputString = "cat -n file1.txt file2.txt | cut -b 1-10";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(CAT_NUMBER_FORMAT, 1)).append("Thi").append(STRING_NEWLINE)
                .append(String.format(CAT_NUMBER_FORMAT, 2)).append("Sec");
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCatCutParseCommand_forwardCutWithBytesRangeAndMultipleFilesToCatWithNumbers_testPassed() throws Exception {
        String inputString = "cut -b 1-10 file1.txt file2.txt | cat -n";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = String.format(CAT_NUMBER_FORMAT, 1) + "This is th" + STRING_NEWLINE +
                String.format(CAT_NUMBER_FORMAT, 2) + "Second fil";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCatCutParseCommand_forwardCatToCutWithAnotherFile_testPassed() throws Exception {
        String inputString = "cat file1.txt | cut -c 1-4 - file2.txt";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "This" + STRING_NEWLINE + "Seco";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCatCutParseCommand_forwardCutToCatWithAnotherFile_testPassed() throws Exception {
        String inputString = "cut -c 1-4 file1.txt | cat -n - file2.txt";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(CAT_NUMBER_FORMAT, 1)).append("This").append(STRING_NEWLINE)
                .append(String.format(CAT_NUMBER_FORMAT, 2))
                .append("Second file");
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCatCutParseCommand_forwardCatWithInvalidOptionToCut_testThrowsException() throws Exception {
        String inputString = "cat file1.txt -l | cut -c 1,2";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(CatException.class, () -> command.evaluate(System.in, myOut), CAT_EXCEPTION_MSG);
    }

    @Test
    void testCatCutParseCommand_forwardCutWithMissingFirstOptionToCat_testThrowsException() throws Exception {
        String inputString = "cut 1,2 file1.txt | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(CutException.class, () -> command.evaluate(System.in, myOut), CUT_EXCEPTION_MSG);
    }

    @Test
    void testPasteWcParseCommand_forwardPasteToWcWithInvalidOption_testThrowsException() throws Exception {
        String inputString = "cut -c file1.txt | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(CutException.class, () -> command.evaluate(System.in, myOut), CUT_EXCEPTION_MSG);
    }

    @Test
    void testCatCutParseCommand_forwardCatToCutWithFirstMissingOption_testThrowsException() throws Exception {
        String inputString = "cat file1.txt | cut 1,2";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(CutException.class, () -> command.evaluate(System.in, myOut), CUT_EXCEPTION_MSG);
    }

    @Test
    void testCatCutParseCommand_forwardCatToCutWithSecondMissingOption_testThrowsException() throws Exception {
        String inputString = "cat file1.txt | cut -c";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(CutException.class, () -> command.evaluate(System.in, myOut), CUT_EXCEPTION_MSG);
    }

    // TODO: Put in assumption when file not exist cat will not throw exception but return with error message
    //  to STDOUT
    @Test
    void testCatCutParseCommand_forwardCatWithNonExistentFileToCut_testThrowsException() throws Exception {
        String inputString = "cat blabla.txt | cut -c 1-42";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "cat: blabla.txt: No such file or directory";

        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCatCutParseCommand_forwardCutWithNonExistentFileToCat_testThrowsException() throws Exception {
        String inputString = "cut -c 1-10 blabla.txt | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(CutException.class, () -> command.evaluate(System.in, myOut), CUT_EXCEPTION_MSG);
    }
}
