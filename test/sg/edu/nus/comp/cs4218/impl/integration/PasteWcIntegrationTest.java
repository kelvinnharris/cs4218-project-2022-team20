package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.PasteException;
import sg.edu.nus.comp.cs4218.exception.WcException;
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
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.PASTE_WC_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class PasteWcIntegrationTest {

    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = PASTE_WC_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    static final String WC_NUMBER_FORMAT = " %7d";
    static final String STRING_FORMAT = " %s";

    private static final String FILE_NAME_1 = "file1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_2 = "file2.txt";
    private static final String FILE_PATH_2 = TEST_PATH + FILE_NAME_2;

    private static final String WC_EXCEPTION_MSG = "Should throw WcException";
    private static final String PASTE_EXCPTN_MSG = "Should throw PasteException";

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        String contentOne = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D";
        TestUtils.createFile(FILE_PATH_1, contentOne);
        String contentTwo = "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "3" + STRING_NEWLINE + "4";
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
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void testPasteWcParseCommand_forwardWcToPaste_testPassed() throws Exception {
        String inputString = "wc file1.txt | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 7;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 10;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 3) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, totalByte) +
                String.format(STRING_FORMAT, FILE_NAME_1);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWcParseCommand_forwardPasteToWc_testPassed() throws Exception {
        String inputString = "paste file1.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 8;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 12;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, totalByte);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWcParseCommand_forwardWcWithByteAndLineOptionsToPaste_testPassed() throws Exception {
        String inputString = "wc -wl file1.txt | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = String.format(WC_NUMBER_FORMAT, 3) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(STRING_FORMAT, FILE_NAME_1);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWcParseCommand_forwardPasteInSerialToWc_testPassed() throws Exception {
        String inputString = "paste -s file1.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 8;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 9;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 1) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, totalByte);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWcParseCommand_forwardWcWithMultipleFilesToPaste_testPassed() throws Exception {
        String inputString = "wc file1.txt file2.txt | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 7;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 10;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 3) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, totalByte) +
                String.format(STRING_FORMAT, FILE_NAME_1) + STRING_NEWLINE +
                String.format(WC_NUMBER_FORMAT, 3) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, totalByte) +
                String.format(STRING_FORMAT, FILE_NAME_2) + STRING_NEWLINE +
                String.format(WC_NUMBER_FORMAT, 6) +
                String.format(WC_NUMBER_FORMAT, 8) +
                String.format(WC_NUMBER_FORMAT, totalByte + totalByte) +
                String.format(STRING_FORMAT, "total");
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWcParseCommand_forwardPasteWithMultipleFilesToWc_testPassed() throws Exception {
        String inputString = "paste file1.txt file2.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 16;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 20;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, 8) +
                String.format(WC_NUMBER_FORMAT, totalByte);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWcParseCommand_forwardWcWithMultipleFilesToPasteInSerial_testPassed() throws Exception {
        String inputString = "wc file1.txt file2.txt | paste -s";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 7;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 10;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 3) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, totalByte) +
                String.format(STRING_FORMAT, FILE_NAME_1) + STRING_TAB +
                String.format(WC_NUMBER_FORMAT, 3) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, totalByte) +
                String.format(STRING_FORMAT, FILE_NAME_2) + STRING_TAB +
                String.format(WC_NUMBER_FORMAT, 6) +
                String.format(WC_NUMBER_FORMAT, 8) +
                String.format(WC_NUMBER_FORMAT, totalByte + totalByte) +
                String.format(STRING_FORMAT, "total");
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWcParseCommand_forwardPasteWithMultipleFilesToWcWithWordLineOption_testPassed() throws Exception {
        String inputString = "paste file1.txt file2.txt | wc -wl";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();


        String sbExpected = String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, 8);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWcParseCommand_forwardWcToPasteWithAnotherFileInSerial_testPassed() throws Exception {
        String inputString = "wc file1.txt | paste -s - file2.txt";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 7;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 10;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 3) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, totalByte) +
                String.format(STRING_FORMAT, "file1.txt") + STRING_NEWLINE +
                "1" + STRING_TAB +
                "2" + STRING_TAB +
                "3" + STRING_TAB +
                "4";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWcParseCommand_forwardPasteToWcWithAnotherFile_testPassed() throws Exception {
        String inputString = "paste file1.txt | wc - file2.txt";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte1 = 7;
        int totalByte2 = 8;
        if (TestUtils.isWindowsSystem()) {
            totalByte1 = 10;
            totalByte2 = 12;
        }

        String sbExpected = String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, totalByte2) +
                String.format(STRING_FORMAT, "-") + STRING_NEWLINE +
                String.format(WC_NUMBER_FORMAT, 3) +
                String.format(WC_NUMBER_FORMAT, 4) +
                String.format(WC_NUMBER_FORMAT, totalByte1) +
                String.format(STRING_FORMAT, "file2.txt") + STRING_NEWLINE +
                String.format(WC_NUMBER_FORMAT, 7) +
                String.format(WC_NUMBER_FORMAT, 8) +
                String.format(WC_NUMBER_FORMAT, totalByte1 + totalByte2) +
                String.format(STRING_FORMAT, "total");
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput); //8 7 15
    }

    @Test
    void testPasteWcParseCommand_forwardPasteWithInvalidOptionToWc_testThrowsException() throws Exception {
        String inputString = "paste file1.txt -l | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(PasteException.class, () -> command.evaluate(System.in, myOut), PASTE_EXCPTN_MSG);
    }

    @Test
    void testPasteWcParseCommand_forwardWcWithInvalidOptionToPaste_testThrowsException() throws Exception {
        String inputString = "wc file1.txt -z | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(WcException.class, () -> command.evaluate(System.in, myOut), WC_EXCEPTION_MSG);
    }

    @Test
    void tesPasteWcParseCommand_forwardPasteToWcWithInvalidOption_testThrowsException() throws Exception {
        String inputString = "paste file1.txt | wc -z";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(WcException.class, () -> command.evaluate(System.in, myOut), WC_EXCEPTION_MSG);
    }

    @Test
    void testPasteWcParseCommand_forwardWcToPasteWithInvalidOption_testThrowsException() throws Exception {
        String inputString = "wc file1.txt | paste -z";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(PasteException.class, () -> command.evaluate(System.in, myOut), PASTE_EXCPTN_MSG);
    }

    // TODO: Put this behavior in Assumption
    @Test
    void testPasteWcParseCommand_forwardPasteWithNonExistentFileToWc_testThrowsException() throws Exception {
        String inputString = "paste blabla.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(PasteException.class, () -> command.evaluate(System.in, myOut), "Should throw PasteException");
    }

    @Test
    void testPasteWcParseCommand_forwardWcWithNonExistentFileToPaste_testThrowsException() throws Exception {
        String inputString = "wc blabla.txt | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "wc: blabla.txt: No such file or directory";

        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }
}
