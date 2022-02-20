package sg.edu.nus.comp.cs4218.impl.app;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.exception.SortException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class SortApplicationTest {
    private static SortApplication sortApplication;

    @BeforeAll
    static void setUp() {
        sortApplication = new SortApplication();

        try {
            deleteDir(new File("file.txt"));
            Files.createFile(Paths.get("file.txt"));
        }
        catch( IOException ioe ) {
            System.err.println("error creating temporary test file " + ioe);
        }
    }


    @AfterAll
    static void tearDown() throws IOException {
        deleteDir(new File("file.txt"));
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
        FileWriter fw = new FileWriter( "file.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write( "10\n1\n2");
        bw.close();
        String output = sortApplication.sortFromFiles(true, false, false, new String[] {"file.txt"});
        assertEquals("1\n2\n10", output);
    }

    @Test
    void sortFromFiles_notFirstWordNumberReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        FileWriter fw = new FileWriter( "file.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write( "10\n1\n2");
        bw.close();
        String output = sortApplication.sortFromFiles(false, true, false, new String[] {"file.txt"});
        assertEquals("2\n10\n1", output);
    }

    @Test
    void sortFromFiles_notFirstWordNumberNotReverseOrderCaseIndependent_returnsLines() throws Exception {
        FileWriter fw = new FileWriter( "file.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write( "10\n1\n2");
        bw.close();
        String output = sortApplication.sortFromFiles(false, false, true, new String[] {"file.txt"});
        assertEquals("1\n10\n2", output);
    }

    @Test
    void sortFromFiles_firstWordNumberReverseOrderCaseIndependent_returnsLines() throws Exception {
        FileWriter fw = new FileWriter( "file.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write( "10\n1\n2");
        bw.close();
        String output = sortApplication.sortFromFiles(true, true, true, new String[] {"file.txt"});
        assertEquals("10\n2\n1", output);
    }

    @Test
    void sortFromFiles_notFirstWordNumberNotReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        FileWriter fw = new FileWriter( "file.txt");
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write( "10\n1\n2");
        bw.close();
        String output = sortApplication.sortFromFiles(false, false, false, new String[] {"file.txt"});
        assertEquals("1\n10\n2", output);
    }

    @Test
    void sortFromStdin_firstWordNumberNotReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("10\n1\n2".getBytes());
        String output = sortApplication.sortFromStdin(true, false, false, input);
        assertEquals("1\n2\n10", output);
    }

    @Test
    void sortFromStdin_notFirstWordNumberReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("10\n1\n2".getBytes());
        String output = sortApplication.sortFromStdin(false, true, false, input);
        assertEquals("2\n10\n1", output);
    }

    @Test
    void sortFromStdin_notFirstWordNumberNotReverseOrderCaseIndependent_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("10\n1\n2".getBytes());
        String output = sortApplication.sortFromStdin(false, false, true, input);
        assertEquals("1\n10\n2", output);
    }

    @Test
    void sortFromStdin_firstWordNumberReverseOrderCaseIndependent_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("10\n1\n2".getBytes());
        String output = sortApplication.sortFromStdin(true, true, true, input);
        assertEquals("10\n2\n1", output);
    }

    @Test
    void sortFromStdin_notFirstWordNumberNotReverseOrderNotCaseIndependent_returnsLines() throws Exception {
        InputStream input = new ByteArrayInputStream("10\n1\n2".getBytes());
        String output = sortApplication.sortFromStdin(false, false, false, input);
        assertEquals("1\n10\n2", output);
    }
    

}