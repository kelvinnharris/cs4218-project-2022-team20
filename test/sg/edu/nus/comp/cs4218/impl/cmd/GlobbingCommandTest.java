package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.GLOBBING_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

class GlobbingCommandTest {
    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = GLOBBING_FOLDER;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FOLDER1 = "folder1";
    private static final String FILE1 = "file1.xml";
    private static final String FILE2 = "file2.xml";
    private static final String FILE3 = "file3.txt";
    private static final String FILE4 = "file4.txt";

    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + CHAR_FILE_SEP + FOLDER1));
        Files.createFile(Paths.get(TEST_PATH + CHAR_FILE_SEP + FOLDER1 + CHAR_FILE_SEP + FILE1));
        Files.createFile(Paths.get(TEST_PATH + CHAR_FILE_SEP + FOLDER1 + CHAR_FILE_SEP + FILE2));
        Files.createFile(Paths.get(TEST_PATH + CHAR_FILE_SEP + FOLDER1 + CHAR_FILE_SEP + FILE3));
        Files.createFile(Paths.get(TEST_PATH + CHAR_FILE_SEP + FILE4));
        Files.createFile(Paths.get(TEST_PATH + CHAR_FILE_SEP + "tmp_file1.txt"));
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @BeforeEach
    void setUpEach() {
        Environment.currentDirectory = TEST_PATH;
        myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));
    }

    @Test
    void testGlobbing_currentDirectory_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls fil*";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("file4.txt" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_currentDirectoryMultipleStars_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls -X *fil*.t*";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("file4.txt" + STRING_NEWLINE + STRING_NEWLINE + "tmp_file1.txt" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_specifiedDirectory_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls " + FOLDER1 + CHAR_FILE_SEP + "*.xml";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(FOLDER1 + CHAR_FILE_SEP + FILE1 + STRING_NEWLINE + STRING_NEWLINE +
                FOLDER1 + CHAR_FILE_SEP + FILE2 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globSpecifiedDirectoryAndFiles_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls fol*" + CHAR_FILE_SEP + "*.xml";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(FOLDER1 + CHAR_FILE_SEP + FILE1 + STRING_NEWLINE + STRING_NEWLINE +
                FOLDER1 + CHAR_FILE_SEP + FILE2 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globSpecifiedDirectory_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls fol*";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(FILE1 + STRING_NEWLINE + FILE2 + STRING_NEWLINE + FILE3 + STRING_NEWLINE, standardOutput);
    }
}