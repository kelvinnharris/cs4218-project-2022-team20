package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CdException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.CD_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

class CdApplicationTest {
    /* before each file path:
        > folder1
           > file1.txt
           > folder2
              > file2.txt
        > file3.xml
     */

    private static CdApplication cdApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = CD_FOLDER + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FOLDER_1 = "folder1";
    private static final String FOLDER_2 = "folder2";
    private static final String FILE_1 = "file1.txt";
    private static final String FILE_2 = "file2.txt";
    private static final String FILE_3 = "file3.xml";

    @BeforeAll
    static void setUp() throws IOException {
        cdApplication = new CdApplication();
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_1));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2 + CHAR_FILE_SEP + FILE_2));
        Files.createFile(Paths.get(TEST_PATH + FILE_3));
    }

    @BeforeEach
    void setUpEach() {
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() throws IOException {
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void changeToDirectory_absoluteFolder_shouldChangeToValidFolder() throws CdException {
        cdApplication.changeToDirectory(TEST_PATH);
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void changeToDirectory_samePath_shouldRemainTheSameDirectory() throws CdException {
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        cdApplication.changeToDirectory(".");
        Path givenPath = Paths.get(Environment.currentDirectory).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void changeToDirectory_validPrevPath_shouldChangeToPrevDirectory() throws CdException {
        Environment.currentDirectory = TEST_PATH;
        cdApplication.changeToDirectory("..");
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(ROOT_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void changeToDirectory_relativePath_shouldChangeToValidFolder() throws CdException {
        String path = TEST_FOLDER_NAME + FOLDER_1 + CHAR_FILE_SEP + "." + CHAR_FILE_SEP + FOLDER_2 + CHAR_FILE_SEP + ".." + CHAR_FILE_SEP + ".." + CHAR_FILE_SEP + ".";
        cdApplication.changeToDirectory(path);
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void changeToDirectory_validFile_shouldReturnNotADirectoryError() {
        String path = TEST_PATH + FILE_3;
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory(path));
    }

    @Test
    void changeToDirectory_invalidPath_shouldReturnNoSuchDirectoryError() {
        String path = TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + "invalidFolder";
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory(path));
    }

    @Test
    void run_nullArgs_shouldReturnCdError() {
        String path = TEST_PATH + FILE_3;
        assertThrows(CdException.class, () -> cdApplication.run(null, System.in, System.out));
    }

    @Test
    void run_noArgs_shouldReturnCdError() {
        String[] emptyArgs = new String[]{""};
        assertThrows(CdException.class, () -> cdApplication.run(emptyArgs, System.in, System.out));
    }

    @Test
    void run_multipleArgs_shouldReturnCdError() {
        String[] multipleArgs = new String[]{ FOLDER_1, FOLDER_2 };
        assertThrows(CdException.class, () -> cdApplication.run(multipleArgs, System.in, System.out));
    }
}