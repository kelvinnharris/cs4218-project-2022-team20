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

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

class CommandSubstitutionTest {
    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCmdSubsTestFolder" + CHAR_FILE_SEP + "";
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
    void testCommandSubstitution_echoBackQuoteWithoutDoubleQuote_testPassed() throws Exception {
        String inputString = "echo `echo 'quote is interpreted as special character'`";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("quote is interpreted as special character" + StringUtils.STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitution_doubleQuoteEchoOutsideBackQuote_testPassed() throws Exception {
        String inputString = "echo \"`echo 'quote is interpreted as special character'`\"";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("quote is interpreted as special character" + StringUtils.STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitution_doubleQuoteEchoInsideBackQuote_testPassed() throws Exception {
        String inputString = "echo `echo \"'quote is not interpreted as special character'\"`";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("'quote is not interpreted as special character'" + StringUtils.STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitution_echoOnRealSubstitutable_testPassed() throws Exception {
        String inputString = "echo `ls " + TEST_PATH + "`";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("file1.xml folder1" + StringUtils.STRING_NEWLINE, standardOutput);
    }
}