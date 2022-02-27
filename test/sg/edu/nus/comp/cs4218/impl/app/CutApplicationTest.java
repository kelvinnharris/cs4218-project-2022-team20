package sg.edu.nus.comp.cs4218.impl.app;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class CutApplicationTest {
    private CutApplication cutApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCutTestFolder" + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FILE_SINGLE_LINE = TEST_PATH + "fileSingleLine.txt";
    private static final String FILE_MULTIPLE_LINES = TEST_PATH + "fileMultipleLines.txt"; //NOPMD
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
            Files.createFile(Paths.get(FILE_MULTIPLE_LINES));
            Files.write(Paths.get(FILE_SINGLE_LINE), (ABCD).getBytes(), APPEND);
            Files.write(Paths.get(FILE_MULTIPLE_LINES), (ABCD + STRING_NEWLINE + EFGH).getBytes(), APPEND);

        } catch (IOException ioe) {
            System.err.println("error creating temporary test file " + ioe);
        }
    }


    @AfterAll
    static void tearDown() throws IOException {
        deleteDir(new File(FILE_SINGLE_LINE));
        deleteDir(new File(FILE_MULTIPLE_LINES));
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

    @Test
    void cutFromFiles_byCharSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(true, false, false, 0, 1, FILE_SINGLE_LINE);
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, false, 0, 1, FILE_SINGLE_LINE);
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byCharRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(true, false, true, 0, 2, FILE_SINGLE_LINE);
        assertEquals(ABC + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, FILE_SINGLE_LINE);
        assertEquals(ABC + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byCharSingleIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(true, false, false, 0, 1, FILE_MULTIPLE_LINES);
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteSingleIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, false, 0, 1, FILE_MULTIPLE_LINES);
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byCharRangeIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(true, false, true, 0, 2, FILE_MULTIPLE_LINES);
        assertEquals(ABC + STRING_NEWLINE + EFG + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, FILE_MULTIPLE_LINES);
        assertEquals(ABC + STRING_NEWLINE + EFG + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleFiles_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, new String[]{FILE_SINGLE_LINE, FILE_MULTIPLE_LINES});
        assertEquals(output, ABC + STRING_NEWLINE + ABC + STRING_NEWLINE + EFG + STRING_NEWLINE);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleFilesAndStdin_returnsLines() throws Exception {
        String inputString = "z" + STRING_NEWLINE + "yy" + STRING_NEWLINE + "x" + STRING_NEWLINE + "www";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, new String[]{FILE_SINGLE_LINE, "-", FILE_MULTIPLE_LINES});
        assertEquals(output, ABC + STRING_NEWLINE + "z" + STRING_NEWLINE + "yy" + STRING_NEWLINE + "x" + STRING_NEWLINE + "www" + STRING_NEWLINE + ABC + STRING_NEWLINE + EFG + STRING_NEWLINE);
    }


    @Test
    void cutFromStdin_byCharSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream(ABCD.getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 0, 1, input);
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream(ABCD.getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 0, 1, input);
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byCharRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream(ABCD.getBytes());
        String output = cutApplication.cutFromStdin(true, false, true, 0, 2, input);
        assertEquals(ABC + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream(ABCD.getBytes());
        String output = cutApplication.cutFromStdin(false, true, true, 0, 2, input);
        assertEquals(ABC + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byCharSingleIndexMultipleLines_returnsLines() throws Exception {
        String inputString = ABCD + STRING_NEWLINE + EFGH;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 0, 1, input);
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteSingleIndexMultipleLines_returnsLines() throws Exception {
        String inputString = ABCD + STRING_NEWLINE + EFGH;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 0, 1, input);
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);

    }

    @Test
    void cutFromStdin_byCharRangeIndexMultipleLines_returnsLines() throws Exception {
        String inputString = ABCD + STRING_NEWLINE + EFGH;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = cutApplication.cutFromStdin(true, false, true, 0, 2, input);
        assertEquals(ABC + STRING_NEWLINE + EFG + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteRangeIndexMultipleLines_returnsLines() throws Exception {
        String inputString = ABCD + STRING_NEWLINE + EFGH;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = cutApplication.cutFromStdin(false, true, true, 0, 2, input);
        assertEquals(ABC + STRING_NEWLINE + EFG + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_EmptyString_returnsLines() throws Exception {
        String inputString = STRING_NEWLINE;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 0, 1, input);
        assertEquals(STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_MultipleEmptyString_returnsLines() throws Exception {
        String inputString = STRING_NEWLINE + STRING_NEWLINE;
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 0, 1, input);
        assertEquals(STRING_NEWLINE + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byCharIndexMoreThanInputSize_returnsNull() throws Exception {
        String inputString = "a";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, input);
        char[] charArray = new char[1];
        String expected = new String(charArray) + STRING_NEWLINE;
        assertEquals(expected , output);
    }

    @Test
    void cutFromStdin_byByteIndexMoreThanInputSize_returnsNull() throws Exception {
        String inputString = "a";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 1, 2, input);
        byte[] byteArray = new byte[1];
        String expected = new String(byteArray) + STRING_NEWLINE;
        assertEquals(expected , output);
    }

}