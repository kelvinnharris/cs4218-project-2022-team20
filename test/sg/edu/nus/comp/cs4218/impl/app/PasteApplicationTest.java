package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class PasteApplicationTest {
    private static PasteApplication pasteApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpPasteTestFolder/";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String ERR_IS_A_DIRECTORY = ": Is a directory";
    private static final String ERR_NO_SUCH_FILE_OR_DIRECTORY = ": No such file or directory";

    private static final String stdIn = "-";

    private static final String nonExistentFile = "paste";

    private static final String fileName1 = "test1.txt";
    private static final String filePath1 = TEST_FOLDER_NAME + fileName1;
    private static final String fileName2 = "test2.txt";
    private static final String filePath2 = TEST_FOLDER_NAME + fileName2;
    private static final String fileStdIn = "stdIn.txt";
    private static final String filePathStdIn = TEST_FOLDER_NAME + fileStdIn;

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        StringBuilder sbContentFile1 = new StringBuilder();
        sbContentFile1.append("1").append(StringUtils.STRING_NEWLINE);
        sbContentFile1.append("2").append(StringUtils.STRING_NEWLINE);
        sbContentFile1.append("3").append(StringUtils.STRING_NEWLINE);
        sbContentFile1.append("4").append(StringUtils.STRING_NEWLINE);
        sbContentFile1.append("5");
        TestUtils.createFile(filePath1, sbContentFile1.toString());

        StringBuilder sbContentFile2 = new StringBuilder();
        sbContentFile2.append("A").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("B").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("C").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("D").append(StringUtils.STRING_NEWLINE);
        sbContentFile2.append("E");
        TestUtils.createFile(filePath2, sbContentFile2.toString());

        StringBuilder sbContentFileStdIn = new StringBuilder();
        sbContentFileStdIn.append("This is from stdIn").append(StringUtils.STRING_NEWLINE);
        sbContentFileStdIn.append("This is stdIn line 2").append(StringUtils.STRING_NEWLINE);
        TestUtils.createFile(filePathStdIn, sbContentFileStdIn.toString());
    }

    @BeforeEach
    void setUpEach() {
        // Instantiate here because every time there is a call command, a new instance of the application is created
        pasteApplication = new PasteApplication();
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() {
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    @Test
        // command: paste tmpPasteTestFolder/test1.txt tmpPasteTestFolder/test2.txt
    void testPaste_fileInputWithoutFlag_shouldShowMergedContentsInParallel() throws Exception {
        pasteApplication.setCurrentOperation(pasteApplication.getFileOperation());
        String result = pasteApplication.mergeFile(false, filePath1, filePath2);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("1").append(StringUtils.STRING_TAB).append("A").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("2").append(StringUtils.STRING_TAB).append("B").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("3").append(StringUtils.STRING_TAB).append("C").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("4").append(StringUtils.STRING_TAB).append("D").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("5").append(StringUtils.STRING_TAB).append("E");

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: paste -s tmpPasteTestFolder/test1.txt tmpPasteTestFolder/test2.txt
    void testPaste_fileInputWithFlag_shouldShowMergedContentsSerially() throws Exception {
        pasteApplication.setCurrentOperation(pasteApplication.getFileOperation());
        String result = pasteApplication.mergeFile(true, filePath1, filePath2);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("1").append(StringUtils.STRING_TAB).append("2").append(StringUtils.STRING_TAB);
        sbExpected.append("3").append(StringUtils.STRING_TAB).append("4").append(StringUtils.STRING_TAB);
        sbExpected.append("5").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("A").append(StringUtils.STRING_TAB).append("B").append(StringUtils.STRING_TAB);
        sbExpected.append("C").append(StringUtils.STRING_TAB).append("D").append(StringUtils.STRING_TAB);
        sbExpected.append("E");

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: paste tmpPasteTestFolder/test1.txt tmpPasteTestFolder/test2.txt
    void testPaste_stdInWithoutFlag_shouldShowMergedContentsInParallel() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(filePath2);
        String result = pasteApplication.mergeStdin(false, inputStream);
        IOUtils.closeInputStream(inputStream);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("A").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("B").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("C").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("D").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("E");

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: paste tmpPasteTestFolder/test1.txt tmpPasteTestFolder/test2.txt
    void testPaste_stdInWithFlag_shouldShowMergedContentsSerially() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(filePath2);
        String result = pasteApplication.mergeStdin(true, inputStream);
        IOUtils.closeInputStream(inputStream);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("A").append(StringUtils.STRING_TAB);
        sbExpected.append("B").append(StringUtils.STRING_TAB);
        sbExpected.append("C").append(StringUtils.STRING_TAB);
        sbExpected.append("D").append(StringUtils.STRING_TAB);
        sbExpected.append("E");

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: paste - tmpPasteTestFolder/test1.txt -
    void testPaste_fileInputAndStdIntWithoutFlag_shouldShowMergedContentsInParallel() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(filePath2);
        String result = pasteApplication.mergeFileAndStdin(false, inputStream, stdIn, filePath1, stdIn);
        IOUtils.closeInputStream(inputStream);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("A").append(StringUtils.STRING_TAB).append("1").append(StringUtils.STRING_TAB).append("B").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("C").append(StringUtils.STRING_TAB).append("2").append(StringUtils.STRING_TAB).append("D").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("E").append(StringUtils.STRING_TAB).append("3").append(StringUtils.STRING_TAB).append("").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("").append(StringUtils.STRING_TAB).append("4").append(StringUtils.STRING_TAB).append("").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("").append(StringUtils.STRING_TAB).append("5").append(StringUtils.STRING_TAB).append("");

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: paste -s - tmpPasteTestFolder/test2.txt -
    void testPaste_fileInputAndStdIntWithFlag_shouldShowMergedContentsSerially() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(filePath1);
        String result = pasteApplication.mergeFileAndStdin(true, inputStream, stdIn, filePath2, stdIn);
        IOUtils.closeInputStream(inputStream);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("1").append(StringUtils.STRING_TAB).append("2").append(StringUtils.STRING_TAB);
        sbExpected.append("3").append(StringUtils.STRING_TAB).append("4").append(StringUtils.STRING_TAB);
        sbExpected.append("5").append(StringUtils.STRING_NEWLINE);
        sbExpected.append("A").append(StringUtils.STRING_TAB).append("B").append(StringUtils.STRING_TAB);
        sbExpected.append("C").append(StringUtils.STRING_TAB).append("D").append(StringUtils.STRING_TAB);
        sbExpected.append("E").append(StringUtils.STRING_NEWLINE);

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: paste - tmpPasteTestFolder/test1.txt tmpPasteTestFolder/
    void testPaste_fileInputAndStdInAndDirectoryWithoutFlag_shouldShowMergedContentsInParallel() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(filePath2);
        String result = pasteApplication.mergeFileAndStdin(false, inputStream, stdIn, filePath1, TEST_FOLDER_NAME);
        IOUtils.closeInputStream(inputStream);

        StringBuilder sbIsDirectory = new StringBuilder();
        sbIsDirectory.append("paste: ").append(TEST_FOLDER_NAME).append(ERR_IS_A_DIRECTORY).append(STRING_NEWLINE);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("A").append(StringUtils.STRING_TAB).append("1").append(StringUtils.STRING_TAB).append(sbIsDirectory);
        sbExpected.append(StringUtils.STRING_NEWLINE);
        sbExpected.append("B").append(StringUtils.STRING_TAB).append("2").append(StringUtils.STRING_TAB).append(StringUtils.STRING_NEWLINE);
        sbExpected.append("C").append(StringUtils.STRING_TAB).append("3").append(StringUtils.STRING_TAB).append(StringUtils.STRING_NEWLINE);
        sbExpected.append("D").append(StringUtils.STRING_TAB).append("4").append(StringUtils.STRING_TAB).append(StringUtils.STRING_NEWLINE);
        sbExpected.append("E").append(StringUtils.STRING_TAB).append("5").append(StringUtils.STRING_TAB);

        assertEquals(sbExpected.toString(), result);
    }

    @Test
        // command: paste - tmpPasteTestFolder/test1.txt paste
    void testPaste_fileInputAndStdInAndNonExistentFileWithoutFlag_shouldShowMergedContentsInParallel() throws Exception {
        InputStream inputStream = IOUtils.openInputStream(filePath2);
        String result = pasteApplication.mergeFileAndStdin(false, inputStream, stdIn, filePath1, nonExistentFile);
        IOUtils.closeInputStream(inputStream);

        StringBuilder sbExpected = new StringBuilder();
        sbExpected.append("paste: ").append(nonExistentFile).append(": ").append(ERR_NO_SUCH_FILE_OR_DIRECTORY);

        assertEquals(sbExpected.toString(), result);
    }
}
