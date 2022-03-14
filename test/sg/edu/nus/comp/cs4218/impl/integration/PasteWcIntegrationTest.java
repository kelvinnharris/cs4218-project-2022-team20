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

public class PasteWcIntegrationTest {

    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = PASTE_WC_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    static final String WC_NUMBER_FORMAT = " %7d";
    static final String STRING_FORMAT = " %s";

    private static final String FILE_NAME_1 = "file1.txt";
    private static final String FILE_PATH_1 = TEST_FOLDER_NAME + FILE_NAME_1;
    private static final String FILE_NAME_2 = "file2.txt";
    private static final String FILE_PATH_2 = TEST_FOLDER_NAME + FILE_NAME_2;

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        String file_content_1 = "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D";
        TestUtils.createFile(FILE_PATH_1, file_content_1);
        String file_content_2 = "1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "3" + STRING_NEWLINE + "4";
        TestUtils.createFile(FILE_PATH_2, file_content_2);
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
    void testWcPaste_forwardWcToPaste_testPassed() throws Exception {
        String inputString = "wc file1.txt | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 7;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 10;
        }

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 3))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, FILE_NAME_1));
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWc_forwardPasteToWc_testPassed() throws Exception {
        String inputString = "paste file1.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 8;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 12;
        }

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, totalByte));
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcPaste_forwardWcWithByteAndLineOptionsToPaste_testPassed() throws Exception {
        String inputString = "wc -wl file1.txt | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 3))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(STRING_FORMAT, FILE_NAME_1));
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWc_forwardPasteInSerialToWc_testPassed() throws Exception {
        String inputString = "paste -s file1.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 8;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 9;
        }

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 1))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, totalByte));
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcPaste_forwardWcWithMultipleFilesToPaste_testPassed() throws Exception {
        String inputString = "wc file1.txt file2.txt | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 7;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 10;
        }

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 3))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, FILE_NAME_1)).append(STRING_NEWLINE)
                .append(String.format(WC_NUMBER_FORMAT, 3))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, FILE_NAME_2)).append(STRING_NEWLINE)
                .append(String.format(WC_NUMBER_FORMAT, 6))
                .append(String.format(WC_NUMBER_FORMAT, 8))
                .append(String.format(WC_NUMBER_FORMAT, totalByte + totalByte))
                .append(String.format(STRING_FORMAT, "total"));
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWc_forwardPasteWithMultipleFilesToWc_testPassed() throws Exception {
        String inputString = "paste file1.txt file2.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 16;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 20;
        }

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, 8))
                .append(String.format(WC_NUMBER_FORMAT, totalByte));
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcPaste_forwardWcWithMultipleFilesToPasteInSerial_testPassed() throws Exception {
        String inputString = "wc file1.txt file2.txt | paste -s";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 7;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 10;
        }

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 3))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, FILE_NAME_1)).append(STRING_TAB)
                .append(String.format(WC_NUMBER_FORMAT, 3))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, FILE_NAME_2)).append(STRING_TAB)
                .append(String.format(WC_NUMBER_FORMAT, 6))
                .append(String.format(WC_NUMBER_FORMAT, 8))
                .append(String.format(WC_NUMBER_FORMAT, totalByte + totalByte))
                .append(String.format(STRING_FORMAT, "total"));
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWc_forwardPasteWithMultipleFilesToWcWithWordLineOption_testPassed() throws Exception {
        String inputString = "paste file1.txt file2.txt | wc -wl";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();


        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, 8));
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcPaste_forwardWcToPasteWithAnotherFileInSerial_testPassed() throws Exception {
        String inputString = "wc file1.txt | paste -s - file2.txt";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 7;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 10;
        }

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 3))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, "file1.txt")).append(STRING_NEWLINE)
                .append("1").append(STRING_TAB)
                .append("2").append(STRING_TAB)
                .append("3").append(STRING_TAB)
                .append("4");
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testPasteWc_forwardPasteToWcWithAnotherFile_testPassed() throws Exception {
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

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, totalByte2))
                .append(String.format(STRING_FORMAT, "-")).append(STRING_NEWLINE)
                .append(String.format(WC_NUMBER_FORMAT, 3))
                .append(String.format(WC_NUMBER_FORMAT, 4))
                .append(String.format(WC_NUMBER_FORMAT, totalByte1))
                .append(String.format(STRING_FORMAT, "file2.txt")).append(STRING_NEWLINE)
                .append(String.format(WC_NUMBER_FORMAT, 7))
                .append(String.format(WC_NUMBER_FORMAT, 8))
                .append(String.format(WC_NUMBER_FORMAT, totalByte1 + totalByte2))
                .append(String.format(STRING_FORMAT, "total"));
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput); //8 7 15
    }

    @Test
    void testPasteWc_forwardPasteWithInvalidOptionToWc_testThrowsException() throws Exception {
        String inputString = "paste file1.txt -l | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(PasteException.class, () -> command.evaluate(System.in, myOut), "Should throw PasteException");
    }

    @Test
    void testWcPaste_forwardWcWithInvalidOptionToPaste_testThrowsException() throws Exception {
        String inputString = "wc file1.txt -z | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(WcException.class, () -> command.evaluate(System.in, myOut), "Should throw WcException");
    }

    @Test
    void tesPasteWc_forwardPasteToWcWithInvalidOption_testThrowsException() throws Exception {
        String inputString = "paste file1.txt | wc -z";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(WcException.class, () -> command.evaluate(System.in, myOut), "Should throw WcException");
    }

    @Test
    void testWcPaste_forwardWcToPasteWithInvalidOption_testThrowsException() throws Exception {
        String inputString = "wc file1.txt | paste -z";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        assertThrows(PasteException.class, () -> command.evaluate(System.in, myOut), "Should throw PasteException");
    }

    // TODO: Put this behavior in Assumption ??
    @Test
    void testPasteWc_forwardPasteWithNonExistentFileToWc_testThrowsException() throws Exception {
        String inputString = "paste blabla.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(WC_NUMBER_FORMAT, 1))
                .append(String.format(WC_NUMBER_FORMAT, 8))
                .append(String.format(WC_NUMBER_FORMAT, 48));

        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcPaste_forwardWcWithNonExistentFileToPaste_testThrowsException() throws Exception {
        String inputString = "wc blabla.txt | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "wc: blabla.txt: No such file or directory";

        assertEquals(expected + STRING_NEWLINE,standardOutput);
    }
}
