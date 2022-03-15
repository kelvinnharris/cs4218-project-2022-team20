package sg.edu.nus.comp.cs4218.impl.app;

import javafx.util.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CutException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

class CutApplicationTest {
    private CutApplication cutApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCutTestFolder" + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FILE_SINGLE_LINE = TEST_PATH + "fileSingleLine.txt";
    private static final String FILE_MULT_LINES = TEST_PATH + "fileMultipleLines.txt";
    private static final String ABC = "abc";
    private static final String EFG = "efg";
    private static final String ABCD = "abcd";
    private static final String EFGH = "efgh";


    @BeforeEach
    public void setUpEach() {
        cutApplication = new CutApplication();
    }

    @BeforeAll
    static void setUp() {
        try {
            deleteDir(new File(TEST_PATH));
            Files.createDirectories(Paths.get(TEST_PATH));
            Files.createFile(Paths.get(FILE_SINGLE_LINE));
            Files.createFile(Paths.get(FILE_MULT_LINES));
            Files.write(Paths.get(FILE_SINGLE_LINE), (ABCD).getBytes(), APPEND);
            Files.write(Paths.get(FILE_MULT_LINES), (ABCD + STRING_NEWLINE + EFGH).getBytes(), APPEND);

        } catch (IOException ioe) {
            System.err.println("error creating temporary test file " + ioe);
        }
    }


    @AfterAll
    static void tearDown() throws IOException {
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void cutFromFiles_byCharSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));
        String output = cutApplication.cutFromFiles(true, false, ranges, FILE_SINGLE_LINE);
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));
        String output = cutApplication.cutFromFiles(false, true, ranges, FILE_SINGLE_LINE);
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byCharRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        String output = cutApplication.cutFromFiles(true, false, ranges, FILE_SINGLE_LINE);
        assertEquals(ABC + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        String output = cutApplication.cutFromFiles(false, true, ranges, FILE_SINGLE_LINE);
        assertEquals(ABC + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byCharSingleIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));
        String output = cutApplication.cutFromFiles(true, false, ranges, FILE_MULT_LINES);
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteSingleIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));
        String output = cutApplication.cutFromFiles(false, true, ranges, FILE_MULT_LINES);
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byCharRangeIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        String output = cutApplication.cutFromFiles(true, false, ranges, FILE_MULT_LINES);
        assertEquals(ABC + STRING_NEWLINE + EFG + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        String output = cutApplication.cutFromFiles(false, true, ranges, FILE_MULT_LINES);
        assertEquals(ABC + STRING_NEWLINE + EFG + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleFiles_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        String output = cutApplication.cutFromFiles(false, true, ranges, new String[]{FILE_SINGLE_LINE, FILE_MULT_LINES});
        assertEquals(output, ABC + STRING_NEWLINE + ABC + STRING_NEWLINE + EFG + STRING_NEWLINE);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleFilesAndStdin_returnsLines() throws Exception {
        String inputString = "z" + STRING_NEWLINE + "yy" + STRING_NEWLINE + "x" + STRING_NEWLINE + "www";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        cutApplication.stdin = input;
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        String output = cutApplication.cutFromFiles(false, true, ranges, new String[]{FILE_SINGLE_LINE, "-", FILE_MULT_LINES});
        assertEquals(output, ABC + STRING_NEWLINE + "z" + STRING_NEWLINE + "yy" + STRING_NEWLINE + "x" + STRING_NEWLINE + "www" + STRING_NEWLINE + ABC + STRING_NEWLINE + EFG + STRING_NEWLINE);
    }


    @Test
    void cutFromStdin_byCharSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream(ABCD.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));
        String output = cutApplication.cutFromStdin(true, false, ranges, input);
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream(ABCD.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));
        String output = cutApplication.cutFromStdin(false, true, ranges, input);
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byCharRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream(ABCD.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        String output = cutApplication.cutFromStdin(true, false, ranges, input);
        assertEquals(ABC + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream(ABCD.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        String output = cutApplication.cutFromStdin(false, true, ranges, input);
        assertEquals(ABC + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byCharSingleIndexMultipleLines_returnsLines() throws Exception {
        String inputString = ABCD + STRING_NEWLINE + EFGH;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));
        String output = cutApplication.cutFromStdin(true, false, ranges, input);
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteSingleIndexMultipleLines_returnsLines() throws Exception {
        String inputString = ABCD + STRING_NEWLINE + EFGH;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));
        String output = cutApplication.cutFromStdin(false, true, ranges, input);
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);

    }

    @Test
    void cutFromStdin_byCharRangeIndexMultipleLines_returnsLines() throws Exception {
        String inputString = ABCD + STRING_NEWLINE + EFGH;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        String output = cutApplication.cutFromStdin(true, false, ranges, input);
        assertEquals(ABC + STRING_NEWLINE + EFG + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteRangeIndexMultipleLines_returnsLines() throws Exception {
        String inputString = ABCD + STRING_NEWLINE + EFGH;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 2));
        String output = cutApplication.cutFromStdin(false, true, ranges, input);
        assertEquals(ABC + STRING_NEWLINE + EFG + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_EmptyString_returnsLines() throws Exception {
        String inputString = STRING_NEWLINE;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));
        String output = cutApplication.cutFromStdin(true, false, ranges, input);
        assertEquals(STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_MultipleEmptyString_returnsLines() throws Exception {
        String inputString = STRING_NEWLINE + STRING_NEWLINE;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(0, 0));
        String output = cutApplication.cutFromStdin(true, false, ranges, input);
        assertEquals(STRING_NEWLINE + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byCharIndexMoreThanInputSize_returnsNull() throws Exception {
        String inputString = "a";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(1, 1));
        String output = cutApplication.cutFromStdin(true, false, ranges, input);
        char[] charArray = new char[1];
        String expected = "" + STRING_NEWLINE;
        assertEquals(expected, output);
    }

    @Test
    void cutFromStdin_byByteIndexMoreThanInputSize_returnsNull() throws Exception {
        String inputString = "a";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        List<Pair<Integer, Integer>> ranges = new ArrayList<>();
        ranges.add(new Pair<>(1, 1));
        String output = cutApplication.cutFromStdin(false, true, ranges, input);
        byte[] byteArray = new byte[1];
        String expected = "" + STRING_NEWLINE;
        assertEquals(expected, output);
    }

    @Test
    void run_nonEmptyArgsNonEmptyIndex_shouldPassed() {
        String inputString = "a";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String[] args = new String[]{"-b", "1"};
        assertDoesNotThrow(() -> cutApplication.run(args, input, System.out));
    }

    @Test
    void run_emptyArgsNonEmptyIndex_shouldThrow() {
        String inputString = "a";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String[] args = new String[]{"1"};
        assertThrows(CutException.class, () -> cutApplication.run(args, input, System.out));
    }

    @Test
    void run_nonEmptyArgsEmptyIndex_shouldThrow() {
        String inputString = "a";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String[] args = new String[]{"-b"};
        assertThrows(CutException.class, () -> cutApplication.run(args, input, System.out));
    }

    @Test
    void run_emptyArgsEmptyIndex_shouldThrow() {
        String inputString = "a";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String[] args = new String[]{};
        assertThrows(CutException.class, () -> cutApplication.run(args, input, System.out));
    }

    @Test
    void run_nullOutputStream_shouldThrow() {
        String inputString = "a";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String[] args = new String[]{"-b", "1"};
        assertThrows(CutException.class, () -> cutApplication.run(args, input, null));
    }

    @Test
    void run_argsError_shouldThrow() {
        String inputString = "a";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String[] args = new String[]{"-b", "-c", "1"};
        assertThrows(CutException.class, () -> cutApplication.run(args, input, System.out));
    }


}