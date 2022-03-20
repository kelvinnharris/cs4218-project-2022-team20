package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.TestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_FILES;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.isWindowsSystem;

public class WcApplicationTest {

    private static WcApplication wcApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpWcTestFolder" + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String STDIN = "-";

    static final String NUMBER_FORMAT = " %7d";
    static final String STRING_WC = "wc: ";
    static final String STRING_FORMAT = " %s";
    static final String TOTAL = "total";

    private static final String NON_EXISTENT_FILE = "wc";

    private static final String FILE_NAME_1 = "test1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_2 = "test2.txt";
    private static final String FILE_PATH_2 = TEST_PATH + FILE_NAME_2;
    private static final String FILE_NAME_3 = "test3.txt";
    private static final String FILE_PATH_3 = TEST_PATH + FILE_NAME_3;

    private static final String ERR_IS_DIRECTORY = ": Is a directory";
    private static final String ERR_NOT_FOUND = ": No such file or directory";

    @BeforeAll
    static void setUp() throws IOException {
        wcApplication = new WcApplication();
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        TestUtils.createFile(FILE_PATH_1, "This is WC Test file 1" + STRING_NEWLINE);
        TestUtils.createFile(FILE_PATH_2, "This is WC Test file 2" + STRING_NEWLINE + " Test for second line" + STRING_NEWLINE);
        TestUtils.createFile(FILE_PATH_3, "This is WC Test file 3" + STRING_NEWLINE
                + " Test for second line" + STRING_NEWLINE
                + " Test for third line" + STRING_NEWLINE);
    }

    @BeforeEach
    void setUpEach() {
        // Instantiate here because every time there is a call command, a new instance of the application is created
        wcApplication = new WcApplication();
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    // command: wc tmpWcTestFolder/test1.txt
    @Test
    void testWcCountFromFiles_fileInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
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

    // command: wc
    @Test
    void testWcCountFromStdin_noFileArgumentsWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_1);
        String result;
        try {
            result = wcApplication.countFromStdin(true, true, true, input);
        } finally {
            input.close();
        }

        int totalByte = 23;
        if (isWindowsSystem()) {
            totalByte = 24;
        }
        assertEquals(String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, totalByte), result);
    }

    // command: wc -
    @Test
    void testWcCountFromFileAndStdin_stdInFileArgumentWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_1);
        String result;
        try {
            result = wcApplication.countFromFileAndStdin(true, true, true, input, STDIN);
        } finally {
            input.close();
        }

        int totalByte = 23;
        if (isWindowsSystem()) {
            totalByte = 24;
        }
        String sbExpected = String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, totalByte) +
                String.format(STRING_FORMAT, STDIN);

        assertEquals(sbExpected, result);
    }

    // command: wc tmpWcTestFolder/test1.txt tmpWcTestFolder/test2.txt
    @Test
    void testWcCountFromFiles_multipleFilesFromSameDirectoryInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, new String[]{FILE_PATH_1, FILE_PATH_2});

        StringBuilder sbExpected = new StringBuilder();
        int totalByte = 23;
        int totalBytes2 = 45;
        if (isWindowsSystem()) {
            totalByte = 24;
            totalBytes2 = 47;
        }
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, FILE_PATH_1)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 2)).append(String.format(NUMBER_FORMAT, 10)).append(String.format(NUMBER_FORMAT, totalBytes2))
                .append(String.format(STRING_FORMAT, FILE_PATH_2)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 3)).append(String.format(NUMBER_FORMAT, 16)).append(String.format(NUMBER_FORMAT, totalByte + totalBytes2))
                .append(String.format(STRING_FORMAT, TOTAL));

        assertEquals(sbExpected.toString(), result);
    }

    // command: wc tmpWcTestFolder/test1.txt tmpWcTestFolder/test2.txt -
    @Test
    void testWcCountFromFileAndStdin_multipleFilesFromSameDirectoryAndStandardInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_3);
        String result;
        try {
            result = wcApplication.countFromFileAndStdin(true, true, true, input, FILE_PATH_1, FILE_PATH_2, STDIN);
        } finally {
            input.close();
        }

        StringBuilder sbExpected = new StringBuilder();
        int totalByte = 23;
        int totalBytes2 = 45;
        int totalBytes3 = 66;
        if (isWindowsSystem()) {
            totalByte = 24;
            totalBytes2 = 47;
            totalBytes3 = 69;
        }
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, FILE_PATH_1)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 2)).append(String.format(NUMBER_FORMAT, 10)).append(String.format(NUMBER_FORMAT, totalBytes2))
                .append(String.format(STRING_FORMAT, FILE_PATH_2)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 3)).append(String.format(NUMBER_FORMAT, 14)).append(String.format(NUMBER_FORMAT, totalBytes3))
                .append(String.format(STRING_FORMAT, STDIN)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, 30)).append(String.format(NUMBER_FORMAT, totalByte + totalBytes2 + totalBytes3))
                .append(String.format(STRING_FORMAT, TOTAL));

        assertEquals(sbExpected.toString(), result);
    }

    // command: wc tmpWcTestFolder/test1.txt wc -
    @Test
    void testWcCountFromFileAndStdin_singleFileAndNonExistentFilesAndStandardInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_3);
        String result;
        try {
            result = wcApplication.countFromFileAndStdin(true, true, true, input, FILE_PATH_1, NON_EXISTENT_FILE, STDIN);
        } finally {
            input.close();
        }

        StringBuilder sbExpected = new StringBuilder();
        int totalByte = 23;
        int totalBytes3 = 66;
        if (isWindowsSystem()) {
            totalByte = 24;
            totalBytes3 = 69;
        }
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, FILE_PATH_1)).append(STRING_NEWLINE)
                .append(STRING_WC).append(NON_EXISTENT_FILE).append(ERR_NOT_FOUND).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 3)).append(String.format(NUMBER_FORMAT, 14)).append(String.format(NUMBER_FORMAT, totalBytes3))
                .append(String.format(STRING_FORMAT, STDIN)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 4)).append(String.format(NUMBER_FORMAT, 20)).append(String.format(NUMBER_FORMAT, totalByte + totalBytes3))
                .append(String.format(STRING_FORMAT, TOTAL));

        assertEquals(sbExpected.toString(), result);
    }

    // command: wc - - - < tmpWcTestFolder/test1.txt
    @Test
    void testWcCountFromFileAndStdin_InputRedirectionWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_1);
        String result;
        try {
            result = wcApplication.countFromFileAndStdin(true, true, true, input, STDIN, STDIN, STDIN);
        } finally {
            input.close();
        }

        StringBuilder sbExpected = new StringBuilder();
        int totalByte = 23;
        if (isWindowsSystem()) {
            totalByte = 24;
        }
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, STDIN)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0))
                .append(String.format(STRING_FORMAT, STDIN)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0))
                .append(String.format(STRING_FORMAT, STDIN)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, TOTAL));

        assertEquals(sbExpected.toString(), result);
    }

    // command: wc tmpWcTestFolder wc - tmpWcTestFolder/test1.txt
    @Test
    void testWcCountFromFileAndStdin_argumentsFromDirectoryNonExistentFileStdInAndSingleFileWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(FILE_PATH_1);
        String result;
        try {
            result = wcApplication.countFromFileAndStdin(true, true, true, input, TEST_FOLDER_NAME, NON_EXISTENT_FILE, STDIN, FILE_PATH_2);
        } finally {
            input.close();
        }

        StringBuilder sbExpected = new StringBuilder();
        int totalByte = 23;
        int totalBytes2 = 45;
        if (isWindowsSystem()) {
            totalByte = 24;
            totalBytes2 = 47;
        }
        sbExpected.append(STRING_WC).append(TEST_FOLDER_NAME).append(ERR_IS_DIRECTORY).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0))
                .append(String.format(STRING_FORMAT, TEST_FOLDER_NAME)).append(STRING_NEWLINE)
                .append(STRING_WC).append(NON_EXISTENT_FILE).append(ERR_NOT_FOUND).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, STDIN)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 2)).append(String.format(NUMBER_FORMAT, 10)).append(String.format(NUMBER_FORMAT, totalBytes2))
                .append(String.format(STRING_FORMAT, FILE_PATH_2)).append(STRING_NEWLINE)
                .append(String.format(NUMBER_FORMAT, 3)).append(String.format(NUMBER_FORMAT, 16)).append(String.format(NUMBER_FORMAT, totalBytes2 + totalByte))
                .append(String.format(STRING_FORMAT, TOTAL));

        assertEquals(sbExpected.toString(), result);
    }

    // command: wc tmpWcTestFolder/test1.txt -l
    @Test
    void testWcCountFromFiles_fileInputWithLineFlag_shouldShowLinesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(false, true, false, FILE_PATH_1);

        String sbExpected = String.format(NUMBER_FORMAT, 1) +
                String.format(STRING_FORMAT, FILE_PATH_1);

        assertEquals(sbExpected, result);
    }

    // command: wc tmpWcTestFolder/test1.txt -w
    @Test
    void testWcCountFromFiles_fileInputWithWordFlag_shouldShowWordsWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(true, false, false, FILE_PATH_1);

        StringBuilder sbExpected = new StringBuilder();
        int totalByte = 22 + STRING_NEWLINE.getBytes().length;
        sbExpected.append(String.format(NUMBER_FORMAT, totalByte))
                .append(String.format(STRING_FORMAT, FILE_PATH_1));

        assertEquals(sbExpected.toString(), result);
    }

    // command: wc tmpWcTestFolder/test1.txt -c
    @Test
    void testWcCountFromFiles_fileInputWithByteFlag_shouldShowBytesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(false, false, true, FILE_PATH_1);

        String sbExpected = String.format(NUMBER_FORMAT, 6) +
                String.format(STRING_FORMAT, FILE_PATH_1);

        assertEquals(sbExpected, result);
    }

    // command: wc tmpWcTestFolder
    @Test
    void testWcCountFromFiles_inputFileIsDirectory_shouldDisplayIsDirectoryError() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, TEST_FOLDER_NAME);

        String sbExpected = STRING_WC + TEST_FOLDER_NAME + ERR_IS_DIRECTORY + STRING_NEWLINE +
                String.format(NUMBER_FORMAT, 0) + String.format(NUMBER_FORMAT, 0) + String.format(NUMBER_FORMAT, 0) +
                String.format(STRING_FORMAT, TEST_FOLDER_NAME);

        assertEquals(sbExpected, result);
    }

    // command: wc nonExistentName
    @Test
    void testWcCountFromFiles_inputNonExistentFileOrDirectory_shouldDisplayNoSuchFileOrDirectory() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, NON_EXISTENT_FILE);
        assertEquals(STRING_WC + NON_EXISTENT_FILE + ERR_NOT_FOUND, result);
    }

    @Test
    void testWcCountFromStdin_nullInputStream_shouldThrowException() {
        assertThrows(WcException.class, () -> wcApplication.countFromStdin(true, true, true, null), ERR_NULL_STREAMS);
    }

    @Test
    void testWcCountFromFiles_nullFileNames_shouldThrowException() {
        assertThrows(WcException.class, () -> wcApplication.countFromFiles(true, true, true, null), ERR_NULL_FILES);
    }

    @Test
    void testWcCountFromFileAndStdin_nullFileNamesAndInputStream_shouldThrowException() throws ShellException, IOException {
        assertThrows(WcException.class, () -> wcApplication.countFromFileAndStdin(true, true, true, null, new String[]{}), ERR_NULL_STREAMS);
        InputStream input = IOUtils.openInputStream(FILE_PATH_1);
        try {
            assertThrows(WcException.class, () -> wcApplication.countFromFileAndStdin(true, true, true, input, null), ERR_NULL_FILES);
        } finally {
            input.close();
        }
    }

    @Test
    void testWcRun_nullStdIn_shouldThrowCatException() {
        assertThrows(WcException.class, () -> wcApplication.run(new String[]{FILE_PATH_1}, null, System.out), "Should Throw WcException");
    }

    @Test
    void testWcRun_nullStdout_shouldThrowCatException() {
        assertThrows(WcException.class, () -> wcApplication.run(new String[]{FILE_PATH_1}, System.in, null), "Should Throw WcException");
    }

    @Test
    void testWcRun_invalidFlag_shouldThrowCatException() {
        assertThrows(WcException.class, () -> wcApplication.run(new String[]{FILE_PATH_1, "-z"}, System.in, System.out), "Should Throw WcException");
    }

    @Test
    void testWcRun_correctInputs_shouldNotThrowException() throws AbstractApplicationException {
        wcApplication.run(new String[]{FILE_PATH_1}, System.in, System.out);
        WcApplication.Result actualRes = wcApplication.listResult.get(0);

        assertEquals(actualRes.words, 6);
        assertEquals(actualRes.lines, 1);
        assertEquals(actualRes.bytes, TestUtils.isWindowsSystem() ? 24 : 23);
        assertEquals(actualRes.fileName, FILE_PATH_1);
    }
}
