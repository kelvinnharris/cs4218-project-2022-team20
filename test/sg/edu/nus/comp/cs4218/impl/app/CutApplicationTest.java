package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class CutApplicationTest {
    private CutApplication cutApplication;

    @BeforeEach
    public void setUpEach() {
        cutApplication = new CutApplication();
    }

    @BeforeAll
    static void setUp() {
        try {
            deleteDir(new File("fileSingleLine.txt"));
            deleteDir(new File("fileMultipleLines.txt"));
            Files.createFile(Paths.get("fileSingleLine.txt"));
            Files.createFile(Paths.get("fileMultipleLines.txt"));
            FileWriter fw1 = new FileWriter( "fileSingleLine.txt");
            BufferedWriter bw1 = new BufferedWriter(fw1);
            bw1.write( "abcd");
            bw1.close();
            FileWriter fw2 = new FileWriter( "fileMultipleLines.txt");
            BufferedWriter bw2 = new BufferedWriter(fw2);
            bw2.write( "abcd" + STRING_NEWLINE + "efgh");
            bw2.close();
        }
        catch( IOException ioe ) {
            System.err.println("error creating temporary test file " + ioe);
        }
    }


    @AfterAll
    static void tearDown() throws IOException {
        deleteDir(new File("fileSingleLine.txt"));
        deleteDir(new File("fileMultipleLines.txt"));
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
        String output = cutApplication.cutFromFiles(true, false, false, 0, 1, new String[] {"fileSingleLine.txt"});
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, false, 0, 1, new String[] {"fileSingleLine.txt"});
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byCharRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(true, false, true, 0, 2, new String[] {"fileSingleLine.txt"});
        assertEquals("abc" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, new String[] {"fileSingleLine.txt"});
        assertEquals("abc" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byCharSingleIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(true, false, false, 0, 1, new String[] {"fileMultipleLines.txt"});
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteSingleIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, false, 0, 1, new String[] {"fileMultipleLines.txt"});
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byCharRangeIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(true, false, true, 0, 2, new String[] {"fileMultipleLines.txt"});
        assertEquals("abc" + STRING_NEWLINE + "efg" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, new String[] {"fileMultipleLines.txt"});
        assertEquals("abc" + STRING_NEWLINE + "efg" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleFiles_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, new String[] {"fileSingleLine.txt", "fileMultipleLines.txt"});
        assertEquals(output, "abc" + STRING_NEWLINE + "abc" + STRING_NEWLINE + "efg" + STRING_NEWLINE);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleFilesAndStdin_returnsLines() throws Exception {
        String inputString = "z" + STRING_NEWLINE + "yy" + STRING_NEWLINE + "x" + STRING_NEWLINE + "www";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());
        cutApplication.stdin = input;
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, new String[] {"fileSingleLine.txt", "-", "fileMultipleLines.txt"});
        assertEquals(output, "abc" + STRING_NEWLINE + "z" + STRING_NEWLINE + "yy" + STRING_NEWLINE + "x" + STRING_NEWLINE + "www" + STRING_NEWLINE + "abc" + STRING_NEWLINE + "efg" + STRING_NEWLINE);
    }


    @Test
    void cutFromStdin_byCharSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 0, 1, input);
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 0, 1, input);
        assertEquals("a" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byCharRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(true, false, true, 0, 2, input);
        assertEquals("abc" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(false, true, true, 0, 2, input);
        assertEquals("abc" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byCharSingleIndexMultipleLines_returnsLines() throws Exception {
        String inputString = "abcd" + STRING_NEWLINE + "efgh";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());          String output = cutApplication.cutFromStdin(true, false, false, 0, 1, input);
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteSingleIndexMultipleLines_returnsLines() throws Exception {
        String inputString = "abcd" + STRING_NEWLINE + "efgh";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());          String output = cutApplication.cutFromStdin(false, true, false, 0, 1, input);
        assertEquals("a" + STRING_NEWLINE + "e" + STRING_NEWLINE, output);

    }

    @Test
    void cutFromStdin_byCharRangeIndexMultipleLines_returnsLines() throws Exception {
        String inputString = "abcd" + STRING_NEWLINE + "efgh";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());          String output = cutApplication.cutFromStdin(true, false, true, 0, 2, input);
        assertEquals("abc" + STRING_NEWLINE + "efg" + STRING_NEWLINE, output);
    }

    @Test
    void cutFromStdin_byByteRangeIndexMultipleLines_returnsLines() throws Exception {
        String inputString = "abcd" + STRING_NEWLINE + "efgh";
        InputStream input = new ByteArrayInputStream(inputString.getBytes());        String output = cutApplication.cutFromStdin(false, true, true, 0, 2, input);
        assertEquals("abc" + STRING_NEWLINE + "efg" + STRING_NEWLINE, output);
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

}