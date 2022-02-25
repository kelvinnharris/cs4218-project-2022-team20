package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.TestUtils;
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

    private static final String NONEXISTENTFILE = "nofile.txt";

    CallCommand callCommand;
    List<String> argsList;
    private ApplicationRunner appRunner;
    private ArgumentResolver argResolver;
    private InputStream inputStream;
    private OutputStream outputStream;

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        TestUtils.createFile(FILE_PATH_1, "This is WC Test file 1" + StringUtils.STRING_NEWLINE);
    }

    @BeforeEach
    void setUpEach() {
        argsList = new ArrayList<>();
        appRunner = new ApplicationRunner();
        argResolver = new ArgumentResolver();
        inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        outputStream = new OutputStream() {
            @Override
            public void write(int num) throws IOException { // NOPMD
            }
        };
    }

    @AfterAll
    static void tearDown() {
        TestUtils.deleteDir(new File(FILE_PATH_1));
        TestUtils.deleteDir(new File(FILE_PATH_2));
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    @Test
    void testCallCommand_getArgsList_shouldReturnSameList() {
        argsList.addAll(Arrays.asList("wc", "<", NONEXISTENTFILE));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        assertEquals(argsList, callCommand.getArgsList());
    }

    @Test
    void testCallCommand_invalidFileForInStream_shouldThrowShellException() {
        argsList.addAll(Arrays.asList("wc", "<", NONEXISTENTFILE));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        assertThrows(ShellException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testCallCommand_invalidFileForOutStream_shouldThrowFileNotFoundException() {
        argsList.addAll(Arrays.asList("wc", ">", TEST_FOLDER_NAME));
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        assertThrows(FileNotFoundException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testCallCommand_nullArgsList_shouldThrowShellException() {
        callCommand = new CallCommand(null, appRunner, argResolver);
        assertThrows(ShellException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_SYNTAX);
    }

    @Test
    void testCallCommand_emptyArgsList_shouldThrowShellException() {
        callCommand = new CallCommand(argsList, appRunner, argResolver);
        assertThrows(ShellException.class, () -> callCommand.evaluate(inputStream, outputStream), ERR_SYNTAX);
    }
}
