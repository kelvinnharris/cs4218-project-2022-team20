package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.util.TestUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class UniqApplicationTest {
    private UniqApplication uniqApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpUniqTestFolder" + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FILE_NAME = TEST_PATH + "file.txt";
    private static final String FILE_NAME2 = TEST_PATH + "file2.txt";
    private static final String FILE_NAME3 = TEST_PATH + "file3.txt";
    private static final String HELLO_WORLD = "Hello World";
    private static final String ALICE = "Alice";
    private static final String BOB = "Bob";
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeAll
    public static void setUp() throws IOException {
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
        Files.write(Path.of(FILE_NAME), (HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
    }

    @BeforeEach
    public void setUpEach() {
        uniqApplication = new UniqApplication();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDownEach() {
        File file = new File(FILE_NAME2);
        if (file.exists()) {
            TestUtils.deleteDir(file);
        }
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void uniqFromStdin_countNotRepeatedNotAllRepeated_returnsOutput() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = "2 " + HELLO_WORLD + STRING_NEWLINE +
                "2 " + ALICE + STRING_NEWLINE +
                "1 " + BOB + STRING_NEWLINE +
                "1 " + ALICE + STRING_NEWLINE +
                "1 " + BOB + STRING_NEWLINE;
        String output = uniqApplication.uniqFromStdin(true, false, false, input, null);
        assertEquals(expected, output);
    }

    @Test
    void uniqFromStdin_notCountRepeatedNotAllRepeated_returnsOutput() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE + ALICE + STRING_NEWLINE;
        String output = uniqApplication.uniqFromStdin(false, true, false, input, null);
        assertEquals(expected, output);
    }

    @Test
    void uniqFromStdin_notCountNotRepeatedAllRepeated_returnsOutput() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE + HELLO_WORLD + STRING_NEWLINE + ALICE + STRING_NEWLINE + ALICE + STRING_NEWLINE;
        String output = uniqApplication.uniqFromStdin(false, false, true, input, null);
        assertEquals(expected, output);
    }

    @Test
    void uniqFromStdin_CountRepeatedAllRepeated_returnsOutput() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = "2 " + HELLO_WORLD + STRING_NEWLINE + "2 " + HELLO_WORLD + STRING_NEWLINE + "2 " + ALICE + STRING_NEWLINE + "2 " + ALICE + STRING_NEWLINE;
        String output = uniqApplication.uniqFromStdin(true, true, true, input, null);
        assertEquals(expected, output);
    }

    @Test
    void uniqFromStdin_NotCountNotRepeatedNotAllRepeated_returnsOutput() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE;
        String output = uniqApplication.uniqFromStdin(false, false, false, input, null);
        assertEquals(expected, output);
    }

    @Test
    void uniqFromFile_countNotRepeatedNotAllRepeated_returnsOutput() throws Exception {
        String expected = "2 " + HELLO_WORLD + STRING_NEWLINE +
                "2 " + ALICE + STRING_NEWLINE +
                "1 " + BOB + STRING_NEWLINE +
                "1 " + ALICE + STRING_NEWLINE +
                "1 " + BOB + STRING_NEWLINE;
        String output = uniqApplication.uniqFromFile(true, false, false, FILE_NAME, FILE_NAME2);
        assertEquals(expected, output);
    }

    @Test
    void uniqFromFile_notCountRepeatedNotAllRepeated_returnsOutput() throws Exception {
        String expected = HELLO_WORLD + STRING_NEWLINE + ALICE + STRING_NEWLINE;
        String output = uniqApplication.uniqFromFile(false, true, false, FILE_NAME, FILE_NAME2);
        assertEquals(expected, output);
    }

    @Test
    void uniqFromFile_notCountNotRepeatedAllRepeated_returnsOutput() throws Exception {
        String expected = HELLO_WORLD + STRING_NEWLINE + HELLO_WORLD + STRING_NEWLINE + ALICE + STRING_NEWLINE + ALICE + STRING_NEWLINE;
        String output = uniqApplication.uniqFromFile(false, false, true, FILE_NAME, FILE_NAME2);
        assertEquals(expected, output);
    }

    @Test
    void uniqFromFile_CountRepeatedAllRepeated_returnsOutput() throws Exception {
        String expected = "2 " + HELLO_WORLD + STRING_NEWLINE + "2 " + HELLO_WORLD + STRING_NEWLINE + "2 " + ALICE + STRING_NEWLINE + "2 " + ALICE + STRING_NEWLINE;
        String output = uniqApplication.uniqFromFile(true, true, true, FILE_NAME, FILE_NAME2);
        assertEquals(expected, output);
    }

    @Test
    void uniqFromFile_CountRepeatedNotAllRepeated_returnsOutput() throws Exception {
        String expected = "2 " + HELLO_WORLD + STRING_NEWLINE + "2 " + ALICE + STRING_NEWLINE;
        String output = uniqApplication.uniqFromFile(true, true, false, FILE_NAME, FILE_NAME2);
        assertEquals(expected, output);
    }

    @Test
    void uniqFromFile_NotCountNotRepeatedNotAllRepeated_returnsOutput() throws Exception {
        String expected = HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE;
        String output = uniqApplication.uniqFromFile(false, false, false, FILE_NAME, FILE_NAME2);
        assertEquals(expected, output);
    }

    @Test
    void run_NotCountNotRepeatedNotAllRepeatedStdin_returnsOutputStdOut() throws Exception {
        InputStream stdin = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE;
        String[] args = {};
        uniqApplication.run(args, stdin, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_NotCountNotRepeatedNotAllRepeatedFiles_returnsOutputStdOut() throws Exception {
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE;
        String[] args = {FILE_NAME};
        uniqApplication.run(args, stdin, outputStream);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void run_NotCountNotRepeatedNotAllRepeated_overwritesOutputFile() throws Exception {
        Files.write(Path.of(FILE_NAME2), (BOB).getBytes());
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE;
        String[] args = {FILE_NAME, FILE_NAME2};
        uniqApplication.run(args, stdin, outputStream);
        String actual = Files.readString(Paths.get(FILE_NAME2));
        assertEquals(expected, actual);
        assertEquals("", outputStream.toString());
    }

    @Test
    void run_NotCountNotRepeatedNotAllRepeated_createsOutputFile() throws Exception {
        InputStream stdin = new ByteArrayInputStream("".getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE;
        String[] args = {FILE_NAME, FILE_NAME3};
        uniqApplication.run(args, stdin, outputStream);
        String actual = Files.readString(Paths.get(FILE_NAME3));
        assertEquals(expected, actual);
        assertEquals("", outputStream.toString());
    }

    @Test
    void run_NotCountNotRepeatedNotAllRepeatedDash_returnsOutputStdOut() throws Exception {
        String stdinInput = HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB;
        InputStream stdin = new ByteArrayInputStream(stdinInput.getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE;
        String[] args = {"-"};
        uniqApplication.run(args, stdin, outputStream);
        assertEquals(expected, outputStream.toString());
    }


    @Test
    void uniqFromFile_IsDir_throwsException() {
        assertThrows(Exception.class, () -> uniqApplication.uniqFromFile(false, false, false, TEST_PATH, null));
    }

}
