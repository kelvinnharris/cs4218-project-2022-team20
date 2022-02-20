package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.impl.app.args.UniqApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UniqApplicationTest {
    private UniqApplication uniqApplication;
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();


    @BeforeEach
    public void setUpEach() {
        uniqApplication = new UniqApplication();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    void uniqFromStdin_countNotRepeatedNotAllRepeated_returnsLinesInStdout() throws Exception {
        InputStream input = new ByteArrayInputStream(("Hello World\n" +
                "Hello World\n" +
                "Alice\n" +
                "Alice\n" +
                "Bob\n +" +
                "Alice\n +" +
                "Bob").getBytes());
        String expected = "2 Hello World\n" +
                "2 Alice\n" +
                "1 Bob\n" +
                "1 Alice\n" +
                "1 Bob";
        uniqApplication.uniqFromStdin(true, false, false, input, null);
        assertEquals(expected, outputStreamCaptor.toString());
    }

    @Test
    void uniqFromStdin_notCountRepeatedNotAllRepeated_returnsLinesInStdout() throws Exception {
        InputStream input = new ByteArrayInputStream(("Hello World\n" +
                "Hello World\n" +
                "Alice\n" +
                "Alice\n" +
                "Bob\n +" +
                "Alice\n +" +
                "Bob").getBytes());
        String expected = "Hello World\nAlice";
        uniqApplication.uniqFromStdin(false, true, false, input, null);
        assertEquals(expected, outputStreamCaptor.toString());
    }

    @Test
    void uniqFromStdin_notCountNotRepeatedAllRepeated_returnsLinesInStdout() throws Exception {
        InputStream input = new ByteArrayInputStream(("Hello World\n" +
                "Hello World\n" +
                "Alice\n" +
                "Alice\n" +
                "Bob\n +" +
                "Alice\n +" +
                "Bob").getBytes());
        String expected = "Hello World\nHello World\nAlice\nAlice";
        uniqApplication.uniqFromStdin(false, false, true, input, null);
        assertEquals(expected, outputStreamCaptor.toString());
    }

    @Test
    void uniqFromStdin_CountRepeatedAllRepeated_returnsLinesInStdout() throws Exception {
        InputStream input = new ByteArrayInputStream(("Hello World\n" +
                "Hello World\n" +
                "Alice\n" +
                "Alice\n" +
                "Bob\n +" +
                "Alice\n +" +
                "Bob").getBytes());
        String expected = "2 Hello World\n2 Hello World\n2 Alice\n2 Alice";
        uniqApplication.uniqFromStdin(true, true, true, input, null);
        assertEquals(expected, outputStreamCaptor.toString());
    }

    @Test
    void uniqFromStdin_NotCountNotRepeatedNotAllRepeated_returnsLinesInStdout() throws Exception {
        InputStream input = new ByteArrayInputStream(("Hello World\n" +
                "Hello World\n" +
                "Alice\n" +
                "Alice\n" +
                "Bob\n +" +
                "Alice\n +" +
                "Bob").getBytes());
        String expected = "Hello World\n" +
                "Alice\n" +
                "Bob\n" +
                "Alice\n" +
                "Bob";
        uniqApplication.uniqFromStdin(false, false, false, input, null);
        assertEquals(expected, outputStreamCaptor.toString());
    }

    @Test
    void uniqFromFiles_countNotRepeatedNotAllRepeated_returnsOutputFile() throws Exception {
        InputStream input = new ByteArrayInputStream(("Hello World\n" +
                "Hello World\n" +
                "Alice\n" +
                "Alice\n" +
                "Bob\n +" +
                "Alice\n +" +
                "Bob").getBytes());
        String expected = "2 Hello World\n" +
                "2 Alice\n" +
                "1 Bob\n" +
                "1 Alice\n" +
                "1 Bob";
        uniqApplication.uniqFromStdin(true, false, false, input, "temp.txt");
        String actual = String.valueOf(Files.readAllLines(Paths.get("temp.txt")));
        assertEquals(expected, actual);    }

    @Test
    void uniqFromFiles_notCountRepeatedNotAllRepeated_returnsOutputFile() throws Exception {
        InputStream input = new ByteArrayInputStream(("Hello World\n" +
                "Hello World\n" +
                "Alice\n" +
                "Alice\n" +
                "Bob\n +" +
                "Alice\n +" +
                "Bob").getBytes());
        String expected = "Hello World\nAlice";
        uniqApplication.uniqFromStdin(false, true, false, input, "temp.txt");
        String actual = String.valueOf(Files.readAllLines(Paths.get("temp.txt")));
        assertEquals(expected, actual);    }

    @Test
    void uniqFromFiles_notCountNotRepeatedAllRepeated_returnsOutputFile() throws Exception {
        InputStream input = new ByteArrayInputStream(("Hello World\n" +
                "Hello World\n" +
                "Alice\n" +
                "Alice\n" +
                "Bob\n +" +
                "Alice\n +" +
                "Bob").getBytes());
        String expected = "Hello World\nHello World\nAlice\nAlice";
        uniqApplication.uniqFromStdin(false, false, true, input, "temp.txt");
        String actual = String.valueOf(Files.readAllLines(Paths.get("temp.txt")));
        assertEquals(expected, actual);    }

    @Test
    void uniqFromFiles_CountRepeatedAllRepeated_returnsOutputFile() throws Exception {
        InputStream input = new ByteArrayInputStream(("Hello World\n" +
                "Hello World\n" +
                "Alice\n" +
                "Alice\n" +
                "Bob\n +" +
                "Alice\n +" +
                "Bob").getBytes());
        String expected = "2 Hello World\n2 Hello World\n2 Alice\n2 Alice";
        uniqApplication.uniqFromStdin(true, true, true, input, "temp.txt");
        String actual = String.valueOf(Files.readAllLines(Paths.get("temp.txt")));
        assertEquals(expected, actual);
    }

    @Test
    void uniqFromFiles_NotCountNotRepeatedNotAllRepeated_returnsOutputFile() throws Exception {
        InputStream input = new ByteArrayInputStream(("Hello World\n" +
                "Hello World\n" +
                "Alice\n" +
                "Alice\n" +
                "Bob\n +" +
                "Alice\n +" +
                "Bob").getBytes());
        String expected = "Hello World\n" +
                "Alice\n" +
                "Bob\n" +
                "Alice\n" +
                "Bob";
        uniqApplication.uniqFromStdin(false, false, false, input, "temp.txt");
        String actual = String.valueOf(Files.readAllLines(Paths.get("temp.txt")));
        assertEquals(expected, actual);
    }

}
