package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CatException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
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
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.WC_CAT_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class WcCatIntegrationTest {

    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = WC_CAT_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    static final String WC_NUMBER_FORMAT = " %7d";
    static final String STRING_FORMAT = " %s";
    private static final String CAT_NUMBER_FORMAT = "%6d ";

    private static final String FILE_NAME_1 = "file1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;

    private static final String CAT_EXCEPTION_MSG = "Should throw CatException";
    private static final String WC_EXCEPTION_MSG = "Should throw WcException";

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
        TestUtils.createFile(FILE_PATH_1, "This is text file 1 without any new line");
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
    void testWcCatParseCommand_forwardCatToWc_testPassed() throws Exception {
        String inputString = "cat file1.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 41;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 42;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 1) +
                String.format(WC_NUMBER_FORMAT, 9) +
                String.format(WC_NUMBER_FORMAT, totalByte);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardWcToCat_testPassed() throws Exception {
        String inputString = "wc file1.txt | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = String.format(WC_NUMBER_FORMAT, 0) +
                String.format(WC_NUMBER_FORMAT, 9) +
                String.format(WC_NUMBER_FORMAT, 40) +
                String.format(STRING_FORMAT, FILE_NAME_1);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardCatWithLineFlagToWc_testPassed() throws Exception {
        String inputString = "cat -n file1.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 48;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 49;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 1) +
                String.format(WC_NUMBER_FORMAT, 10) +
                String.format(WC_NUMBER_FORMAT, totalByte);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardWcWithWordsAndLinesToCat_testPassed() throws Exception {
        String inputString = "wc -wl file1.txt | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = String.format(WC_NUMBER_FORMAT, 0) +
                String.format(WC_NUMBER_FORMAT, 9) +
                String.format(STRING_FORMAT, FILE_NAME_1);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardCatWithInputRedirectionToWc_testPassed() throws Exception {
        String inputString = "cat -n < file1.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 48;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 49;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 1) +
                String.format(WC_NUMBER_FORMAT, 10) +
                String.format(WC_NUMBER_FORMAT, totalByte);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardCatWithInputRedirectionToWcWithWordsAndBytes_testPassed() throws Exception {
        String inputString = "cat -n < file1.txt | wc -wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 48;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 49;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 10) +
                String.format(WC_NUMBER_FORMAT, totalByte);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardWcWithInputRedirectionToCat_testPassed() throws Exception {
        String inputString = "wc < file1.txt | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = String.format(WC_NUMBER_FORMAT, 0) +
                String.format(WC_NUMBER_FORMAT, 9) +
                String.format(WC_NUMBER_FORMAT, 40);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardWcWithInputRedirectionToCatWithNumbers_testPassed() throws Exception {
        String inputString = "wc < file1.txt | cat -n";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = String.format(WC_NUMBER_FORMAT, 0) +
                String.format(WC_NUMBER_FORMAT, 9) +
                String.format(WC_NUMBER_FORMAT, 40);
        assertEquals(String.format(CAT_NUMBER_FORMAT, 1) + sbExpected + StringUtils.STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardCatToWcWithAnotherFile_testPassed() throws Exception {
        String inputString = "cat file1.txt | wc - file1.txt";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 41;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 42;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 1) +
                String.format(WC_NUMBER_FORMAT, 9) +
                String.format(WC_NUMBER_FORMAT, totalByte) +
                String.format(STRING_FORMAT, "-") + STRING_NEWLINE +
                String.format(WC_NUMBER_FORMAT, 0) +
                String.format(WC_NUMBER_FORMAT, 9) +
                String.format(WC_NUMBER_FORMAT, 40) +
                String.format(STRING_FORMAT, FILE_NAME_1) + STRING_NEWLINE +
                String.format(WC_NUMBER_FORMAT, 1) +
                String.format(WC_NUMBER_FORMAT, 18) +
                String.format(WC_NUMBER_FORMAT, 40 + totalByte) +
                String.format(STRING_FORMAT, "total");
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardWcToCatWithAnotherFile_testPassed() throws Exception {
        String inputString = "wc file1.txt | cat - file1.txt";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = String.format(WC_NUMBER_FORMAT, 0) +
                String.format(WC_NUMBER_FORMAT, 9) +
                String.format(WC_NUMBER_FORMAT, 40) +
                String.format(STRING_FORMAT, FILE_NAME_1) + STRING_NEWLINE +
                "This is text file 1 without any new line";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardCatWithInvalidOptionToWc_testThrowsException() throws Exception {
        String inputString = "cat file1.txt -l | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(CatException.class, () -> command.evaluate(System.in, myOut), CAT_EXCEPTION_MSG);
    }

    @Test
    void testWcCatParseCommand_forwardWcWithInvalidOptionToCat_testThrowsException() throws Exception {
        String inputString = "wc file1.txt -z | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(WcException.class, () -> command.evaluate(System.in, myOut), WC_EXCEPTION_MSG);
    }

    @Test
    void testWcCatParseCommand_forwardCatToWcWithInvalidOption_testThrowsException() throws Exception {
        String inputString = "cat file1.txt | wc -z";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(WcException.class, () -> command.evaluate(System.in, myOut), WC_EXCEPTION_MSG);
    }

    @Test
    void testWcCatParseCommand_forwardWcToCatWithInvalidOption_testThrowsException() throws Exception {
        String inputString = "wc file1.txt | cat -z";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(CatException.class, () -> command.evaluate(System.in, myOut), CAT_EXCEPTION_MSG);
    }

    @Test
    void testWcCatParseCommand_forwardCatWithNonExistentFileToWc_testThrowsException() throws Exception {
        String inputString = "cat blabla.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 43;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 44;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 1) +
                String.format(WC_NUMBER_FORMAT, 7) +
                String.format(WC_NUMBER_FORMAT, totalByte);

        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCatParseCommand_forwardWcWithNonExistentFileToCat_testThrowsException() throws Exception {
        String inputString = "wc blabla.txt | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "wc: blabla.txt: No such file or directory";

        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }
}
