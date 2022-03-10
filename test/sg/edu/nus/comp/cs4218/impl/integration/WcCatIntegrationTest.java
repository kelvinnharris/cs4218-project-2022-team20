package sg.edu.nus.comp.cs4218.impl.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import sg.edu.nus.comp.cs4218.impl.util.TestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.WC_CAT_FOLDER;

public class WcCatIntegrationTest {

    ByteArrayOutputStream myOut;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = WC_CAT_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    static final String NUMBER_FORMAT = " %7d";
    static final String STRING_FORMAT = " %s";

    private static final String FILE_NAME_1 = "file1.txt";
    private static final String FILE_PATH_1 = TEST_FOLDER_NAME + FILE_NAME_1;

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
        TestUtils.createFile(FILE_PATH_1, "This is text file 1 without any new line");
    }

    @BeforeEach
    void setUpEach() {
        Environment.currentDirectory = TEST_PATH;
        myOut = new ByteArrayOutputStream();
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    @Test
    void testWcCat_forwardCatToWc_testPassed() throws Exception {
        String inputString = "cat file1.txt | wc";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        int totalByte = 41;
        if (TestUtils.isWindowsSystem()) {
            totalByte = 42;
        }

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 1))
                .append(String.format(NUMBER_FORMAT, 9))
                .append(String.format(NUMBER_FORMAT, totalByte));
        assertEquals(sbExpected + StringUtils.STRING_NEWLINE, standardOutput);
    }

    @Test
    void testWcCat_forwardWcToCat_testPassed() throws Exception {
        String inputString = "wc file1.txt | cat";
        Command command = CommandBuilder.parseCommand(inputString, new ApplicationRunner());

        command.evaluate(System.in, myOut);
        final String standardOutput = myOut.toString();

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 0))
                .append(String.format(NUMBER_FORMAT, 9))
                .append(String.format(NUMBER_FORMAT, 40))
                .append(String.format(STRING_FORMAT, FILE_NAME_1));
        assertEquals(sbExpected + StringUtils.STRING_NEWLINE, standardOutput);
    }
}
