package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.QUOTING_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

class QuotingCommandTest {
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = QUOTING_FOLDER;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    public ByteArrayOutputStream stdout;

    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_PATH));
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @BeforeEach
    void setUpEach() {
        Environment.currentDirectory = TEST_PATH;
        stdout = new ByteArrayOutputStream();
    }

    @Test
    void testQuoting_doubleQuoteValid_testPassed() throws Exception {
        String commandString = "echo \"hello world\"";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("hello world" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_doubleQuoteInvalid_testFailedWithException() {
        String commandString = "echo \"hello world";
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand(commandString, new ApplicationRunner()));
    }

    @Test
    void testQuoting_singleQuoteValid_testPassed() throws Exception {
        String commandString = "echo 'hello world'";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("hello world" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_singleQuoteInvalid_testFailedWithException() {
        String commandString = "echo hello world'";
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand(commandString, new ApplicationRunner()));
    }

    @Test
    void testQuoting_backQuoteValid_testPassed() throws Exception {
        String commandString = "echo `echo hello world`";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("hello world" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_backQuoteInvalid_testFailedWithException() {
        String commandString = "echo `echo hello world";
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand(commandString, new ApplicationRunner()));
    }

    @Test
    void testQuoting_doubleSingleValid_testPassed() throws Exception {
        String commandString = "echo \"'hello' world\"";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("'hello' world" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_singleDoubleValid_testPassed() throws Exception {
        String commandString = "echo '\"hello world\"'";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("\"hello world\"" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_doubleBackValid_testPassed() throws Exception {
        String commandString = "echo \"`echo hello`world\"";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("helloworld" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_backDoubleValid_testPassed() throws Exception {
        String commandString = "echo `\"echo\" helloworld`";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("helloworld" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_backDoubleInvalid_testPassed() throws Exception {
        String commandString = "echo `\"echo helloworld\"`";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(ShellException.class, () -> command.evaluate(System.in, stdout));
    }

    @Test
    void testQuoting_singleBackValid_testPassed() throws Exception {
        String commandString = "echo '`echo hello`world`'";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("`echo hello`world`" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_backSingleValid_testPassed() throws Exception {
        String commandString = "echo `echo 'hello'`";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("hello" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_backSingleInvalid_testPassed() throws Exception {
        String commandString = "echo `'echo hello'`";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        assertThrows(ShellException.class, () -> command.evaluate(System.in, stdout));
    }

    @Test
    void testQuoting_multipleNestingValid_testPassed() throws Exception {
        String commandString = "`````````````````echo \"echo\"` '\"`hello`\"' \"`echo wor`'ld'\"";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("\"`hello`\" wor'ld'" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_multipleNestingInvalid_testFailedWithException() {
        String commandString = "`echo \"echo\"` '\"`hello`\" \"`'echo wor`'ld'\"";
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand(commandString, new ApplicationRunner()));
    }
}