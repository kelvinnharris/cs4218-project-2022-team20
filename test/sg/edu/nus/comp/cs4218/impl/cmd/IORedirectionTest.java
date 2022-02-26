package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.app.TestUtils;
import sg.edu.nus.comp.cs4218.impl.app.WcApplication;
import sg.edu.nus.comp.cs4218.impl.util.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_FILE_NOT_FOUND;
import static sg.edu.nus.comp.cs4218.impl.util.ErrorConstants.ERR_SYNTAX;

class IORedirectionTest {

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpRedirTestFolder" + StringUtils.CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + StringUtils.CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String FILE_NAME_1 = "test1.txt";
    private static final String FILE_PATH_1 = TEST_PATH + FILE_NAME_1;
    private static final String FILE_NAME_2 = "test2.txt";
    private static final String FILE_PATH_2 = TEST_PATH + FILE_NAME_2;

    private static final String NONEXISTENTFILE = "IORedir.txt";

    private IORedirectionHandler ioRedirectionHandler; // NOPMD
    List<String> argsList;
    private ArgumentResolver argumentResolver;
    private InputStream inputStream;
    private OutputStream outputStream;

    @BeforeAll
    static void setUp() throws IOException {
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        TestUtils.createFile(FILE_PATH_1, "This is WC Test file 1" + StringUtils.STRING_NEWLINE);
    }

    @BeforeEach
    void setUpEach() throws IOException {
        argsList = new ArrayList<>();
        argumentResolver = new ArgumentResolver();
        inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        outputStream = new OutputStream() {
            @Override
            public void write(int num) throws IOException { // NOPMD
            }
        };
    }

    @AfterAll
    static void tearDown() {
        TestUtils.deleteDir(new File(FILE_PATH_1));
        TestUtils.deleteDir(new File(FILE_PATH_2));
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    @Test
    void testIORedirection_InputRedirectionWithMultipleArguments_shouldHaveSameInputStreamAsFile() throws Exception {
        argsList.addAll(Arrays.asList("wc", "-", FILE_PATH_1, "<", FILE_PATH_1));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        ioRedirectionHandler.extractRedirOptions();

        InputStream inStrmExpected = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        List<String> dataExpected = IOUtils.getLinesFromInputStream(inStrmExpected);
        IOUtils.closeInputStream(inStrmExpected);

        List<String> dataActual = IOUtils.getLinesFromInputStream(ioRedirectionHandler.getInputStream());

        assertIterableEquals(dataExpected, dataActual, "Input stream from both should have same content");

        List<String> noRedirExpected = Arrays.asList("wc", "-", FILE_PATH_1);
        List<String> noRedirActual = ioRedirectionHandler.getNoRedirArgsList();

        assertIterableEquals(noRedirExpected, noRedirActual, "List from both should be equal");

        System.out.println("Test redir output");
    }

    @Test
    void testIORedirection_InputRedirectionWithMultipleArguments_shouldHaveSameOutputStreamAsFile() throws Exception {
        argsList.addAll(Arrays.asList("wc", "-", FILE_PATH_1, ">", FILE_PATH_2));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        ioRedirectionHandler.extractRedirOptions();

        List<String> noRedirExpected = Arrays.asList("wc", "-", FILE_PATH_1);
        List<String> noRedirActual = ioRedirectionHandler.getNoRedirArgsList();

        assertIterableEquals(noRedirExpected, noRedirActual, "List from both should be equal");

        OutputStream outStrmExpected = ioRedirectionHandler.getOutputStream(); // NOPMD
        String sampleData = "This result is to be appended to filePath2";

        try {
            outStrmExpected.write(sampleData.getBytes());
            outStrmExpected.write(StringUtils.STRING_NEWLINE.getBytes());
        } catch (IOException e) {
            throw new IOException(ErrorConstants.ERR_WRITE_STREAM); // NOPMD
        }

        InputStream inStrmActual = IOUtils.openInputStream(FILE_PATH_2); // NOPMD
        List<String> dataActual = IOUtils.getLinesFromInputStream(inStrmActual);
        IOUtils.closeInputStream(inStrmActual);

        assertEquals(1, dataActual.size(), "Should only have one element");
        assertEquals(sampleData, dataActual.get(0), "Should have the same message");
    }

    @Test
    void testIORedirection_InputRedirection_shouldHaveSameInputStreamAsFile() throws Exception {
        argsList.addAll(Arrays.asList("wc", "<", FILE_PATH_1));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        ioRedirectionHandler.extractRedirOptions();

        InputStream inStrmExpected = IOUtils.openInputStream(FILE_PATH_1); // NOPMD
        List<String> dataExpected = IOUtils.getLinesFromInputStream(inStrmExpected);
        IOUtils.closeInputStream(inStrmExpected);

        List<String> dataActual = IOUtils.getLinesFromInputStream(ioRedirectionHandler.getInputStream());

        assertIterableEquals(dataExpected, dataActual, "Input stream from both should have same content");
    }

    @Test
    void testIORedirection_nonExistentInputFile_shouldThrowShellException() {
        argsList.addAll(Arrays.asList("wc", "<", NONEXISTENTFILE));

        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        assertThrows(ShellException.class, () -> ioRedirectionHandler.extractRedirOptions(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testIORedirection_nonValidOutputFile_shouldThrowFileNotFoundException() {
        argsList.addAll(Arrays.asList("wc", ">", TEST_FOLDER_NAME));

        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        assertThrows(FileNotFoundException.class, () -> ioRedirectionHandler.extractRedirOptions(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testIORedirection_nullArgsList_shouldThrowException() {
        ioRedirectionHandler = new IORedirectionHandler(null, inputStream, outputStream, argumentResolver);
        assertThrows(ShellException.class, () -> ioRedirectionHandler.extractRedirOptions(), ERR_SYNTAX);
    }
}
