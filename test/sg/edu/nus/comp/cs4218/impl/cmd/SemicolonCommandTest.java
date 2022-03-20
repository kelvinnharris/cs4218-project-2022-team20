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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

class SemicolonCommandTest {
    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpSemicolonTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    public static final String FOLDER1_NAME = "folder1";
    public static final String FOLDER1_PATH = TEST_PATH + FOLDER1_NAME;
    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = TEST_PATH + FILE1_NAME;
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE2_PATH = TEST_PATH + FILE2_NAME;
    public static final String FILE3_NAME = "file3.txt";
    public static final String FILE3_PATH = TEST_PATH + FILE3_NAME;
    public static final String OUTPUT1_NAME = "output1.txt";
    public static final String OUTPUT1_PATH = TEST_PATH + OUTPUT1_NAME;
    public static final String GREP_ERR = String.format("grep: %s", ERR_SYNTAX);
    private static final String FIRST_OUTPUT = "first output";
    private static final String SECOND_OUTPUT = "second output";
    private static final String THIRD_OUTPUT = "third output";
    public static final String[] LINES1 = {FIRST_OUTPUT};
    public static final String[] LINES2 = {SECOND_OUTPUT};
    public static final String[] LINES3 = {THIRD_OUTPUT};


    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(Paths.get(FOLDER1_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(FILE3_PATH));
        Files.createFile(Paths.get(OUTPUT1_PATH));
        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);
        appendToFile(Paths.get(FILE3_PATH), LINES3);

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
    void testSemicolonParseCommand_oneSemicolonSameCommand_returnResultFromBothCommands() throws Exception {
        String inputString = String.format("echo %s; echo %s", FIRST_OUTPUT, SECOND_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE + SECOND_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolonParseCommand_oneSemicolonSameCommandWithFlags_returnResultFromBothCommands() throws Exception {
        String inputString = String.format("sort -r %s %s; sort -r %s", FILE1_PATH, FILE2_PATH, FILE3_PATH);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = SECOND_OUTPUT + STRING_NEWLINE + FIRST_OUTPUT + STRING_NEWLINE + THIRD_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }


    @Test
    void testSemicolonParseCommand_oneSemicolonDifferentCommand_returnResultFromBothCommands() throws Exception {
        String inputString = String.format("echo %s; ls %s", FIRST_OUTPUT, TEST_PATH);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE + FILE1_NAME + STRING_NEWLINE + FILE2_NAME + STRING_NEWLINE + FILE3_NAME + STRING_NEWLINE + FOLDER1_NAME + STRING_NEWLINE + OUTPUT1_NAME + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolonParseCommand_oneSemicolonDifferentCommandWithFlags_returnResultFromBothCommands() throws Exception {
        String inputString = String.format("sort -r %s %s; cut -b 1 %s", FILE1_PATH, FILE2_PATH, FILE3_PATH);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = SECOND_OUTPUT + STRING_NEWLINE + FIRST_OUTPUT + STRING_NEWLINE + "t" + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolonParseCommand_oneSemicolonWriteToFileReadFromFile_returnResultFromBothCommands() throws Exception {
        String inputString = String.format("tee %s; grep \"%s\" %s", OUTPUT1_PATH, FIRST_OUTPUT, OUTPUT1_PATH);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        String inputStdin = FIRST_OUTPUT;
        InputStream input = new ByteArrayInputStream(inputStdin.getBytes());
        command.evaluate(input, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE + FIRST_OUTPUT + STRING_NEWLINE;
        String expectedContent = FIRST_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
        String fileContent = readString(Paths.get(OUTPUT1_PATH));
        assertEquals(expectedContent, fileContent);
    }

    @Test
    void testSemicolonParseCommand_oneSemicolonReadFromSameFile_returnResultFromBothCommands() throws Exception {
        String inputString = String.format("grep \"%s\" %s; grep \"%s\" %s", FIRST_OUTPUT, FILE1_PATH, FIRST_OUTPUT, FILE1_PATH);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE + FIRST_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolonParseAndEvaluate_validCommandInvalidCommand_returnFirstOutputAndException() throws Exception {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("echo \"%s\"; grep", FIRST_OUTPUT);
        String expectedOutput = FIRST_OUTPUT + STRING_NEWLINE + GREP_ERR + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, myOut);
        assertEquals(expectedOutput, myOut.toString());
    }

    @Test
    void testSemicolonParseAndEvaluate_invalidCommandValidCommand_returnExceptionAndSecondOutput() throws Exception {
        ShellImpl shell = new ShellImpl();
        String commandString = String.format("grep; echo \"%s\"", SECOND_OUTPUT);
        String expectedOutput = GREP_ERR + STRING_NEWLINE + SECOND_OUTPUT + STRING_NEWLINE;
        shell.parseAndEvaluate(commandString, myOut);
        assertEquals(expectedOutput, myOut.toString());
    }

    @Test
    void testSemicolonParseCommand_semicolonAtStartOneCommand_returnException() {
        String inputString = String.format(";echo %s", FIRST_OUTPUT);
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand(inputString, new ApplicationRunner()));
    }

    @Test
    void testSemicolonParseCommand_semicolonAtStartTwoCommand_returnException() {
        String inputString = String.format(";echo %s; echo", FIRST_OUTPUT);
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand(inputString, new ApplicationRunner()));
    }

    @Test
    void testSemicolonParseCommand_semicolonAtEndOneCommand_returnResult() throws Exception {
        String inputString = String.format("echo %s;", FIRST_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolonParseCommand_semicolonAtEndTwoCommand_returnResult() throws Exception {
        String inputString = String.format("echo %s; echo;", FIRST_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolonParseCommand_semicolonAtStartAtEnd_returnException() {
        String inputString = String.format(";echo %s; echo;", FIRST_OUTPUT);
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand(inputString, new ApplicationRunner()));
    }

    @Test
    void testSemicolonParseCommand_doubleSemicolon_returnException() {
        String inputString = String.format("echo %s;; echo", FIRST_OUTPUT);
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand(inputString, new ApplicationRunner()));
    }

    @Test
    void testSemicolonParseCommand_twoSemicolonSameCommand_returnResultFromAllEchos() throws Exception {
        String inputString = String.format("echo %s; echo %s; echo %s", FIRST_OUTPUT, SECOND_OUTPUT, THIRD_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE + SECOND_OUTPUT + STRING_NEWLINE + THIRD_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolonParseCommand_twoSemicolonDifferentCommand_returnResultFromAllEchos() throws Exception {
        String inputString = String.format("echo %s; ls %s; echo %s", FIRST_OUTPUT, TEST_PATH, THIRD_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE + FILE1_NAME + STRING_NEWLINE + FILE2_NAME + STRING_NEWLINE + FILE3_NAME + STRING_NEWLINE + FOLDER1_NAME + STRING_NEWLINE + OUTPUT1_NAME + STRING_NEWLINE +THIRD_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolonParseCommand_twoSemicolonValidCommandInvalidCommandValidCommand_returnResultFromAllEchos() throws Exception {
        String inputString = String.format("echo %s; grep; echo %s", FIRST_OUTPUT, THIRD_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = FIRST_OUTPUT + STRING_NEWLINE + GREP_ERR + STRING_NEWLINE + THIRD_OUTPUT + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolonParseCommand_twoSemicolonInvalidCommandValidCommandInvalidCommand_returnResultFromAllEchos() throws Exception {
        String inputString = String.format("grep; echo %s; grep", SECOND_OUTPUT);
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = GREP_ERR + STRING_NEWLINE + SECOND_OUTPUT + STRING_NEWLINE + GREP_ERR + STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolonParseCommand_threeCommandsSemicolonAtEnd_returnException() {
        String inputString = String.format(";echo %s; echo; echo", FIRST_OUTPUT);
        assertThrows(ShellException.class, () -> CommandBuilder.parseCommand(inputString, new ApplicationRunner()));
    }

}
