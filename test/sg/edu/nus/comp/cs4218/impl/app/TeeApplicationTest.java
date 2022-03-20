package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;
import sg.edu.nus.comp.cs4218.exception.TeeException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.TEE_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class TeeApplicationTest {

    private static final String INPUT = "hello" + STRING_NEWLINE + "world" + STRING_NEWLINE + "goodbye" + STRING_NEWLINE + "world" + STRING_NEWLINE;
    private static final String FILE1_NAME = "file1.txt";
    private static final String FILE2_NAME = "file2.txt";
    private static final String FOLDER1_NAME = "folder1";
    private static final String NE_FILE_NAME = "nonExistent.txt";
    private static final String UNWR_FILE_NAME = "unwritable.txt";
    private static final String[] LINES1 = {"The first file", "The second line"};
    private static final String[] LINES2 = {"The second file", "The second line"};

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEE_FOLDER;
    private static final String FILE1_PATH = TEST_PATH + CHAR_FILE_SEP + FILE1_NAME;
    private static final String FILE2_PATH = TEST_PATH + CHAR_FILE_SEP + FILE2_NAME;
    private static final String FOLDER1_PATH = TEST_PATH + CHAR_FILE_SEP + FOLDER1_NAME;
    private static final String NE_FILE_PATH = TEST_PATH + CHAR_FILE_SEP + NE_FILE_NAME;
    private static final String UNWR_FILE_PATH = TEST_PATH + CHAR_FILE_SEP + UNWR_FILE_NAME;
    private static TeeApplication teeApplication;
    private static OutputStream outputStream;
    private final InputStream inputStream = new ByteArrayInputStream(INPUT.getBytes());

    @BeforeAll
    static void setUp() {
        teeApplication = new TeeApplication();
    }

    @AfterEach
    void tearDownEach() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @BeforeEach
    void setUpEach() throws IOException {
        outputStream = new ByteArrayOutputStream();
        Files.deleteIfExists(Paths.get(TEST_PATH));
        Files.createDirectory(Paths.get(TEST_PATH));
        Files.createDirectory(Paths.get(FOLDER1_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(UNWR_FILE_PATH));
        Paths.get(UNWR_FILE_PATH).toFile().setReadOnly();

        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);

    }

    @Test
    void teeFromStdin_teeWithValidFile_shouldOverwritePreviousContent() throws TeeException {
        try {
            teeApplication.teeFromStdin(false, inputStream, FILE1_PATH);
            String fileContent = readString(Paths.get(FILE1_PATH));
            assertEquals(INPUT, fileContent);
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }

    @Test
    void teeFromStdin_teeWithValidFiles_shouldOverwritePreviousContent() throws TeeException {
        try {
            String[] files = {FILE1_PATH, FILE2_PATH};
            teeApplication.teeFromStdin(false, inputStream, files);
            String file1Content = readString(Paths.get(FILE1_PATH));
            String file2Content = readString(Paths.get(FILE2_PATH));
            assertEquals(INPUT, file1Content);
            assertEquals(INPUT, file2Content);
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }

    @Test
    void teeFromStdin_teeWithValidFileAppend_shouldAppendToFile() throws TeeException {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : LINES1) {
                stringBuilder.append(s).append(STRING_NEWLINE);
            }
            stringBuilder.append(INPUT);
            teeApplication.teeFromStdin(true, inputStream, FILE1_PATH);
            String fileContent = readString(Paths.get(FILE1_PATH));
            assertEquals(stringBuilder.toString(), fileContent);
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }


    @Test
    void teeFromStdin_teeWithSameNameFilesAppend_shouldAppendOrderPreserved() throws TeeException {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : LINES1) {
                stringBuilder.append(s).append(STRING_NEWLINE);
            }
            String[] inputWords = INPUT.split(STRING_NEWLINE);
            for (String word : inputWords) {
                stringBuilder.append(word).append(STRING_NEWLINE);
                stringBuilder.append(word).append(STRING_NEWLINE);
            }
            String[] files = {FILE1_PATH, FILE1_PATH};
            teeApplication.teeFromStdin(true, inputStream, files);
            String fileContent = readString(Paths.get(FILE1_PATH));
            assertEquals(stringBuilder.toString(), fileContent);
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }

    @Test
    void teeFromStdin_teeWithFolderAsInputFile_shouldReadFromStdin() throws IOException, TeeException {
        assertDoesNotThrow(() -> teeApplication.teeFromStdin(false, inputStream, FOLDER1_PATH));
    }

    @Test
    void teeFromStdin_teeWithNonExistentFile_shouldCreateNewFileAndWrite() throws TeeException {
        try {
            teeApplication.teeFromStdin(false, inputStream, NE_FILE_PATH);
            String fileContent = readString(Paths.get(NE_FILE_PATH));
            assertEquals(INPUT, fileContent);
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }

    @Test
    void run_teeWithEmptyArgs_shouldPass() {
        String[] args = {};
        assertDoesNotThrow(() -> teeApplication.run(args, inputStream, outputStream));
    }

    @Test
    void run_teeWithNullStdin_shouldThrowException() {
        String[] args = {FILE1_NAME};
        assertThrows(TeeException.class, () -> teeApplication.run(args, null, outputStream));
    }

    @Test
    void run_teeWithNullStdout_shouldThrowException() {
        String[] args = {FILE1_NAME};
        assertThrows(TeeException.class, () -> teeApplication.run(args, inputStream, null));
    }

    @Test
    void run_teeWithUnwritableFile_shouldThrowException() throws AbstractApplicationException {
        String[] args = {TEE_FOLDER + CHAR_FILE_SEP + UNWR_FILE_NAME};
        teeApplication.run(args, inputStream, outputStream);
        assertFalse(Files.isWritable(Paths.get(UNWR_FILE_PATH)));
        assertEquals(String.format("%s: Permission denied", TEE_FOLDER + CHAR_FILE_SEP + UNWR_FILE_NAME) + STRING_NEWLINE + INPUT, outputStream.toString());
    }

    @Test
    void run_teeWithInvalidArgs_shouldThrowException() {
        String[] argList = new String[]{"-d", FILE1_NAME};
        assertThrows(Exception.class, () -> teeApplication.run(argList, inputStream, outputStream));
    }
}
