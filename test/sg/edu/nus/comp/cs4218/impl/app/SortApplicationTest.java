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

class SortApplicationTest {
    private static SortApplication sortApplication;

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpSortTestFolder" + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    static String file = "file.txt";
    static String file2 = "file2.txt";
    static String path = TEST_PATH + file;
    static String path2 = TEST_PATH + file2;

    @BeforeAll
    static void setUp() {
        sortApplication = new SortApplication();

        try {
            deleteDir(new File(TEST_PATH));
            Files.createDirectories(Paths.get(TEST_PATH));

            Files.createFile(Paths.get(path));
            Files.createFile(Paths.get(path2));
            Files.write(Paths.get(path), ("10" + STRING_NEWLINE + "1" + STRING_NEWLINE + "2").getBytes(), APPEND);
            Files.write(Paths.get(path2), ("a" + STRING_NEWLINE + "A" + STRING_NEWLINE + "ab" + STRING_NEWLINE + "AB").getBytes(), APPEND);

        } catch (IOException ioe) {
            System.err.println("error creating temporary test file " + ioe);
        }
    }


    @AfterAll
    static void tearDown() throws IOException {
        deleteDir(new File(TEST_PATH));
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
    void sortFromFiles_firstWordNumberNotReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        String output = sortApplication.sortFromFiles(true, false, false, new String[]{path});
        assertEquals("1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "10", output);

    }

    @Test
    void sortFromFiles_notFirstWordNumberReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        String output = sortApplication.sortFromFiles(false, true, false, new String[]{path});
        assertEquals("2" + STRING_NEWLINE + "10" + STRING_NEWLINE + "1", output);

    }

    @Test
    void sortFromFiles_notFirstWordNumberNotReverseOrderCaseIndependent_returnsLines() throws Exception {
        String output = sortApplication.sortFromFiles(false, false, true, new String[]{path});
        assertEquals("1" + STRING_NEWLINE + "10" + STRING_NEWLINE + "2", output);

    }

    @Test
    void sortFromFiles_firstWordNumberReverseOrderCaseIndependent_returnsLines() throws Exception {
        String output = sortApplication.sortFromFiles(true, true, true, new String[]{path});
        assertEquals("10" + STRING_NEWLINE + "2" + STRING_NEWLINE + "1", output);

    }

    @Test
    void sortFromFiles_notFirstWordNumberNotReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        String output = sortApplication.sortFromFiles(false, false, false, new String[]{path});
        assertEquals("1" + STRING_NEWLINE + "10" + STRING_NEWLINE + "2", output);

    }

    @Test
    void sortFromFiles_firstWordNumberReverseOrderCaseIndependentMultipleFiles_returnsLines() throws Exception {
        String output = sortApplication.sortFromFiles(true, true, true, new String[]{path,path2});
        assertEquals("AB" + STRING_NEWLINE + "ab" + STRING_NEWLINE + "A" + STRING_NEWLINE + "a" + STRING_NEWLINE + "10" + STRING_NEWLINE + "2" + STRING_NEWLINE + "1", output);

    }

    @Test
    void sortFromStdin_firstWordNumberNotReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        String inputString = "10" + STRING_NEWLINE + "1" + STRING_NEWLINE + "2";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = sortApplication.sortFromStdin(true, false, false, input);
        assertEquals("1" + STRING_NEWLINE + "2" + STRING_NEWLINE + "10", output);
    }

    @Test
    void sortFromStdin_notFirstWordNumberReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        String inputString = "10" + STRING_NEWLINE + "1" + STRING_NEWLINE + "2";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = sortApplication.sortFromStdin(false, true, false, input);
        assertEquals("2" + STRING_NEWLINE + "10" + STRING_NEWLINE + "1", output);
    }

    @Test
    void sortFromStdin_notFirstWordNumberNotReverseOrderCaseIndependent_returnsLines() throws Exception {
        String inputString = "10" + STRING_NEWLINE + "1" + STRING_NEWLINE + "2";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = sortApplication.sortFromStdin(false, false, true, input);
        assertEquals("1" + STRING_NEWLINE + "10" + STRING_NEWLINE + "2", output);
    }

    @Test
    void sortFromStdin_firstWordNumberReverseOrderCaseIndependent_returnsLines() throws Exception {
        String inputString = "10" + STRING_NEWLINE + "1" + STRING_NEWLINE + "2";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = sortApplication.sortFromStdin(true, true, true, input);
        assertEquals("10" + STRING_NEWLINE + "2" + STRING_NEWLINE + "1", output);
    }

    @Test
    void sortFromStdin_notFirstWordNumberNotReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        String inputString = "10" + STRING_NEWLINE + "1" + STRING_NEWLINE + "2";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = sortApplication.sortFromStdin(false, false, false, input);
        assertEquals("1" + STRING_NEWLINE + "10" + STRING_NEWLINE + "2", output);
    }

    @Test
    void sortFromStdin_notFirstWordNumberNotReverseOrderCaseIndependentLetters_returnsLines() throws Exception {
        String inputString = "ab" + STRING_NEWLINE + "A" + STRING_NEWLINE + "a" + STRING_NEWLINE + "AB";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = sortApplication.sortFromStdin(false, false, true, input);
        assertEquals("A" + STRING_NEWLINE + "a" + STRING_NEWLINE + "ab" + STRING_NEWLINE + "AB", output);

    }

    @Test
    void sortFromStdin_notFirstWordNumberNotReverseOrderNotCaseIndependentLetters_returnsLines() throws Exception {
        String inputString = "ab" + STRING_NEWLINE + "A" + STRING_NEWLINE + "a" + STRING_NEWLINE + "AB";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        String output = sortApplication.sortFromStdin(false, false, false, input);
        assertEquals("A" + STRING_NEWLINE + "AB" + STRING_NEWLINE + "a" + STRING_NEWLINE + "ab", output);
    }
}