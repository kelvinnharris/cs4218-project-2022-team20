package sg.edu.nus.comp.cs4218.impl.Hackathon;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.app.CatApplication;
import sg.edu.nus.comp.cs4218.impl.app.PasteApplication;
import sg.edu.nus.comp.cs4218.impl.app.WcApplication;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;
import sg.edu.nus.comp.cs4218.impl.util.TestUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.createFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class HackathonBugs3 {
    private static CatApplication catApplication;
    private static WcApplication wcApplication;
    private static PasteApplication pasteApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCatTestFolder" + StringUtils.CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + StringUtils.CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String FILE_NAME_1 = "test1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_3 = "test3.txt";
    private static final String FILE_PATH_3 = TEST_PATH + FILE_NAME_3;

    static final String NUMBER_FORMAT = "\t%d";
    static final String STRING_FORMAT = " %s";

    @BeforeAll
    static void setUp() throws IOException {
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        createFile(FILE_PATH_1, "This is WC Test file 1" + STRING_NEWLINE);
        createFile(FILE_PATH_3, "");
    }

    @BeforeEach
    void setUpEach() {
        catApplication = new CatApplication();
        wcApplication = new WcApplication();
        pasteApplication = new PasteApplication();
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void runCatFilesFromS3_emptyFileInputWithoutFlag_shouldShowEmptyStringWithNoNewLine() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(new String[]{FILE_PATH_3}, System.in, outputStream);

        assertEquals("", outputStream.toString());
    }

    @Test
    void runCatFilesFromP3_emptyFileInputWithoutFlag_shouldShowEmptyStringWithNoNewLine() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(new String[]{FILE_PATH_3}, System.in, outputStream);

        assertEquals("", outputStream.toString());
    }

    // command: wc tmpWcTestFolder/test1.txt
    @Test
    void testWcCountFromFilesP2_fileInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, FILE_PATH_1);

        StringBuilder sbExpected = new StringBuilder();
        int totalByte = 23;
        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win")) {
            totalByte = 24;
        }
        sbExpected.append(String.format(NUMBER_FORMAT, 1))
                .append(String.format(NUMBER_FORMAT, 6))
                .append(String.format(NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, FILE_PATH_1));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
    void testPasteRun_PasteMultipleDashes_Success() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        String input = "a" + STRING_NEWLINE + "b" + STRING_NEWLINE + "c" + STRING_NEWLINE;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        String expected = "a\tb" + STRING_NEWLINE
                + "c\t" + STRING_NEWLINE;

        pasteApplication.run(new String[]{"-","-"}, inputStream, outputStream);

        String actual = outputStream.toString();
        assertEquals(expected, actual);
    }
}
