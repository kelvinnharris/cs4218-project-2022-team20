package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.TestUtils;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.ArgumentResolver;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

class CallCommandTest {

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCallCmdTestFolder" + StringUtils.CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + StringUtils.CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String FILE_NAME_1 = "test1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_2 = "test2.txt";
    private static final String FILE_PATH_2 = TEST_PATH + FILE_NAME_2;

    private static final String FILE_CONTENT_1 = "This is WC Test file 1" + StringUtils.STRING_NEWLINE;

    private static final String NON_EXISTENT_FILE = "nofile.txt";

    CallCommand callCommand;
    List<String> argsList;
    private ApplicationRunner appRunner;
    private ArgumentResolver argResolver;
    private InputStream inputStream;
    private OutputStream outputStream;

    @BeforeAll
    static void setUp() throws IOException {
        Environment.currentDirectory = TEST_PATH;
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        TestUtils.createFile(FILE_PATH_1, FILE_CONTENT_1);
    }

    @BeforeEach
    void setUpEach() {
        argsList = new ArrayList<>();
        appRunner = new ApplicationRunner();
        argResolver = new ArgumentResolver();
        inputStream = System.in;
        outputStream = new ByteArrayOutputStream();
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        TestUtils.deleteDir(new File(FILE_PATH_1));
        TestUtils.deleteDir(new File(FILE_PATH_2));
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    @Test
    void testCallCommand_CatApplicationGetArgsList_shouldReturnSameList() throws FileNotFoundException, AbstractApplicationException, ShellException {
        argsList.addAll(Arrays.asList("cat", "-n", FILE_PATH_1));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        callCommand.evaluate(inputStream, outputStream);

        String numberFormat = "%6d ";

        final String standardOutput = outputStream.toString();
        assertEquals(standardOutput, String.format(numberFormat, 1) + "This is WC Test file 1" + StringUtils.STRING_NEWLINE);
    }

    @Test
    void testCallCommand_LsApplicationWithGlobbing_testPassed() throws FileNotFoundException, AbstractApplicationException, ShellException {
        argsList.addAll(Arrays.asList("ls", "*.txt"));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        callCommand.evaluate(inputStream, outputStream);

        final String standardOutput = outputStream.toString();

        String expected = FILE_NAME_1;
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCallCommand_EchoApplicationWithSubCommand_testPassed() throws FileNotFoundException, AbstractApplicationException, ShellException {
        argsList.addAll(Arrays.asList("echo", String.format("`cat %s`", FILE_NAME_1)));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        callCommand.evaluate(inputStream, outputStream);

        final String standardOutput = outputStream.toString();

        String expected = FILE_CONTENT_1;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testCallCommand_EchoApplicationWithEchoDoubleQuoting_testPassed() throws FileNotFoundException, AbstractApplicationException, ShellException {
        argsList.addAll(Arrays.asList("echo", String.format("\" hello world \"", FILE_NAME_1)));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        callCommand.evaluate(inputStream, outputStream);

        final String standardOutput = outputStream.toString();

        String expected = " hello world ";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCallCommand_EchoApplicationWithEchSingleQuoting_testPassed() throws FileNotFoundException, AbstractApplicationException, ShellException {
        argsList.addAll(Arrays.asList("echo", String.format("\' hello world \'", FILE_NAME_1)));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        callCommand.evaluate(inputStream, outputStream);

        final String standardOutput = outputStream.toString();

        String expected = " hello world ";
        assertEquals(expected + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCallCommand_WcApplicationWithInputRedirectionGetArgsList_shouldReturnSameList() {
        argsList.addAll(Arrays.asList("wc", "<", NON_EXISTENT_FILE));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        assertEquals(argsList, callCommand.getArgsList());
    }

    @Test
    void testCallCommand_WcApplicationInvalidFileForInStream_shouldThrowShellException() {
        argsList.addAll(Arrays.asList("wc", "<", NON_EXISTENT_FILE));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        assertThrows(ShellException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testCallCommand_CatApplicationInvalidFileForOutStream_shouldThrowFileNotFoundException() {
        argsList.addAll(Arrays.asList("cat", FILE_PATH_1, ">", TEST_PATH));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        assertThrows(ShellException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_IS_DIR);
    }

    @Test
    void testCallCommand_NullArgsList_shouldThrowShellException() {
        callCommand = new CallCommand(null, appRunner, argResolver);
        assertThrows(ShellException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_SYNTAX);
    }

    @Test
    void testCallCommand_EmptyArgsList_shouldThrowShellException() {
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        assertThrows(ShellException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_SYNTAX);
    }
}
