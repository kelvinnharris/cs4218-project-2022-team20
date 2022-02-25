package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.exception.TeeException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class TeeApplicationTest {

    private static TeeApplication teeApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;

    public static final String INPUT = "hello" + STRING_NEWLINE + "world" + STRING_NEWLINE +"goodbye" + STRING_NEWLINE + "world" + STRING_NEWLINE;
    public final InputStream inputStream = new ByteArrayInputStream(INPUT.getBytes());

    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = ROOT_PATH + CHAR_FILE_SEP + FILE1_NAME;
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE2_PATH = ROOT_PATH + CHAR_FILE_SEP + FILE2_NAME;

    public static final String[] LINES1 = {"The first file", "The second line"};
    public static final String[] LINES2 = {"The second file", "The second line"};

    @BeforeAll
    static void setUp() {
        teeApplication = new TeeApplication();
    }

    @BeforeEach
    void setUpEach() throws IOException {
        Environment.currentDirectory = ROOT_PATH;
        Files.deleteIfExists(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.deleteIfExists(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(FILE2_PATH));

        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);
    }

    @AfterAll
    static void tearDown() throws IOException {
        Files.delete(Paths.get(FILE1_PATH));
        Files.delete(Paths.get(FILE2_PATH));
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

    static void appendToFile(Path file, String... lines) throws IOException {
        for (String line : lines) {
            Files.write(file, (line + STRING_NEWLINE).getBytes(), APPEND);
        }
    }

    static String readString(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    @Test
    void testTee_teeWithValidFile_shouldOverwritePreviousContent() throws TeeException {
        try {
            teeApplication.teeFromStdin(false, inputStream, FILE1_NAME);
            String fileContent = readString(Paths.get(FILE1_NAME));
            assertEquals(INPUT, fileContent);
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }

    @Test
    void testTee_teeWithValidFiles_shouldOverwritePreviousContent() throws TeeException {
        try {
            String[] files = {FILE1_NAME, FILE2_NAME};
            teeApplication.teeFromStdin(false, inputStream, files);
            String file1Content = readString(Paths.get(FILE1_NAME));
            String file2Content = readString(Paths.get(FILE2_NAME));
            assertEquals(INPUT, file1Content);
            assertEquals(INPUT, file2Content);
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }

    @Test
    void testTee_teeWithValidFileAppend_shouldAppendToFile() throws TeeException {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : LINES1) {
                stringBuilder.append(s).append(STRING_NEWLINE);
            }
            stringBuilder.append(INPUT);
            teeApplication.teeFromStdin(true, inputStream, FILE1_NAME);
            String fileContent = readString(Paths.get(FILE1_NAME));
            assertEquals(stringBuilder.toString(), fileContent);
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }


    @Test
    void testTee_teeWithSameNameFilesAppend_shouldAppendOrderPreserved() throws TeeException {
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
            String[] files = {FILE1_NAME, FILE1_NAME};
            teeApplication.teeFromStdin(true, inputStream, files);
            String fileContent = readString(Paths.get(FILE1_NAME));
            assertEquals(stringBuilder.toString(), fileContent);
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }

    @Test
    void testTee_teeWithFolderAsInputFile_shouldThrowTeeException() throws IOException {
        String folderName = "folder";
        String folderPath = ROOT_PATH + CHAR_FILE_SEP + folderName;
        Files.deleteIfExists(Paths.get(folderPath));
        Files.createDirectories(Paths.get(folderPath));
        assertThrows(TeeException.class, () -> teeApplication.teeFromStdin(false, inputStream, folderName));
        deleteDir(new File(folderPath));
    }

    @Test
    void testTee_teeWithNonExistentFile_shouldCreateNewFileAndWrite() throws TeeException {
        try {
            teeApplication.teeFromStdin(false, inputStream, "nonExistent.txt");
            String fileContent = readString(Paths.get("nonExistent.txt"));
            assertEquals(INPUT, fileContent);
        } catch (Exception e) {
            throw new TeeException(e);
        }
    }
}
