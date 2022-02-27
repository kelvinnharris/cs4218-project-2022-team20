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

class GlobbingCommandTest {
    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = GLOBBING_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    @BeforeAll
    static void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_PATH + "folder1"));
        Files.createFile(Paths.get(TEST_PATH + "folder1" + CHAR_FILE_SEP + "file1.xml"));
        Files.createFile(Paths.get(TEST_PATH + "folder1" + CHAR_FILE_SEP + "file2.xml"));
        Files.createFile(Paths.get(TEST_PATH + "folder1" + CHAR_FILE_SEP + "file3.txt"));
        Files.createFile(Paths.get(TEST_PATH + "file4.txt"));
        Files.createFile(Paths.get(TEST_PATH + "tmp_file1.txt"));
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
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
        String inputString = "ls folder1" + CHAR_FILE_SEP + "*.xml";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("folder1" + CHAR_FILE_SEP + "file1.xml" + STRING_NEWLINE + STRING_NEWLINE +
                "folder1" + CHAR_FILE_SEP + "file2.xml" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globSpecifiedDirectoryAndFiles_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls fol*" + CHAR_FILE_SEP + "*.xml";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("folder1" + CHAR_FILE_SEP + "file1.xml" + STRING_NEWLINE + STRING_NEWLINE +
                "folder1" + CHAR_FILE_SEP + "file2.xml" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testGlobbing_globSpecifiedDirectory_shouldReturnCorrectOutput() throws Exception {
        String inputString = "ls fol*";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("file1.xml" + STRING_NEWLINE + "file2.xml" + STRING_NEWLINE + "file3.txt" + STRING_NEWLINE, standardOutput);
    }
}