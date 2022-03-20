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
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;

class CallCommandTest {

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCallCmdTestFolder" + StringUtils.CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + StringUtils.CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String FILE_NAME_1 = "test1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_2 = "test2.txt";
    private static final String FILE_PATH_2 = TEST_PATH + FILE_NAME_2;

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

        TestUtils.createFile(FILE_PATH_1, "This is WC Test file 1" + StringUtils.STRING_NEWLINE);
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
    void testCallCommand_WcApplicationGetArgsList_shouldReturnSameList() {
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
        assertThrows(FileNotFoundException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testCallCommand_WcApplicationNullArgsList_shouldThrowShellException() {
        callCommand = new CallCommand(null, appRunner, argResolver);
        assertThrows(ShellException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_SYNTAX);
    }

    @Test
    void testCallCommand_WcApplicationEmptyArgsList_shouldThrowShellException() {
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        assertThrows(ShellException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_SYNTAX);
    }
}
