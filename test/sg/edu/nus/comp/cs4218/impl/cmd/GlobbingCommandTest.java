package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;
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
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.*;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.GLOBBING_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.isWindowsSystem;

class GlobbingCommandTest {
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = GLOBBING_FOLDER;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FOLDER1 = "folder1";
    private static final String FILE1 = "file1.xml";
    private static final String FILE2 = "file2.xml";
    private static final String FILE3 = "file3.txt";
    private static final String FILE4 = "file4.txt";
    private static final String FILE5 = "file5.txt";
    ByteArrayOutputStream myOut;

    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + CHAR_FILE_SEP + FOLDER1));
        Files.createFile(Paths.get(TEST_PATH + CHAR_FILE_SEP + FOLDER1 + CHAR_FILE_SEP + FILE1));
        Files.createFile(Paths.get(TEST_PATH + CHAR_FILE_SEP + FOLDER1 + CHAR_FILE_SEP + FILE2));
        Files.createFile(Paths.get(TEST_PATH + CHAR_FILE_SEP + FOLDER1 + CHAR_FILE_SEP + FILE3));
        Files.createFile(Paths.get(TEST_PATH + CHAR_FILE_SEP + FILE4));
        Files.createFile(Paths.get(TEST_PATH + CHAR_FILE_SEP + FILE5));
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
    void testGlobbing_globFilesInCurrentDirectory_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls fil*";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(FILE4 + STRING_NEWLINE + FILE5 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globFilesInCurrentDirectoryNoMatchingFiles_shouldThrowException() throws Exception {
        String inputString = "ls abc*";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        if (isWindowsSystem()) {
            assertThrows(LsException.class, () -> command.evaluate(System.in, System.out));
        } else {
            command.evaluate(System.in, System.out);
            final String standardOutput = myOut.toString();
            assertEquals("ls: cannot access 'abc*': No such file or directory" + STRING_NEWLINE, standardOutput);
        }
    }

    @Test
    void testGlobbing_globFilesInCurrentDirectoryDot_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls ." + CHAR_FILE_SEP + "fil*";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(FILE4 + STRING_NEWLINE + FILE5 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globFilesInPreviousDirectoryDotDot_shouldReturnCorrectOutput() throws Exception {
        String prevDirPrefix = ".." + CHAR_FILE_SEP;
        Environment.currentDirectory = TEST_PATH + CHAR_FILE_SEP + FOLDER1;
        String inputString = "ls " + prevDirPrefix + "fil*";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();

        assertEquals(prevDirPrefix + FILE4 + STRING_NEWLINE + prevDirPrefix + FILE5 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globFilesInCurrentDirectoryMultipleStars_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls -X *fil*.t*";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(FILE4 + STRING_NEWLINE + FILE5 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globFilesInSpecifiedDirectory_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls " + FOLDER1 + CHAR_FILE_SEP + "*.xml";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(FOLDER1 + CHAR_FILE_SEP + FILE1 + STRING_NEWLINE +
                FOLDER1 + CHAR_FILE_SEP + FILE2 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globFilesInSpecifiedDirectoryMultipleStars_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls " + FOLDER1 + CHAR_FILE_SEP + "*fil*.x*";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(FOLDER1 + CHAR_FILE_SEP + FILE1 + STRING_NEWLINE +
                FOLDER1 + CHAR_FILE_SEP + FILE2 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globBothSpecifiedDirectoryAndFiles_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls fol*" + CHAR_FILE_SEP + "*.xml";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(FOLDER1 + CHAR_FILE_SEP + FILE1 + STRING_NEWLINE +
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

    @Test
    void testGlobbing_globCurrentDirectoryNoPattern_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls *";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(FILE4 + STRING_NEWLINE + FILE5 + STRING_NEWLINE
                + FOLDER1 + STRING_COLON + STRING_NEWLINE
                + FILE1 + STRING_NEWLINE
                + FILE2 + STRING_NEWLINE
                + FILE3 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globMultipleDirectoryLevelsNoPattern_shouldReturnCorrectOutput() throws Exception {
        String inputString = String.format("ls *%c*", CHAR_FILE_SEP);
        String resultPrefix = FOLDER1 + CHAR_FILE_SEP;
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(resultPrefix + FILE1 + STRING_NEWLINE
                + resultPrefix + FILE2 + STRING_NEWLINE
                + resultPrefix + FILE3 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globbingWithCommandSubstitution_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo \"`ls *" + CHAR_FILE_SEP + "*`\"";
        String resultPrefix = FOLDER1 + CHAR_FILE_SEP;
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(resultPrefix + FILE1 + STRING_NEWLINE
                + resultPrefix + FILE2 + STRING_NEWLINE
                + resultPrefix + FILE3 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globbingWithPipe_shouldReturnCorrectOutput() throws Exception {
        String inputString = String.format("ls *%c* | grep \"3\"", CHAR_FILE_SEP);
        String resultPrefix = FOLDER1 + CHAR_FILE_SEP;
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(resultPrefix + FILE3 + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globbingWithSemicolon_shouldReturnCorrectOutput() throws Exception {
        String inputString = String.format("ls *%c*; sort *.txt", CHAR_FILE_SEP);
        String resultPrefix = FOLDER1 + CHAR_FILE_SEP;
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals(resultPrefix + FILE1 + STRING_NEWLINE
                + resultPrefix + FILE2 + STRING_NEWLINE
                + resultPrefix + FILE3 + STRING_NEWLINE, standardOutput);
    }
}
