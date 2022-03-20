package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.GrepException;
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
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.UNIQ_GREP_FOLDER;

public class UniqGrepIntegrationTest {
    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = UNIQ_GREP_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String FILE_NAME_1 = "file1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_2 = "file2.txt";
    private static final String FILE_PATH_2 = TEST_PATH + FILE_NAME_2;

    private static final String UNIQ_EXCEPTN_MSG = "Should throw UniqException";
    private static final String GREP_EXCEPTN_MSG = "Should throw GrepException";

    private static final String BOB = "Bob";
    private static final String HELLO_WORLD = "Hello World";
    private static final String ALICE = "Alice";

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
        String fileContent = HELLO_WORLD + STRING_NEWLINE
                + HELLO_WORLD + STRING_NEWLINE
                + ALICE + STRING_NEWLINE
                + ALICE + STRING_NEWLINE
                + BOB + STRING_NEWLINE
                + ALICE + STRING_NEWLINE
                + BOB + STRING_NEWLINE
                + "bOb";
        TestUtils.createFile(FILE_PATH_1, fileContent);
        TestUtils.createFile(FILE_PATH_2, "A" + STRING_NEWLINE + "B" + STRING_NEWLINE + "C" + STRING_NEWLINE + "D" + STRING_NEWLINE + "b");
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
    void testUniqGrepParseCommand_forwardUniqToGrep_testPassed() throws Exception {
        String inputString = "uniq file1.txt | grep Bob";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = BOB + STRING_NEWLINE + BOB;
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testUniqGrepParseCommand_forwardGrepToUniq_testPassed() throws Exception {
        String inputString = "grep Bob file1.txt | uniq";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        assertEquals(BOB + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testUniqGrepParseCommand_forwardUniqWithLinesToGrep_testPassed() throws Exception {
        String inputString = "uniq -c file1.txt | grep Alice";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = "2 Alice" + STRING_NEWLINE + "1 Alice";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testUniqGrepParseCommand_forwardGrepWithCaseInsensitiveWithFilenameToUniqWithCount_testPassed() throws Exception {
        String inputString = "grep Bob -iH file1.txt | uniq -c";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = "2 file1.txt: Bob" + STRING_NEWLINE + "1 file1.txt: bOb";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testUniqGrepParseCommand_forwardGrepWithCaseInsensitiveWithMultipleFilesWithToUniqWithCount_testPassed() throws Exception {
        String inputString = "grep b -i file1.txt file2.txt | uniq -c";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = "2 file1.txt: Bob" + STRING_NEWLINE +
                "1 file1.txt: bOb" + STRING_NEWLINE +
                "1 file2.txt: B" + STRING_NEWLINE +
                "1 file2.txt: b";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testUniqGrepParseCommand_forwardGrepWithInvalidFileToUniqWithCount_testPassed() throws Exception {
        String inputString = "grep b -i blabla.txt | uniq -c";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = "1 grep: blabla.txt: No such file or directory";
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testUniqGrepParseCommand_forwardUniqWithInvalidFileToGrep_throwsException() throws Exception {
        String inputString = "uniq blabla.txt | grep b -i";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(UniqException.class, () -> command.evaluate(System.in, myOut), UNIQ_EXCEPTN_MSG);
    }

    @Test
    void testUniqGrepParseCommand_forwardUniqWithFolderToGrep_throwsException() throws Exception {
        String inputString = String.format("uniq %s | grep b -i", TEST_FOLDER_NAME);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(UniqException.class, () -> command.evaluate(System.in, myOut), UNIQ_EXCEPTN_MSG);
    }

    @Test
    void testUniqGrepParseCommand_forwardGrepWithInvalidFlagsToUniq_throwsException() throws Exception {
        String inputString = String.format("grep b -z %s | uniq", FILE_NAME_1);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(GrepException.class, () -> command.evaluate(System.in, myOut), GREP_EXCEPTN_MSG);
    }

    @Test
    void testUniqGrepParseCommand_forwardUniqWithInvalidFlagsToGrep_throwsException() throws Exception {
        String inputString = String.format("uniq %s -z| grep b -i", FILE_NAME_1);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(UniqException.class, () -> command.evaluate(System.in, myOut), UNIQ_EXCEPTN_MSG);
    }
}
