package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.GrepException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.GREP_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.appendToFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class GrepApplicationTest {

    private static final String INPUT = "The first file" + STRING_NEWLINE + "The second line" + STRING_NEWLINE + "1000" + STRING_NEWLINE;
    private static final String FILE1_NAME = "file1.txt";
    private static final String FILE2_NAME = "file2.txt";
    private static final String NE_FILE_NAME = "nonExistent.txt";
    private static final String[] LINES1 = {"The first file", "The second line", "1000"};
    private static final String[] LINES2 = {"The second file", "The second line", "10"};
    private static final String PATTERN1 = "The second";
    private static final String PATTERN1_INSEN = "THE SECoND";
    private static final String TEST_PATH = Environment.currentDirectory + CHAR_FILE_SEP + GREP_FOLDER;
    private static final String FILE1_PATH = TEST_PATH + CHAR_FILE_SEP + FILE1_NAME;
    private static final String FILE2_PATH = TEST_PATH + CHAR_FILE_SEP + FILE2_NAME;
    private static final String NE_FILE_PATH = TEST_PATH + CHAR_FILE_SEP + NE_FILE_NAME;
    private static GrepApplication grepApplication;
    private final InputStream inputStream = new ByteArrayInputStream(INPUT.getBytes());
    private OutputStream stdout;

    @BeforeAll
    static void setUp() {
        grepApplication = new GrepApplication();
    }

    @AfterEach
    void tearDown() throws IOException {
        deleteDir(new File(TEST_PATH));
    }

    @BeforeEach
    void setUpEach() throws IOException {
        stdout = new ByteArrayOutputStream();
        Files.createDirectory(Paths.get(TEST_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE2_PATH));

        appendToFile(Paths.get(FILE1_PATH), LINES1);
        appendToFile(Paths.get(FILE2_PATH), LINES2);
    }

    @Test
    void grepFromFiles_grepFromFile_shouldReturnCorrectLines() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, false, false, false, FILE1_PATH);
            String fileContent = readString(Paths.get(FILE1_PATH));
            StringBuilder stringBuilder = new StringBuilder();
            String[] lines = fileContent.split(STRING_NEWLINE);
            for (String line : lines) {
                if (line.contains(PATTERN1)) {
                    stringBuilder.append(line).append(STRING_NEWLINE);
                }
            }
            assertEquals(stringBuilder.toString(), actualOutput);
        } catch (Exception e) {
            throw new GrepException(e);
        }
    }

    @Test
    void grepFromFiles_grepFromMultipleFilesExistentAndNonExistent_shouldReturnCorrectLinesAndDisplayErrorMessage() throws GrepException {
        try {
            String[] files = {FILE1_PATH, FILE2_PATH, NE_FILE_PATH};
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, false, false, false, files);
            String fileContent1 = readString(Paths.get(FILE1_PATH));
            StringBuilder stringBuilder = new StringBuilder();
            String[] lines1 = fileContent1.split(STRING_NEWLINE);
            for (String line : lines1) {
                if (line.contains(PATTERN1)) {
                    stringBuilder.append(FILE1_PATH).append(": ").append(line).append(STRING_NEWLINE);
                }
            }
            String fileContent2 = readString(Paths.get(FILE2_PATH));
            String[] lines2 = fileContent2.split(STRING_NEWLINE);
            for (String line : lines2) {
                if (line.contains(PATTERN1)) {
                    stringBuilder.append(FILE2_PATH).append(": ").append(line).append(STRING_NEWLINE);
                }
            }
            String errorMsg = String.format("grep: %s: No such file or directory" + STRING_NEWLINE, NE_FILE_PATH);
            stringBuilder.append(errorMsg);
            assertEquals(stringBuilder.toString(), actualOutput);
        } catch (Exception e) {
            throw new GrepException(e);
        }
    }

    @Test
    void grepFromFiles_grepFromFileCountLines_shouldReturnCorrectCount() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, false, true, false, FILE1_PATH);
            String fileContent = readString(Paths.get(FILE1_PATH));
            String[] lines = fileContent.split(STRING_NEWLINE);
            long count = Arrays.stream(lines).filter(line -> line.contains(PATTERN1)).count();
            String expectedOutput = count + STRING_NEWLINE;
            assertEquals(expectedOutput, actualOutput);
        } catch (Exception e) {
            throw new GrepException(e);
        }
    }

    @Test
    void grepFromFiles_grepFromFileCaseInsensitive_shouldReturnCorrectLines() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, true, true, false, FILE1_PATH);
            String fileContent = readString(Paths.get(FILE1_PATH));
            String[] lines = fileContent.split(STRING_NEWLINE);
            long count = Arrays.stream(lines)
                    .filter(line -> Pattern.compile(Pattern.quote(PATTERN1_INSEN), Pattern.CASE_INSENSITIVE)
                            .matcher(line).find())
                    .count();
            String expectedOutput = count + STRING_NEWLINE;
            assertEquals(expectedOutput, actualOutput);
        } catch (Exception e) {
            throw new GrepException(e);
        }
    }

    @Test
    void grepFromFiles_grepFromFilePrintFileNames_shouldReturnCorrectLines() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, false, false, true, FILE1_PATH);
            String fileContent = readString(Paths.get(FILE1_PATH));
            StringBuilder stringBuilder = new StringBuilder();
            String[] lines = fileContent.split(STRING_NEWLINE);
            for (String line : lines) {
                if (line.contains(PATTERN1)) {
                    stringBuilder.append(FILE1_PATH).append(": ").append(line).append(STRING_NEWLINE);
                }
            }
            assertEquals(stringBuilder.toString(), actualOutput);
        } catch (Exception e) {
            throw new GrepException(e);
        }
    }

    @Test
    void grepFromFiles_grepFromNonExistentFile_shouldDisplayErrorMessage() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFiles(PATTERN1, false, false, false, "nonExistent.txt");
            String errorMsg = String.format("grep: %s: No such file or directory" + STRING_NEWLINE, "nonExistent.txt");
            assertEquals(errorMsg, actualOutput);
        } catch (Exception e) {
            throw new GrepException(e);
        }
    }

    @Test
    void grepFromStdin_grepFromStdin_shouldReturnCorrectLines() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromStdin(PATTERN1, false, false, false, inputStream);
            StringBuilder stringBuilder = new StringBuilder();
            String[] lines = INPUT.split(STRING_NEWLINE);
            for (String line : lines) {
                if (line.contains(PATTERN1)) {
                    stringBuilder.append(line).append(STRING_NEWLINE);
                }
            }
            assertEquals(stringBuilder.toString(), actualOutput);
        } catch (Exception e) {
            throw new GrepException(e);
        }
    }


    @Test
    void grepFromFileAndStdin_grepFromFileAndStdin_shouldReturnCorrectLines() throws GrepException {
        try {
            String actualOutput = grepApplication.grepFromFileAndStdin(PATTERN1, false, false, false, inputStream, FILE1_PATH);
            StringBuilder stringBuilder = new StringBuilder();
            String fileContent = readString(Paths.get(FILE1_PATH));
            String[] fileLines = fileContent.split(STRING_NEWLINE);
            for (String line : fileLines) {
                if (line.contains(PATTERN1)) {
                    stringBuilder.append(FILE1_PATH).append(": ").append(line).append(STRING_NEWLINE);
                }
            }

            String[] stdinLines = INPUT.split(STRING_NEWLINE);
            for (String line : stdinLines) {
                if (line.contains(PATTERN1)) {
                    stringBuilder.append("(standard input): ").append(line).append(STRING_NEWLINE);
                }
            }
            assertEquals(stringBuilder.toString(), actualOutput);
        } catch (Exception e) {
            throw new GrepException(e);
        }
    }

    @Test
    void run_nullStdin_shouldThrowGrepException() {
        assertThrows(GrepException.class, () -> grepApplication.run(new String[]{"abc"}, null, System.out));
    }

    @Test
    void run_invalidRegex_shouldThrowGrepException() {
        assertThrows(GrepException.class, () -> grepApplication.run(new String[]{"?i)"}, System.in, System.out));
    }

    @Test
    void run_validGrepFromStdin_shouldReturnGrepOutput() throws Exception {
        grepApplication.run(new String[]{"The second", "-"}, inputStream, stdout);
        assertEquals("The second line" + STRING_NEWLINE, stdout.toString());
    }

    @Test
    void run_validGrepFromFiles_shouldReturnGrepOutput() throws Exception {
        grepApplication.run(new String[]{"The second", FILE1_PATH}, inputStream, stdout);
        assertEquals("The second line" + STRING_NEWLINE, stdout.toString());
    }
}
