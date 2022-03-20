package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.GrepException;
import sg.edu.nus.comp.cs4218.exception.TeeException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.TestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.GREP_TEE_FOLDER;

public class GrepTeeIntegrationTest {
    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = GREP_TEE_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String FILE_NAME_1 = "file1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_2 = "file2.txt";
    private static final String FILE_PATH_2 = TEST_PATH + FILE_NAME_2;

    private static final String TEE_EXCEPTN_MSG = "Should throw TeeException";
    private static final String GREP_EXCEPTN_MSG = "Should throw GrepException";

    private static final String BOB = "Bob";
    private static final String SECOND_BOB = "bOb";
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
                + SECOND_BOB;
        TestUtils.createFile(FILE_PATH_1, fileContent);
        TestUtils.createFile(FILE_PATH_2, ALICE + STRING_NEWLINE + "Bob" + STRING_NEWLINE + "Alice Bob");
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
    void testGrepTeeParseCommand_forwardGrepToTee_testPassed() throws Exception {
        String inputString = "grep B file1.txt | tee";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = BOB + STRING_NEWLINE + BOB;
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGrepTeeParseCommand_forwardTeeWithStdinRedirectionToGrep_testPassed() throws Exception {
        String inputString = "tee < file1.txt | grep B";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = BOB + STRING_NEWLINE + BOB;
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGrepTeeParseCommand_forwardGrepWithMultipleFilesToTee_testPassed() throws Exception {
        String inputString = "grep B file1.txt file2.txt | tee";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String sbExpected = FILE_NAME_1 + STRING_COLON + BOB +
                STRING_NEWLINE + FILE_NAME_1 + STRING_COLON + BOB +
                STRING_NEWLINE + FILE_NAME_2 + STRING_COLON + BOB +
                STRING_NEWLINE + FILE_NAME_2 + STRING_COLON + String.format("%s %s", ALICE, BOB);
        assertEquals(sbExpected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGrepTeeParseCommand_forwardTeeWithStdinRedirectionToGrepWithCaseInsensitiveAndFileNameAndCount_testPassed() throws Exception {
        String inputString = "tee < file1.txt | grep o -icH";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = "(standard input):5";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGrepTeeParseCommand_forwardGrepToTeeWithFile_testPassed() throws Exception {
        String inputString = "grep o file1.txt | tee file3.txt";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = HELLO_WORLD + STRING_NEWLINE + HELLO_WORLD + STRING_NEWLINE + BOB + STRING_NEWLINE + BOB;
        assertEquals(expected + STRING_NEWLINE, standardOutput);

        InputStream input = IOUtils.openInputStream("file3.txt");
        List<String> fileContent;
        try {
            fileContent = IOUtils.getLinesFromInputStream(input);
        } finally {
            input.close();
        }

        assertEquals(4, fileContent.size());
        assertEquals(fileContent.get(0), HELLO_WORLD);
        assertEquals(fileContent.get(1), HELLO_WORLD);
        assertEquals(fileContent.get(2), BOB);
        assertEquals(fileContent.get(3), BOB);

        TestUtils.deleteDir(new File("file3.txt"));
    }

    @Test
    void testGrepTeeParseCommand_forwardTeeWithStdinRedirectionAndAppendFlagToGrepWithMultFiles_testPassed() throws Exception {
        String inputString = "tee < file1.txt -a file3.txt | grep b - file3.txt";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        String expected = String.format("file3.txt:%s", BOB) + STRING_NEWLINE +
                String.format("file3.txt:%s", BOB) + STRING_NEWLINE +
                String.format("file3.txt:%s", SECOND_BOB) + STRING_NEWLINE +
                String.format("(standard input):%s", BOB) + STRING_NEWLINE +
                String.format("(standard input):%s", BOB) + STRING_NEWLINE +
                String.format("(standard input):%s", SECOND_BOB);
        assertEquals(expected + STRING_NEWLINE, standardOutput);

        TestUtils.deleteDir(new File("file3.txt"));
    }

    @Test
    void testGrepTeeParseCommand_forwardGrepWithInvalidOptionToTee_testPassed() throws Exception {
        String inputString = "grep B file1.txt -z | tee";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(GrepException.class, () -> command.evaluate(System.in, myOut), GREP_EXCEPTN_MSG);
    }

    @Test
    void testGrepTeeParseCommand_forwardTeeWithInvalidOptionToGrep_testPassed() throws Exception {
        String inputString = "tee < file1.txt -z | grep";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(TeeException.class, () -> command.evaluate(System.in, myOut), TEE_EXCEPTN_MSG);
    }

    @Test
    void testGrepTeeParseCommand_forwardGrepToTeeWithInvalidOption_testPassed() throws Exception {
        String inputString = "grep B file1.txt | tee -z";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(TeeException.class, () -> command.evaluate(System.in, myOut), TEE_EXCEPTN_MSG);
    }

    @Test
    void testGrepTeeParseCommand_forwardTeeToGrepWithInvalidOption_testPassed() throws Exception {
        String inputString = "tee < file1.txt | grep -z";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(GrepException.class, () -> command.evaluate(System.in, myOut), GREP_EXCEPTN_MSG);
    }
}
