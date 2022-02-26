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
    private static final String TEST_FOLDER_NAME = "tmpCdTestFolder" + CHAR_FILE_SEP + "";
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;


    @BeforeAll
    static void setUp() throws IOException {
        cdApplication = new CdApplication();
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + "folder1" + CHAR_FILE_SEP + "folder2"));
        Files.createFile(Paths.get(TEST_PATH + "folder1" + CHAR_FILE_SEP + "file1.txt"));
        Files.createFile(Paths.get(TEST_PATH + "folder1" + CHAR_FILE_SEP + "folder2" + CHAR_FILE_SEP + "file2.txt"));
        Files.createFile(Paths.get(TEST_PATH + "file3.xml"));
    }

    @BeforeEach
    void setUpEach() {
        Environment.currentDirectory = ROOT_PATH;
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
    void testCd_absoluteFolder_shouldChangeToValidFolder() throws CdException {
        cdApplication.changeToDirectory(TEST_PATH);
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testCd_samePath_shouldRemainTheSameDirectory() throws CdException {
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        cdApplication.changeToDirectory(".");
        Path givenPath = Paths.get(Environment.currentDirectory).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testCd_validPrevPath_shouldChangeToPrevDirectory() throws CdException {
        Environment.currentDirectory = TEST_PATH;
        cdApplication.changeToDirectory("..");
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(ROOT_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testCd_relativePath_shouldChangeToValidFolder() throws CdException {
        String path = TEST_FOLDER_NAME + "folder1" + CHAR_FILE_SEP + "." + CHAR_FILE_SEP + "folder2" + CHAR_FILE_SEP + ".." + CHAR_FILE_SEP + ".." + CHAR_FILE_SEP + ".";
        cdApplication.changeToDirectory(path);
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testCd_validFile_shouldReturnNotADirectoryError() throws CdException {
        String path = TEST_PATH + "file3.xml";
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory(path));
    }

    @Test
    void testCd_invalidPath_shouldReturnNoSuchDirectoryError() throws CdException {
        String path = TEST_PATH + "folder1" + CHAR_FILE_SEP + "invalidFolder";
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory(path));
    }

    @Test
    void testCd_nullArgs_shouldReturnCdError() throws CdException {
        String path = TEST_PATH + "file3.xml";
        assertThrows(CdException.class, () -> cdApplication.run(null, System.in, System.out));
    }

    @Test
    void testCd_noArgs_shouldReturnCdError() throws CdException {
        String[] emptyArgs = new String[]{ "" };
        assertThrows(CdException.class, () -> cdApplication.run(emptyArgs, System.in, System.out));
    }

    @Test
    void testCd_multipleArgs_shouldTakeFirstArgumentOnly() throws CdException {
        String[] args = new String[]{ TEST_PATH + "folder1" + CHAR_FILE_SEP + "folder2", TEST_PATH + "folder1", "." };
        cdApplication.run(args, System.in, System.out);
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH + "folder1" + CHAR_FILE_SEP + "folder2").normalize();
        assertEquals(currentPath, givenPath);
    }
}