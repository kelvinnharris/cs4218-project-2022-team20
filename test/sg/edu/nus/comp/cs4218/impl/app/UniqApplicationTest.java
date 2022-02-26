package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class UniqApplicationTest {
    private UniqApplication uniqApplication;
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private static final String HELLO_WORLD = "Hello World";
    private static final String ALICE = "Alice";
    private static final String BOB = "Bob";
    private static final String FILE_NAME = "file.txt";


    @BeforeEach
    public void setUpEach() {
        uniqApplication = new UniqApplication();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void uniqFromStdin_countNotRepeatedNotAllRepeated_returnsLinesInStdout() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = "2 " + HELLO_WORLD + STRING_NEWLINE +
                "2 " + HELLO_WORLD + STRING_NEWLINE +
                "1 " + BOB + STRING_NEWLINE +
                "1 " + ALICE + STRING_NEWLINE +
                "1 " + BOB;
        uniqApplication.uniqFromStdin(true, false, false, input, null);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void uniqFromStdin_notCountRepeatedNotAllRepeated_returnsLinesInStdout() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE + ALICE;
        uniqApplication.uniqFromStdin(false, true, false, input, null);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void uniqFromStdin_notCountNotRepeatedAllRepeated_returnsLinesInStdout() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE + HELLO_WORLD + STRING_NEWLINE + ALICE + STRING_NEWLINE + ALICE;
        uniqApplication.uniqFromStdin(false, false, true, input, null);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void uniqFromStdin_CountRepeatedAllRepeated_returnsLinesInStdout() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = "2 " + HELLO_WORLD + STRING_NEWLINE + "2 " + HELLO_WORLD + STRING_NEWLINE + "2 " + ALICE + STRING_NEWLINE + "2 " + ALICE;
        uniqApplication.uniqFromStdin(true, true, true, input, null);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void uniqFromStdin_NotCountNotRepeatedNotAllRepeated_returnsLinesInStdout() throws Exception {
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
                BOB;
        uniqApplication.uniqFromStdin(false, false, false, input, null);
        assertEquals(expected, outputStream.toString());
    }

    @Test
    void uniqFromFiles_countNotRepeatedNotAllRepeated_returnsOutputFile() throws Exception {
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
                "1 " + BOB;
        uniqApplication.uniqFromStdin(true, false, false, input, FILE_NAME);
        String actual = String.valueOf(Files.readAllLines(Paths.get(FILE_NAME)));
        assertEquals(expected, actual);
    }

    @Test
    void uniqFromFiles_notCountRepeatedNotAllRepeated_returnsOutputFile() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE + ALICE;
        uniqApplication.uniqFromStdin(false, true, false, input, FILE_NAME);
        String actual = String.valueOf(Files.readAllLines(Paths.get(FILE_NAME)));
        assertEquals(expected, actual);
    }

    @Test
    void uniqFromFiles_notCountNotRepeatedAllRepeated_returnsOutputFile() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = HELLO_WORLD + STRING_NEWLINE + HELLO_WORLD + STRING_NEWLINE + ALICE + STRING_NEWLINE + ALICE;
        uniqApplication.uniqFromStdin(false, false, true, input, FILE_NAME);
        String actual = String.valueOf(Files.readAllLines(Paths.get(FILE_NAME)));
        assertEquals(expected, actual);
    }

    @Test
    void uniqFromFiles_CountRepeatedAllRepeated_returnsOutputFile() throws Exception {
        InputStream input = new ByteArrayInputStream((HELLO_WORLD + STRING_NEWLINE +
                HELLO_WORLD + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB + STRING_NEWLINE +
                ALICE + STRING_NEWLINE +
                BOB).getBytes());
        String expected = "2 " + HELLO_WORLD + STRING_NEWLINE + "2 " + HELLO_WORLD + STRING_NEWLINE + "2 " + ALICE + STRING_NEWLINE + "2 " + ALICE;
        uniqApplication.uniqFromStdin(true, true, true, input, FILE_NAME);
        String actual = String.valueOf(Files.readAllLines(Paths.get(FILE_NAME)));
        assertEquals(expected, actual);
    }

    @Test
    void uniqFromFiles_NotCountNotRepeatedNotAllRepeated_returnsOutputFile() throws Exception {
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
                BOB;
        uniqApplication.uniqFromStdin(false, false, false, input, FILE_NAME);
        String actual = String.valueOf(Files.readAllLines(Paths.get(FILE_NAME)));
        assertEquals(expected, actual);
    }

}
