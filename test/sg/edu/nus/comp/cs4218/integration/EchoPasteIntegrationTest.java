package sg.edu.nus.comp.cs4218.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.app.EchoApplication;
import sg.edu.nus.comp.cs4218.impl.app.TestUtils;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.ECHO_PASTE_FOLDER;

class EchoPasteIntegrationTest {
    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = ECHO_PASTE_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    @BeforeAll
    static void setUp() throws IOException {
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
    }

    @BeforeEach
    void setUpEach() throws FileNotFoundException {
        Environment.currentDirectory = TEST_PATH;
        myOut = new ByteArrayOutputStream();
    }

    @AfterAll
    static void tearDown() throws IOException {
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

    @Test
    void testEchoPaste_forwardEchoToPaste_testPassed() throws Exception {
        String inputString = "echo hello world | paste";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();
        assertEquals("hello world" + StringUtils.STRING_NEWLINE, standardOutput);
    }

    @Test
    void testEchoPaste_echoToFileAndPasteReadFromStdin_testPassed() throws Exception {
        String inputString = "echo hello world > tmp.txt; paste tmp.txt;";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();
        assertEquals("hello world" + StringUtils.STRING_NEWLINE, standardOutput);
    }

    @Test
    void testEchoPaste_complexEchoPasteFromMultipleFiles_testPassed() throws Exception {
        String inputString = "echo hello > tmp1.txt; echo \"`echo 'world'`\" > tmp2.txt; paste tmp1.txt tmp2.txt > tmp3.txt; paste tmp3.txt";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();
        assertEquals("hello" + StringUtils.STRING_TAB + "world" + StringUtils.STRING_NEWLINE, standardOutput);
    }
}