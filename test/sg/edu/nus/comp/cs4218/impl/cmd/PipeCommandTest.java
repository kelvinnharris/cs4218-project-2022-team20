package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
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
    public static final String LSA_ERR = "shell: lsa: Invalid app";
    public static final String SHELL_ERR = "shell: Invalid syntax";
    private static final String FIRST_OUTPUT = "first output";
    private static final String SECOND_OUTPUT = "second output";
    private static final String THIRD_OUTPUT = "third output";

    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_PATH + "folder1"));
        Files.createFile(Paths.get(TEST_PATH + "file1.xml"));
    }


    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @BeforeEach
    void setUpEach() {
        myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));
    }

    @Test
    void testPipeParseCommand_onePipeSameCommand_returnCorrectResultOfLastCommand() throws Exception {
        String inputString = String.format("echo %s | echo %s", FIRST_OUTPUT, SECOND_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = SECOND_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipeParseCommand_onePipeDifferentCommand_returnCorrectResultOfLastCommand() throws Exception {
        String inputString = String.format("ls | echo %s", SECOND_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = SECOND_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipeParseCommand_cutFromEchoAsStdin_returnCorrectResult() throws Exception {
        String inputString = String.format("echo %s | cut -c 1-2", FIRST_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = "fi" + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipeParseCommand_sortFromEchoAsStdin_returnCorrectResult() throws Exception {
        String inputString = String.format("echo %s | sort", FIRST_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipeParseCommand_catFromEchoAsStdin_returnCorrectResult() throws Exception {
        String inputString = String.format("echo %s | cat", FIRST_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipeParseAndEvaluate_validCommandInvalidCommand_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("echo %s | lsa", FIRST_OUTPUT);
        String expectedOutput = LSA_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

    @Test
    void testPipeParseAndEvaluate_invalidCommandValidCommand_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("lsa | echo %s", SECOND_OUTPUT);
        String expectedOutput = LSA_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

    @Test
    void testPipeParseAndEvaluate_pipeAtStartOneCommand_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("| echo");
        String expectedOutput = SHELL_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

    @Test
    void testPipeParseAndEvaluate_pipeAtStartTwoCommand_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("| echo | echo %s", SECOND_OUTPUT);
        String expectedOutput = SHELL_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

    @Test
    void testPipeParseAndEvaluate_pipeAtEndOneCommand_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("echo %s |", SECOND_OUTPUT);
        String expectedOutput = SHELL_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

    @Test
    void testPipeParseAndEvaluate_pipeAtEndTwoCommand_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("echo | echo %s |", SECOND_OUTPUT);
        String expectedOutput = SHELL_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

    @Test
    void testPipeParseAndEvaluate_pipeAtStartAtEndOneCommand_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("| echo |");
        String expectedOutput = SHELL_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

    @Test
    void testPipeParseAndEvaluate_pipeAtStartAtEndTwoCommand_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("| echo | echo |");
        String expectedOutput = SHELL_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

    @Test
    void testPipeParseAndEvaluate_doublePipe_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("lsa || echo %s", SECOND_OUTPUT);
        String expectedOutput = SHELL_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

    @Test
    void testPipeParseCommand_twoPipeSameCommands_returnCorrectResultOfLastCommand() throws Exception {
        String inputString = String.format("echo %s | echo %s | echo %s", FIRST_OUTPUT, SECOND_OUTPUT, THIRD_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = THIRD_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipeParseCommand_twoPipeDifferentCommands_returnCorrectResultOfLastCommand() throws Exception {
        String inputString = String.format("echo %s | cut -c 1-2 | cut -c 1", FIRST_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = "f" + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testPipeParseAndEvaluate_twoPipeValidCommandInvalidCommandValidCommand_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("echo %s | lsa | echo %s", FIRST_OUTPUT, THIRD_OUTPUT);
        String expectedOutput = LSA_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

    @Test
    void testPipeParseAndEvaluate_twoPipeInvalidCommandValidCommandInvalidCommand_throwsErrorAndTerminates() {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("lsa | echo %s | lsa", SECOND_OUTPUT);
        String expectedOutput = LSA_ERR;
        Exception exception = assertThrows(
                ShellException.class,
                () -> {
                    shell.parseAndEvaluate(commandString, myOut);
                }
        );
        assertEquals(expectedOutput, exception.getMessage());
    }

}
