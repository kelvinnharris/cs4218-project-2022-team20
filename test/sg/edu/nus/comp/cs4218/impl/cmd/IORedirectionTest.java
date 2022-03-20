package sg.edu.nus.comp.cs4218.impl.cmd;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.ShellException;
import sg.edu.nus.comp.cs4218.impl.util.TestUtils;
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
    private static final String FILE_NAME_3 = "test3.txt";
    private static final String FILE_PATH_3 = TEST_PATH + FILE_NAME_3;

    private static final String NONEXISTENTFILE = "IORedir.txt";

    private static final String FILE_CONTENT_1 = "This is WC Test file 1";
    private static final String FILE_CONTENT_3 = "This file contains z";

    private static final String LIST_EQUAL_MSG = "List from both should be equal";
    private static final String CAT = "cat";
    private static final String STRING_WC = "wc";

    private IORedirectionHandler ioRedirectionHandler; //NOPMD - suppressed LongVariable - long variable to preserve meaningful variable naming
    List<String> argsList;
    private ArgumentResolver argumentResolver;
    private InputStream inputStream;
    private OutputStream outputStream;

    @BeforeAll
    static void setUp() throws IOException {
        Environment.currentDirectory = TEST_PATH;
        TestUtils.deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        TestUtils.createFile(FILE_PATH_1, FILE_CONTENT_1 + StringUtils.STRING_NEWLINE);
        TestUtils.createFile(FILE_PATH_3, FILE_CONTENT_3);
    }

    @BeforeEach
    void setUpEach() throws IOException {
        argsList = new ArrayList<>();
        argumentResolver = new ArgumentResolver();
        inputStream = System.in;
        outputStream = System.out;
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        TestUtils.deleteDir(new File(FILE_PATH_1));
        TestUtils.deleteDir(new File(FILE_PATH_2));
        TestUtils.deleteDir(new File(TEST_PATH));
    }

    @Test
    void testIoRedirectionExtractRedirOptions_NoInpuTredirection_shouldReturnCorrectNoRedirArgsList() throws Exception {
        argsList.addAll(Arrays.asList("paste", "abc", FILE_PATH_1, FILE_PATH_2, FILE_PATH_3));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        ioRedirectionHandler.extractRedirOptions();

        List<String> noRedirExpected = Arrays.asList("paste", "abc", FILE_PATH_1, FILE_PATH_2, FILE_PATH_3);
        List<String> noRedirActual = ioRedirectionHandler.getNoRedirArgsList();

        assertIterableEquals(noRedirExpected, noRedirActual, "Input stream from both should have same content");
    }

    @Test
    void testIORedirectionExtractRedirOptions_InputRedirectionWithMultipleArguments_shouldHaveSameInputStreamAsFile() throws Exception {
        argsList.addAll(Arrays.asList(STRING_WC, "-", FILE_PATH_1, "<", FILE_PATH_1));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        ioRedirectionHandler.extractRedirOptions();

        InputStream inStrmExpected = IOUtils.openInputStream(FILE_PATH_1);
        List<String> dataExpected;
        try {
            dataExpected = IOUtils.getLinesFromInputStream(inStrmExpected);
        } finally {
            inStrmExpected.close();
        }

        List<String> dataActual = IOUtils.getLinesFromInputStream(ioRedirectionHandler.getInputStream());

        assertIterableEquals(dataExpected, dataActual, "Input stream from both should have same content");

        List<String> noRedirExpected = Arrays.asList(STRING_WC, "-", FILE_PATH_1);
        List<String> noRedirActual = ioRedirectionHandler.getNoRedirArgsList();

        assertIterableEquals(noRedirExpected, noRedirActual, LIST_EQUAL_MSG);
    }

    @Test
    void testIORedirectionExtractRedirOptions_InputRedirectionWithMultipleArguments_shouldHaveCorrectNoRedirListAndSameOutputStreamAsFile() throws Exception {
        argsList.addAll(Arrays.asList(STRING_WC, "-", FILE_PATH_1, ">", FILE_PATH_2));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        ioRedirectionHandler.extractRedirOptions();

        List<String> noRedirExpected = Arrays.asList(STRING_WC, "-", FILE_PATH_1);
        List<String> noRedirActual = ioRedirectionHandler.getNoRedirArgsList();

        assertIterableEquals(noRedirExpected, noRedirActual, LIST_EQUAL_MSG);

        OutputStream outStrmExpected = ioRedirectionHandler.getOutputStream();
        String sampleData = "This result is to be appended to filePath2";

        try {
            outStrmExpected.write(sampleData.getBytes());
            outStrmExpected.write(StringUtils.STRING_NEWLINE.getBytes());
        } finally {
            outStrmExpected.close();
        }

        InputStream inStrmActual = IOUtils.openInputStream(FILE_PATH_2);
        List<String> dataActual;
        try {
            dataActual = IOUtils.getLinesFromInputStream(inStrmActual);
        } finally {
            inStrmActual.close();
        }

        assertEquals(1, dataActual.size(), "Should only have one element");
        assertEquals(sampleData, dataActual.get(0), "Should have the same message");
    }

    @Test
    void testIORedirectionExtractRedirOptions_InputRedirection_shouldHaveSameInputStreamAsFile() throws Exception {
        argsList.addAll(Arrays.asList(STRING_WC, "<", FILE_PATH_1));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        ioRedirectionHandler.extractRedirOptions();

        InputStream inStrmExpected = IOUtils.openInputStream(FILE_PATH_1);
        List<String> dataExpected;
        try {
            dataExpected = IOUtils.getLinesFromInputStream(inStrmExpected);
        } finally {
            inStrmExpected.close();
        }

        List<String> dataActual = IOUtils.getLinesFromInputStream(ioRedirectionHandler.getInputStream());

        assertIterableEquals(dataExpected, dataActual, "Input stream from both should have same content");
    }

    @Test
    void testIORedirectionExtractRedirOptions_InputAndOutputRedirection_shouldReturnCorrectNoRedirArgsList() throws Exception {
        argsList.addAll(Arrays.asList(STRING_WC, "-", "<", FILE_PATH_1, ">", FILE_PATH_2));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        ioRedirectionHandler.extractRedirOptions();

        List<String> noRedirExpected = Arrays.asList(STRING_WC, "-");
        List<String> noRedirActual = ioRedirectionHandler.getNoRedirArgsList();

        assertIterableEquals(noRedirExpected, noRedirActual, LIST_EQUAL_MSG);
    }

    @Test
    void testIORedirectionExtractRedirOptions_InputAndOutputRedirection_shouldHaveCorrectNoRedirArgsList() throws Exception {
        argsList.addAll(Arrays.asList(CAT, "<", FILE_PATH_1, ">", FILE_PATH_2));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        ioRedirectionHandler.extractRedirOptions();

        List<String> noRedirExpected = Arrays.asList(CAT);
        List<String> noRedirActual = ioRedirectionHandler.getNoRedirArgsList();

        assertIterableEquals(noRedirExpected, noRedirActual, LIST_EQUAL_MSG);
    }

    @Test
    void testIORedirectionExtractRedirOptions_InputAndOutputRedirectionReversedOrder_shouldHaveSameOutputStreamAsFile() throws Exception {
        argsList.addAll(Arrays.asList(CAT, "-", ">", FILE_PATH_2, "<", FILE_PATH_1));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        ioRedirectionHandler.extractRedirOptions();

        List<String> noRedirExpected = Arrays.asList(CAT, "-");
        List<String> noRedirActual = ioRedirectionHandler.getNoRedirArgsList();

        assertIterableEquals(noRedirExpected, noRedirActual, LIST_EQUAL_MSG);
    }

    @Test
    void testIORedirectionExtractRedirOptions_nonExistentInputFile_shouldThrowShellException() {
        argsList.addAll(Arrays.asList(STRING_WC, "<", NONEXISTENTFILE));

        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        assertThrows(ShellException.class, () -> ioRedirectionHandler.extractRedirOptions(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testIORedirectionExtractRedirOptions_nonValidOutputFile_shouldThrowShellNotFoundException() {
        argsList.addAll(Arrays.asList(STRING_WC, ">", TEST_PATH));

        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        assertThrows(ShellException.class, () -> ioRedirectionHandler.extractRedirOptions(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testIORedirectionExtractRedirOptions_nullArgsList_shouldThrowException() {
        ioRedirectionHandler = new IORedirectionHandler(null, inputStream, outputStream, argumentResolver);
        assertThrows(ShellException.class, () -> ioRedirectionHandler.extractRedirOptions(), ERR_SYNTAX);
    }

    @Test
    void testIORedirectionExtractRedirOptions_inputRedirWithInvalidFileWithValidOutputRedir_shouldThrowShellException() {
        argsList.addAll(Arrays.asList(STRING_WC, "<", NONEXISTENTFILE, ">", FILE_NAME_2));

        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);
        assertThrows(ShellException.class, () -> ioRedirectionHandler.extractRedirOptions(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testIORedirectionExtractRedirOptions_inputRedirWithValidFileWithInvalidFileOutputRedir_shouldThrowShellException() {
        argsList.addAll(Arrays.asList("grep", "B", "<", FILE_PATH_1, ">", TEST_PATH));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);

        assertThrows(ShellException.class, () -> ioRedirectionHandler.extractRedirOptions(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testIORedirectionExtractRedirOptions_doubleInputRedirection_shouldThrowShellException() throws Exception {
        argsList.addAll(Arrays.asList("grep", "z", "<", FILE_PATH_1, "<", FILE_PATH_3));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);

        assertThrows(ShellException.class, () -> ioRedirectionHandler.extractRedirOptions(), ERR_FILE_NOT_FOUND);
    }

    @Test
    void testIORedirectionExtractRedirOptions_doubleOutRedirection_shouldThrowShellException() throws Exception {
        argsList.addAll(Arrays.asList("paste", FILE_PATH_1, ">", FILE_PATH_2, ">", FILE_PATH_3 + "4"));
        ioRedirectionHandler = new IORedirectionHandler(argsList, inputStream, outputStream, argumentResolver);

        assertThrows(ShellException.class, () -> ioRedirectionHandler.extractRedirOptions(), ERR_FILE_NOT_FOUND);
    }
}
