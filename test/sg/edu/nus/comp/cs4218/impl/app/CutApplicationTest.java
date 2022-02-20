package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.CutException;

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
            bw2.write( "abcd\nefgh");
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
        String output = cutApplication.cutFromFiles(true, false, false, 1, 2, new int[]{0}, input, new String[] {"fileSingleLine.txt"});
        assertEquals("a\n", output);
    }

    @Test
    void cutFromFiles_byByteSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, false, 1, 2, new int[]{0}, input, new String[] {"fileSingleLine.txt"});
        assertEquals("a\n", output);
    }

    @Test
    void cutFromFiles_byCharCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(true, false, false, 1, 2, new int[]{0,2,3}, input, new String[] {"fileSingleLine.txt"});
        assertEquals("acd\n", output);
    }

    @Test
    void cutFromFiles_byByteCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, false, 1, 2, new int[]{0,2,3}, input, new String[] {"fileSingleLine.txt"});
        assertEquals("acd\n", output);
    }

    @Test
    void cutFromFiles_byCharUnsortedCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(true, false, false, 1, 2, new int[]{2,3,0}, input, new String[] {"fileSingleLine.txt"});
        assertEquals("acd\n", output);
    }

    @Test
    void cutFromFiles_byByteUnsortedCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, false, 0, 2, new int[]{2,3,0}, input, new String[] {"fileSingleLine.txt"});
        assertEquals("acd\n", output);
    }

    @Test
    void cutFromFiles_byCharDuplicateCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(true, false, false, 1, 2, new int[]{0,0,1}, input, new String[] {"fileSingleLine.txt"});
        assertEquals("ab\n", output);
    }

    @Test
    void cutFromFiles_byByteDuplicateCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, false, 1, 2, new int[]{0,0,1}, input, new String[] {"fileSingleLine.txt"});
        assertEquals("ab\n", output);
    }

    @Test
    void cutFromFiles_byCharRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(true, false, false, 1, 2, new int[]{0,1,2}, input, new String[] {"fileSingleLine.txt"});
        assertEquals("abc\n", output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, false, 1, 2, new int[]{0,1,2}, input, new String[] {"fileSingleLine.txt"});
        assertEquals("abc\n", output);
    }

    @Test
    void cutFromFiles_byCharSingleIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(true, false, false, 1, 2, new int[]{0}, input, new String[] {"fileMultipleLines.txt"});
        assertEquals("a\ne\n", output);
    }

    @Test
    void cutFromFiles_byByteSingleIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, false, 1, 2, new int[]{0}, input, new String[] {"fileMultipleLines.txt"});
        assertEquals("a\ne\n", output);
    }

    @Test
    void cutFromFiles_byCharCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(true, false, false, 1, 2, new int[]{0,2,3}, input, new String[] {"fileMultipleLines.txt"});
        assertEquals("acd\negh\n", output);
    }

    @Test
    void cutFromFiles_byByteCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, false, 1, 2, new int[]{0,2,3}, input, new String[] {"fileMultipleLines.txt"});
        assertEquals("acd\negh\n", output);
    }

    @Test
    void cutFromFiles_byCharUnsortedCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(true, false, false, 1, 2, new int[]{2,3,0}, input, new String[] {"fileMultipleLines.txt"});
        assertEquals("acd\negh\n", output);
    }

    @Test
    void cutFromFiles_byByteUnsortedCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, false, 1, 2, new int[]{2,3,0}, input, new String[] {"fileMultipleLines.txt"});
        assertEquals("acd\negh\n", output);
    }

    @Test
    void cutFromFiles_byCharDuplicateCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(true, false, false, 1, 2, new int[]{0,0,1}, input, new String[] {"fileMultipleLines.txt"});
        assertEquals("ab\nef\n", output);
    }

    @Test
    void cutFromFiles_byByteDuplicateCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, false, 0, 1, new int[]{0,0,1}, input, new String[] {"fileMultipleLines.txt"});
        assertEquals("ab\nef\n", output);
    }

    @Test
    void cutFromFiles_byCharRangeIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(true, false, true, 0, 2, new int[]{0,1,2}, input, new String[] {"fileMultipleLines.txt"});
        assertEquals("abc\nefg\n", output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, new int[]{0,1,2}, input, new String[] {"fileMultipleLines.txt"});
        assertEquals("abc\nefg\n", output);
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleFiles_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("".getBytes());
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, new int[]{0,1,2}, input, new String[] {"fileSingleLine.txt", "fileMultipleLines.txt"});
        assertEquals(output, "abc\nabc\nefg\n");
    }

    @Test
    void cutFromFiles_byByteRangeIndexMultipleFilesAndStdin_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("z\nyy\nx\nwww".getBytes());
        String output = cutApplication.cutFromFiles(false, true, true, 0, 2, new int[]{0,1,2}, input, new String[] {"fileSingleLine.txt", "-", "fileMultipleLines.txt"});
        assertEquals(output, "abc\nz\nyy\nx\nwww\nabc\nefg\n");
    }


    @Test
    void cutFromStdin_byCharSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, new int[]{0}, input);
        assertEquals("a\n", output);
    }

    @Test
    void cutFromStdin_byByteSingleIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 1, 2, new int[]{0}, input);
        assertEquals("a\n", output);
    }

    @Test
    void cutFromStdin_byCharCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, new int[]{0,2,3}, input);
        assertEquals("acd\n", output);
    }

    @Test
    void cutFromStdin_byByteCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 1, 2, new int[]{0,2,3}, input);
        assertEquals("acd\n", output);
    }

    @Test
    void cutFromStdin_byCharUnsortedCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, new int[]{2,3,0}, input);
        assertEquals("acd\n", output);
    }

    @Test
    void cutFromStdin_byByteUnsortedCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 1, 2, new int[]{2,3,0}, input);
        assertEquals("acd\n", output);
    }

    @Test
    void cutFromStdin_byCharDuplicateCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, new int[]{0,0,1}, input);
        assertEquals("ab\n", output);
    }

    @Test
    void cutFromStdin_byByteDuplicateCommaSeparatedIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 1, 2, new int[]{0,0,1}, input);
        assertEquals("ab\n", output);
    }

    @Test
    void cutFromStdin_byCharRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, new int[]{0,1,2}, input);
        assertEquals("abc\n", output);
    }

    @Test
    void cutFromStdin_byByteRangeIndexSingleLine_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd".getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 1, 2, new int[]{0,1,2}, input);
        assertEquals("abc\n", output);
    }

    @Test
    void cutFromStdin_byCharSingleIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd\nefgh".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, new int[]{0}, input);
        assertEquals("a\ne\n", output);
    }

    @Test
    void cutFromStdin_byByteSingleIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd\nefgh".getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 1, 2, new int[]{0}, input);
        assertEquals("a\ne\n", output);

    }

    @Test
    void cutFromStdin_byCharCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd\nefgh".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, new int[]{0,2,3}, input);
        assertEquals("acd\negh\n", output);
    }

    @Test
    void cutFromStdin_byByteCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd\nefgh".getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 1, 2, new int[]{0,2,3}, input);
        assertEquals("acd\negh\n", output);
    }

    @Test
    void cutFromStdin_byCharUnsortedCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd\nefgh".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, new int[]{2,3,0}, input);
        assertEquals("acd\negh\n", output);
    }

    @Test
    void cutFromStdin_byByteUnsortedCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd\nefgh".getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 0, 1, new int[]{2,3,0}, input);
        assertEquals("acd\negh\n", output);

    }

    @Test
    void cutFromStdin_byCharDuplicateCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd\nefgh".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 0, 1, new int[]{0,0,1}, input);
        assertEquals("ab\nef\n", output);
    }

    @Test
    void cutFromStdin_byByteDuplicateCommaSeparatedIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd\nefgh".getBytes());
        String output = cutApplication.cutFromStdin(false, true, false, 0, 1, new int[]{0,0,1}, input);
        assertEquals("ab\nef\n", output);

    }

    @Test
    void cutFromStdin_byCharRangeIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd\nefgh".getBytes());
        String output = cutApplication.cutFromStdin(true, false, true, 0, 2, new int[]{0,1,2}, input);
        assertEquals("abc\nefg\n", output);
    }

    @Test
    void cutFromStdin_byByteRangeIndexMultipleLines_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("abcd\nefgh".getBytes());
        String output = cutApplication.cutFromStdin(false, true, true, 0, 2, new int[]{0,1,2}, input);
        assertEquals("abc\nefg\n", output);
    }

    @Test
    void cutFromStdin_EmptyString_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("\n".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, new int[]{0}, input);
        assertEquals("\n", output);
    }

    @Test
    void cutFromStdin_MultipleEmptyString_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("\n\n".getBytes());
        String output = cutApplication.cutFromStdin(true, false, false, 1, 2, new int[]{0}, input);
        assertEquals("\n\n", output);
    }

}