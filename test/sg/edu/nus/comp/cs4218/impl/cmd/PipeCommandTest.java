package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.ShellImpl;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class PipeCommandTest {

    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpPipeTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_PATH + "folder1"));
        Files.createFile(Paths.get(TEST_PATH + "file1.xml"));
    }


    @AfterAll
    static void tearDown() {
        deleteDir(new File(TEST_PATH));
    }

    @BeforeEach
    void setUpEach() {
        myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));
    }

    @Test
    void testPipe_twoEcho_returnCorrectResultOfLastCommand() throws Exception {
        String inputString = "echo abc | echo def";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = "def" + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipe_oneLsOneEcho_returnCorrectResultOfLastCommand() throws Exception {
        String inputString = "ls | echo abc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = "abc" + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipe_cutFromEchoAsStdin_returnCorrectResultOfLastCommand() throws Exception {
        String inputString = "echo abc | cut -c 1-2";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = "ab" + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipe_multipleCommands_returnCorrectResultOfLastCommand() throws Exception {
        String inputString = "echo abc | cut -c 1-2 | cut -c 1";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = "a" + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipe_oneInvalidCommand_throwsErrorAndTerminates() throws Exception {
        ShellImpl shell = new ShellImpl();
        String commandString = "lsa | echo abc";
        String expectedOutput = String.format("shell: lsa: Invalid app");
        Exception exception = assertThrows(
                Exception.class,
                () -> { shell.parseAndEvaluate(commandString, myOut); }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

}
