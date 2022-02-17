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
    private static final String TEST_FOLDER_NAME = "tmpCdTestFolder/";
    private static final String TEST_PATH = ROOT_PATH + "/" + TEST_FOLDER_NAME;


    @BeforeAll
    static void setUp() throws IOException {
        cdApplication = new CdApplication();
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + "folder1/folder2"));
        Files.createFile(Paths.get(TEST_PATH + "folder1/file1.txt"));
        Files.createFile(Paths.get(TEST_PATH + "folder1/folder2/file2.txt"));
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
    void testCdAbsoluteFolderShouldChangeToValidFolder() throws CdException {
        cdApplication.changeToDirectory(TEST_PATH);
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testCdSamePathShouldRemainTheSameDirectory() throws CdException {
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        cdApplication.changeToDirectory(".");
        Path givenPath = Paths.get(Environment.currentDirectory).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testCdValidPrevPathShouldChangeToPrevDirectory() throws CdException {
        Environment.currentDirectory = TEST_PATH;
        cdApplication.changeToDirectory("..");
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(ROOT_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testCdRelativePathShouldChangeToValidFolder() throws CdException {
        String path = TEST_FOLDER_NAME + "folder1/./folder2/../../.";
        cdApplication.changeToDirectory(path);
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH).normalize();
        assertEquals(currentPath, givenPath);
    }

    @Test
    void testCdValidFileShouldReturnNotADirectoryError() throws CdException {
        String path = TEST_PATH + "file3.xml";
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory(path));
    }

    @Test
    void testCdInvalidPathShouldReturnNoSuchDirectoryError() throws CdException {
        String path = TEST_PATH + "folder1/invalidFolder";
        assertThrows(CdException.class, () -> cdApplication.changeToDirectory(path));
    }

    @Test
    void testCdNullArgsShouldReturnCdError() throws CdException {
        String path = TEST_PATH + "file3.xml";
        assertThrows(CdException.class, () -> cdApplication.run(null, System.in, System.out));
    }

    @Test
    void testCdNoArgsShouldReturnCdError() throws CdException {
        String[] emptyArgs = new String[]{ "" };
        assertThrows(CdException.class, () -> cdApplication.run(emptyArgs, System.in, System.out));
    }

    @Test
    void testCdMultipleArgsShouldTakeFirstArgumentOnly() throws CdException {
        String[] args = new String[]{ TEST_PATH + "folder1/folder2", TEST_PATH + "folder1", "." };
        cdApplication.run(args, System.in, System.out);
        Path currentPath = Paths.get(Environment.currentDirectory).normalize();
        Path givenPath = Paths.get(TEST_PATH + "folder1/folder2").normalize();
        assertEquals(currentPath, givenPath);
    }
}