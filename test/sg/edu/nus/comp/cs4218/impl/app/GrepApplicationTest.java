package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.exception.GrepException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GrepApplicationTest {

    private static GrepApplication grepApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;

    public static final String input = "The first file\r\nThe second line\r\n1000\r\n";
    public final InputStream is = new ByteArrayInputStream(input.getBytes());

    public static final String FILE1_NAME = "file1.txt";
    public static final String FILE1_PATH = ROOT_PATH + "/" + FILE1_NAME;
    public static final String FILE2_NAME = "file2.txt";
    public static final String FILE2_PATH = ROOT_PATH + "/" + FILE2_NAME;

    public static final String[] LINES1 = {"The first file", "The second line", "1000"};
    public static final String[] LINES2 = {"The second file", "The second line", "10"};
    public static final String PATTERN1 = "The second";
    public static final String PATTERN1_CASE_INSEN = "THE SECoND";

    @BeforeAll
    static void setUp() {
        grepApplication = new GrepApplication();
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

    static void appendToFile(Path file, String[] lines) throws IOException {
        for (String line : lines) {
            Files.write(file, (line + "\r\n").getBytes(), APPEND);
        }
    }

    static String readString(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    @Test
    void testGrep_grepFromFile_shouldReturnCorrectLines() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, false, false, false, FILE1_NAME);
            String fileContent = readString(Paths.get(FILE1_NAME));
            StringBuilder sb = new StringBuilder();
            String[] lines = fileContent.split("\r\n");
            for (String line: lines) {
                if (line.contains(PATTERN1)) {
                    sb.append(line).append("\r\n");
                }
            }
            assertEquals(sb.toString(), actualOutput);
        } catch (Exception e) {
            throw new GrepException(e.getMessage());
        }
    }

    @Test
    void testGrep_grepFromMultipleFilesExistentAndNonExistent_shouldReturnCorrectLinesAndDisplayErrorMessage() throws GrepException {
        try {
            String[] files = {FILE1_NAME, FILE2_NAME, "nonExistent.txt"};
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, false, false, false, files);
            String fileContent1 = readString(Paths.get(FILE1_NAME));
            StringBuilder sb = new StringBuilder();
            String[] lines1 = fileContent1.split("\r\n");
            for (String line: lines1) {
                if (line.contains(PATTERN1)) {
                    sb.append(FILE1_NAME).append(": ").append(line).append("\r\n");
                }
            }
            String fileContent2 = readString(Paths.get(FILE2_NAME));
            String[] lines2 = fileContent2.split("\r\n");
            for (String line: lines2) {
                if (line.contains(PATTERN1)) {
                    sb.append(FILE2_NAME).append(": ").append(line).append("\r\n");
                }
            }
            String errorMsg = String.format("grep: %s: No such file or directory\r\n", "nonExistent.txt");
            sb.append(errorMsg);
            assertEquals(sb.toString(), actualOutput);
        } catch (Exception e) {
            throw new GrepException(e.getMessage());
        }
    }

    @Test
    void testGrep_grepFromFileCountLines_shouldReturnCorrectCount() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, false, true, false, FILE1_NAME);
            String fileContent = readString(Paths.get(FILE1_NAME));
            StringBuilder sb = new StringBuilder();
            String[] lines = fileContent.split("\r\n");
            long count = Arrays.stream(lines).filter(line -> line.contains(PATTERN1)).count();
            String expectedOutput = String.valueOf(count) + "\r\n";
            assertEquals(expectedOutput, actualOutput);
        } catch (Exception e) {
            throw new GrepException(e.getMessage());
        }
    }

    @Test
    void testGrep_grepFromFileCaseInsensitive_shouldReturnCorrectLines() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, true, true, false, FILE1_NAME);
            String fileContent = readString(Paths.get(FILE1_NAME));
            StringBuilder sb = new StringBuilder();
            String[] lines = fileContent.split("\r\n");
            long count = Arrays.stream(lines)
                    .filter(line -> Pattern.compile(Pattern.quote(PATTERN1_CASE_INSEN), Pattern.CASE_INSENSITIVE)
                            .matcher(line).find())
                    .count();
            String expectedOutput = String.valueOf(count) + "\r\n";
            assertEquals(expectedOutput, actualOutput);
        } catch (Exception e) {
            throw new GrepException(e.getMessage());
        }
    }

    @Test
    void testGrep_grepFromFilePrintFileNames_shouldReturnCorrectLines() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, false, false, true, FILE1_NAME);
            String fileContent = readString(Paths.get(FILE1_NAME));
            StringBuilder sb = new StringBuilder();
            String[] lines = fileContent.split("\r\n");
            for (String line: lines) {
                if (line.contains(PATTERN1)) {
                    sb.append(FILE1_NAME).append(": ").append(line).append("\r\n");
                }
            }
            assertEquals(sb.toString(), actualOutput);
        } catch (Exception e) {
            throw new GrepException(e.getMessage());
        }
    }

    @Test
    void testGrep_grepFromNonExistentFile_shouldDisplayErrorMessage() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, false, false, false, "nonExistent.txt");
            String errorMsg = String.format("grep: %s: No such file or directory\r\n", "nonExistent.txt");
            assertEquals(errorMsg, actualOutput);
        } catch (Exception e) {
            throw new GrepException(e.getMessage());
        }
    }

    @Test
    void testGrep_grepFromStdin_shouldReturnCorrectLines() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromStdin(PATTERN1, false, false, false, is);
            StringBuilder sb = new StringBuilder();
            String[] lines = input.split("\r\n");
            for (String line: lines) {
                if (line.contains(PATTERN1)) {
                    sb.append(line).append("\r\n");
                }
            }
            assertEquals(sb.toString(), actualOutput);
        } catch (Exception e) {
            throw new GrepException(e.getMessage());
        }
    }



    @Test
    void testGrep_grepFromFileAndStdin_shouldReturnCorrectLines() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFileAndStdin(PATTERN1, false, false, false, is, FILE1_NAME);
            StringBuilder sb = new StringBuilder();
            String fileContent = readString(Paths.get(FILE1_NAME));
            String[] fileLines = fileContent.split("\r\n");
            for (String line: fileLines) {
                if (line.contains(PATTERN1)) {
                    sb.append(FILE1_NAME).append(": ").append(line).append("\r\n");
                }
            }

            String[] stdinLines = input.split("\r\n");
            for (String line: stdinLines) {
                if (line.contains(PATTERN1)) {
                    sb.append("(standard input): ").append(line).append("\r\n");
                }
            }
            assertEquals(sb.toString(), actualOutput);
        } catch (Exception e) {
            throw new GrepException(e.getMessage());
        }
    }


}
