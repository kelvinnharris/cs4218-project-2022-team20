package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

class SemicolonCommandTest {
    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpSemicolonTestFolder" + CHAR_FILE_SEP + "";
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
        myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));
    }

    @Test
    void testSemicolon_twoEchos_returnResultFromBothEchos() throws Exception {
        String inputString = "echo first output; echo second output";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = "first output" + StringUtils.STRING_NEWLINE + "second output" + StringUtils.STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolon_threeEchos_returnResultFromAllEchos() throws Exception {
        String inputString = "echo first output; echo second output; echo third output";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = "first output" + StringUtils.STRING_NEWLINE + "second output" + StringUtils.STRING_NEWLINE + "third output" + StringUtils.STRING_NEWLINE;
        assertEquals(expected, standardOutput);    }

    @Test
    void testSemicolon_oneEchoOneLs_returnResultFromBothCommands() throws Exception {
        String inputString = "echo first output; ls " + TEST_PATH;
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        String expected = "first output" + StringUtils.STRING_NEWLINE + "file1.xml" + StringUtils.STRING_NEWLINE + "folder1" + StringUtils.STRING_NEWLINE;
        assertEquals(expected, standardOutput);
    }

    @Test
    void testSemicolon_semicolonAtStart_returnException() throws Exception {
        String inputString = ";echo first output";

        assertThrows(Exception.class, () -> CommandBuilder.parseCommand(inputString, new ApplicationRunner()));
    }

    @Test
    void testSemicolon_semicolonAtEnd_returnException() throws Exception {
        String inputString = "echo first output; echo second output;";

        assertThrows(Exception.class, () -> CommandBuilder.parseCommand(inputString, new ApplicationRunner()));
    }


}