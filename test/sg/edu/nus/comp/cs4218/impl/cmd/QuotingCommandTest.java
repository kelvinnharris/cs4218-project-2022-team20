package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

class QuotingCommandTest {
    public ByteArrayOutputStream stdout;

    @BeforeEach
    void setUp() {
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
    void testQuoting_doubleQuoteInvalid_testFailedWithException() throws Exception {
        String commandString = "echo \"hello world";
        assertThrows(Exception.class, () -> CommandBuilder.parseCommand(commandString, new ApplicationRunner()));
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
    void testQuoting_singleQuoteInvalid_testFailedWithException() throws Exception {
        String commandString = "echo hello world'";
        assertThrows(Exception.class, () -> CommandBuilder.parseCommand(commandString, new ApplicationRunner()));
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
    void testQuoting_backQuoteInvalid_testFailedWithException() throws Exception {
        String commandString = "echo `echo hello world";
        assertThrows(Exception.class, () -> CommandBuilder.parseCommand(commandString, new ApplicationRunner()));
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
    void testQuoting_doubleBackValid_testPassed() throws Exception {
        String commandString = "echo \"`echo hello`world\"";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("helloworld" + STRING_NEWLINE, standardOutput);
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
    void testQuoting_multipleNestingValid_testPassed() throws Exception {
        String commandString = "`````````````````echo \"echo\"` '\"`hello`\"' \"`echo wor`'ld'\"";
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, stdout);
        final String standardOutput = stdout.toString();
        assertEquals("\"`hello`\" wor'ld'" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testQuoting_multipleNestingInvalid_testPassed() throws Exception {
        String commandString = "`echo \"echo\"` '\"`hello`\" \"`'echo wor`'ld'\"";
        assertThrows(Exception.class, () -> CommandBuilder.parseCommand(commandString, new ApplicationRunner()));
    }

}