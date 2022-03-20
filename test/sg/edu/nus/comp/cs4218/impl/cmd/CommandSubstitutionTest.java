package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.CMD_SUBS_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

class CommandSubstitutionTest {
    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = CMD_SUBS_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FILE1_NAME = "file1.xml";
    private static final String FILE2_NAME = "file2.xml";
    private static final String FOLDER1_NAME = "folder1";
    public static final String[] LINES1 = {"F1L1", "F1L2"};
    public static final String[] LINES2 = {"F2L1", "F2L2"};

    @BeforeAll
    static void setUp() throws IOException {
        Environment.currentDirectory = TEST_PATH;
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER1_NAME));
        Files.createFile(Paths.get(TEST_PATH + FILE1_NAME));
        Files.createFile(Paths.get(TEST_PATH + FILE2_NAME));

        appendToFile(Paths.get(TEST_PATH + FILE1_NAME), LINES1);
        appendToFile(Paths.get(TEST_PATH + FILE1_NAME), LINES2);
    }

    @AfterAll
    static void tearDown() {
        deleteDir(new File(TEST_PATH));
    }

    @BeforeEach
    void setUpEach() {
        myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));
    }

    @Test
    void testCommandSubstitutionParseCommand_backSingleQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo `echo 'quote is interpreted as special character'`";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("quote is interpreted as special character" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_backDoubleQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo `echo \"quote is interpreted as special character\"`";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("quote is interpreted as special character" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_backDoubleSingleQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo `echo \"'quote is not interpreted as special character'\"`";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("'quote is not interpreted as special character'" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_backSingleDoubleQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo `echo '\"quote is not interpreted as special character\"'`";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("\"quote is not interpreted as special character\"" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_doubleBackSingleQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo \"`echo 'quote is interpreted as special character'`\"";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("quote is interpreted as special character" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_doubleSingleBackQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo \"'`echo quote`'\"";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("'quote'" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_singleBackDoubleQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo '`\"echo quote\"`'";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("`\"echo quote\"`" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_singleDoubleBackQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo '\"`echo quote`\"'";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("\"`echo quote`\"" + STRING_NEWLINE, standardOutput);
    }


    @Test
    void testCommandSubstitutionParseCommand_echoOnRealSubstitutable_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo `ls -X .`";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("folder1 file1.xml file2.xml" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_cutFromValidEchoOutputAsArgument_shouldReturnCorrectCutOutput() throws Exception {
        String commandString = String.format("cut -b 2,4 `echo \"%s\"`", FILE1_NAME);
        Command command = CommandBuilder.parseCommand(commandString, new ApplicationRunner());
        command.evaluate(System.in, System.out);
        String expectedOutput = "11" + STRING_NEWLINE + "12" + STRING_NEWLINE + "21" + STRING_NEWLINE + "22" + STRING_NEWLINE;
        assertEquals(expectedOutput, myOut.toString());
    }

    @Test
    void testCommandSubstitutionParseCommand_catOnTwoBackQuotes_shouldReturnCorrectCatOutput() throws Exception {
        String inputString = "cat `echo \"file1.xml\"` `echo file2.xml`";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("F1L1" + STRING_NEWLINE + "F1L2" + STRING_NEWLINE + "F2L1" + STRING_NEWLINE + "F2L2" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_backBackSingleQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo `echo `echo 'quote'``";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("echo quote" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_doubleBackBackQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo \"`echo `echo quote``\"";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("echo quote" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_singleBackBackQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo '`echo `echo quote``'";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("`echo `echo quote``" + STRING_NEWLINE, standardOutput);
    }

    @Test
    void testCommandSubstitutionParseCommand_singleBackSingleBackQuote_shouldReturnCorrectOutput() throws Exception {
        String inputString = "echo '`echo '`echo quote`'`'";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, System.out);
        final String standardOutput = myOut.toString();
        assertEquals("`echo quote`" + STRING_NEWLINE, standardOutput);
    }


    @Test
    void testCommandSubstitutionParseCommand_singlseBackSingleBackQuote_shouldThrowShellExceptionInvalidSyntax() throws Exception {
        String inputString = "echo `echo \"`echo quote`\"`";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());
        assertThrows(ShellException.class, () -> command.evaluate(System.in, System.out));
    }
}