package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.exception.WcException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_FILES;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_NULL_STREAMS;

public class WcApplicationTest {

    private static WcApplication wcApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpWcTestFolder" + StringUtils.CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + StringUtils.CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String stdIn = "-";

    static final String NUMBER_FORMAT = " %7d";

    private static final String nonExistentFile = "wc";

    private static final String fileName1 = "test1.txt";
    private static final String filePath1 = TEST_FOLDER_NAME + fileName1;
    private static final String fileName2 = "test2.txt";
    private static final String filePath2 = TEST_FOLDER_NAME + fileName2;
    private static final String fileName3 = "test3.txt";
    private static final String filePath3 = TEST_FOLDER_NAME + fileName3;
    private static final String fileNameStar1 = "testStar1.txt";
    private static final String fileNameStar2 = "testStar2.txt";
    private static final String filePathAllTextFiles = "*.txt";
    private static final String fileNameWithEOFs = "testEOFs.txt";
    private static final String filePathWithEOFs = TEST_FOLDER_NAME + fileNameWithEOFs;

    private static final String ERR_IS_A_DIRECTORY = ": Is a directory";
    private static final String ERR_NO_SUCH_FILE_OR_DIRECTORY = ": No such file or directory";

    @BeforeAll
    static void setUp() throws IOException {
        wcApplication = new WcApplication();
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        TestUtils.createFile(filePath1, "This is WC Test file 1" + StringUtils.STRING_NEWLINE);
        TestUtils.createFile(filePath2, "This is WC Test file 2" + StringUtils.STRING_NEWLINE + " Test for second line" + StringUtils.STRING_NEWLINE);
        TestUtils.createFile(filePath3, "This is WC Test file 3" + StringUtils.STRING_NEWLINE
                + " Test for second line" + StringUtils.STRING_NEWLINE
                + " Test for third line" + StringUtils.STRING_NEWLINE);

        TestUtils.createFile(fileNameStar1, "This is test star 1" + StringUtils.STRING_NEWLINE);
        TestUtils.createFile(fileNameStar2, "This is test star 2" + StringUtils.STRING_NEWLINE
                + "line 2" + StringUtils.STRING_NEWLINE);
    }

    @BeforeEach
    void setUpEach() {
        // Instantiate here because every time there is a call command, a new instance of the application is created
        wcApplication = new WcApplication();
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() {
        TestUtils.deleteDir(new File(TEST_PATH));
        TestUtils.deleteDir(new File(fileNameStar1));
        TestUtils.deleteDir(new File(fileNameStar2));
    }

    @Test
    // command: wc tmpWcTestFolder/test1.txt
    void testWc_fileInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, filePath1);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, 24));
        sbExpected.append(String.format(" %s", filePath1));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
    // command: wc
    void testWc_noFileArgumentsWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(filePath1);
        String result = wcApplication.countFromStdin(true, true, true, input);
        IOUtils.closeInputStream(input);

        assertEquals(String.format(NUMBER_FORMAT, 1) + String.format(NUMBER_FORMAT, 6) + String.format(NUMBER_FORMAT, 24), result);
    }

    @Test
    // command: wc -
    void testWc_stdInFileArgumentWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(filePath1);
        String result = wcApplication.countFromFileAndStdin(true, true, true, input, stdIn);
        IOUtils.closeInputStream(input);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, 24));
        sbExpected.append(String.format(" %s", stdIn));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
    // command: wc tmpWcTestFolder/test1.txt tmpWcTestFolder/test2.txt
    void testWc_multipleFilesFromSameDirectoryInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, new String[]{filePath1,filePath2});

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, 24));
        sbExpected.append(String.format(" %s", filePath1)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 2)).append(String.format(NUMBER_FORMAT, 10)).append(String.format(NUMBER_FORMAT, 47));
        sbExpected.append(String.format(" %s", filePath2)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 3)).append(String.format(NUMBER_FORMAT, 16)).append(String.format(NUMBER_FORMAT, 71));
        sbExpected.append(String.format(" %s", "total"));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: wc tmpWcTestFolder/test1.txt tmpWcTestFolder/test2.txt -
    void testWc_multipleFilesFromSameDirectoryAndStandardInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(filePath3);
        String result = wcApplication.countFromFileAndStdin(true, true, true, input, filePath1,filePath2,stdIn);
        IOUtils.closeInputStream(input);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, 24));
        sbExpected.append(String.format(" %s", filePath1)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 2)).append(String.format(NUMBER_FORMAT, 10)).append(String.format(NUMBER_FORMAT, 47));
        sbExpected.append(String.format(" %s", filePath2)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 3)).append(String.format(NUMBER_FORMAT, 14)).append(String.format(NUMBER_FORMAT, 69));
        sbExpected.append(String.format(" %s", stdIn)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, 30)).append(String.format(NUMBER_FORMAT, 140));
        sbExpected.append(String.format(" %s", "total"));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: wc tmpWcTestFolder/test1.txt wc -
    void testWc_singleFileAndNonExistentFilesAndStandardInputWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(filePath3);
        String result = wcApplication.countFromFileAndStdin(true, true, true, input, filePath1,nonExistentFile,stdIn);
        IOUtils.closeInputStream(input);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, 24));
        sbExpected.append(String.format(" %s", filePath1)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append("wc: ").append(nonExistentFile).append(ERR_NO_SUCH_FILE_OR_DIRECTORY).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 3)).append(String.format(NUMBER_FORMAT, 14)).append(String.format(NUMBER_FORMAT, 69));
        sbExpected.append(String.format(" %s", stdIn)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 4)).append(String.format(NUMBER_FORMAT, 20)).append(String.format(NUMBER_FORMAT, 93));
        sbExpected.append(String.format(" %s", "total"));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: wc - - - < tmpWcTestFolder/test1.txt
    void testWc_InputRedirectionWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(filePath1);
        String result = wcApplication.countFromFileAndStdin(true, true, true, input, stdIn,stdIn,stdIn);
        IOUtils.closeInputStream(input);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, 24));
        sbExpected.append(String.format(" %s", stdIn)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0));
        sbExpected.append(String.format(" %s", stdIn)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0));
        sbExpected.append(String.format(" %s", stdIn)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, 24));
        sbExpected.append(String.format(" %s", "total"));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: wc tmpWcTestFolder wc - tmpWcTestFolder/test1.txt
    void testWc_argumentsFromDirectoryNonExistentFileStdInAndSingleFileWithoutFlag_shouldShowWordsLinesBytesWithFilename() throws Exception {
        InputStream input = IOUtils.openInputStream(filePath1);
        String result = wcApplication.countFromFileAndStdin(true, true, true, input, TEST_FOLDER_NAME,nonExistentFile,stdIn,filePath2);
        IOUtils.closeInputStream(input);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("wc: ").append(TEST_FOLDER_NAME).append(ERR_IS_A_DIRECTORY).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0));
        sbExpected.append(String.format(" %s", TEST_FOLDER_NAME)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append("wc: ").append(nonExistentFile).append(ERR_NO_SUCH_FILE_OR_DIRECTORY).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 1)).append(String.format(NUMBER_FORMAT, 6)).append(String.format(NUMBER_FORMAT, 24));
        sbExpected.append(String.format(" %s", stdIn)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 2)).append(String.format(NUMBER_FORMAT, 10)).append(String.format(NUMBER_FORMAT, 47));
        sbExpected.append(String.format(" %s", filePath2)).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 3)).append(String.format(NUMBER_FORMAT, 16)).append(String.format(NUMBER_FORMAT, 71));
        sbExpected.append(String.format(" %s", "total"));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: wc tmpWcTestFolder/test1.txt -l
    void testWc_fileInputWithLineFlag_shouldShowLinesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(false, true, false, filePath1);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 1));
        sbExpected.append(String.format(" %s", filePath1));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: wc tmpWcTestFolder/test1.txt -w
    void testWc_fileInputWithWordFlag_shouldShowWordsWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(true, false, false, filePath1);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 24));
        sbExpected.append(String.format(" %s", filePath1));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: wc tmpWcTestFolder/test1.txt -c
    void testWc_fileInputWithByteFlag_shouldShowBytesWithFilename() throws Exception {
        String result = wcApplication.countFromFiles(false, false, true, filePath1);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append(String.format(NUMBER_FORMAT, 6));
        sbExpected.append(String.format(" %s", filePath1));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: wc tmpWcTestFolder
    void testWc_inputFileIsDirectory_shouldDisplayIsDirectoryError() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, TEST_FOLDER_NAME);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("wc: ").append(TEST_FOLDER_NAME).append(ERR_IS_A_DIRECTORY).append(StringUtils.STRING_NEWLINE);
        sbExpected.append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0)).append(String.format(NUMBER_FORMAT, 0));
        sbExpected.append(String.format(" %s", TEST_FOLDER_NAME));

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: wc nonExistentName
    void testWc_inputNonExistentFileOrDirectory_shouldDisplayNoSuchFileOrDirectory() throws Exception {
        String result = wcApplication.countFromFiles(true, true, true, nonExistentFile);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("wc: ").append(nonExistentFile).append(ERR_NO_SUCH_FILE_OR_DIRECTORY);

        assertEquals(sbExpected.toString(), result);
    }

    @Test
    void testWc_nullInputStream_shouldThrowException(){
        assertThrows(WcException.class, () -> wcApplication.countFromStdin(true, true, true, null), ERR_NULL_STREAMS);
    }

    @Test
    void testWc_nullFileNames_shouldThrowException(){
        assertThrows(WcException.class, () -> wcApplication.countFromFiles(true, true, true, null), ERR_NULL_FILES);
    }

    @Test
    void testWc_nullFileNamesAndInputStream_shouldThrowException() throws ShellException {
        assertThrows(WcException.class, () -> wcApplication.countFromFileAndStdin(true, true, true, null, new String[]{}), ERR_NULL_STREAMS);
        InputStream input = IOUtils.openInputStream(filePath1);
        assertThrows(WcException.class, () -> wcApplication.countFromFileAndStdin(true, true, true, input, null), ERR_NULL_FILES);
        IOUtils.closeInputStream(input);
    }

    // TODO:
    //  test redirection
    //  test with different flags combination (many duplicates, single ones)
    //  test with globing
    //  test with multiple stdIn arguments
}
