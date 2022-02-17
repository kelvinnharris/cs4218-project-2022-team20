package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.LsException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LsApplicationTest {
    /* before each file path:
        > folder1
           > file1.txt
           > file2.iml
           > folder2
        > folder3
           > folder4
              > file3.txt
        > file4.xml
     */

    private static LsApplication lsApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpLsTestFolder/";
    private static final String TEST_PATH = ROOT_PATH + "/" + TEST_FOLDER_NAME;

    @BeforeAll
    static void setUp() throws IOException {
        lsApplication = new LsApplication();
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + "folder1/folder2"));
        Files.createDirectories(Paths.get(TEST_PATH + "folder3/folder4"));
        Files.createFile(Paths.get(TEST_PATH + "folder1/file1.txt"));
        Files.createFile(Paths.get(TEST_PATH + "folder1/file2.iml"));
        Files.createFile(Paths.get(TEST_PATH + "file4.xml"));
        Files.createFile(Paths.get(TEST_PATH + "folder3/folder4/file3.txt"));
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
    void lsTestEmptyPathShouldReturnFileNotFound() throws LsException {
        String lsOutput = lsApplication.listFolderContent(false, false, false, "");
        String expectedOutput = "ls: cannot access '': No such file or directory";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void lsTestFullPathShouldReturnAllFilesAndDirectories() throws LsException {
        String lsOutput = lsApplication.listFolderContent(false, false, false, TEST_PATH);
        String expectedOutput = "file4.xml\nfolder1\nfolder3";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void lsTestPathNoArgShouldReturnAllFilesAndDirectories() throws LsException {
        Environment.currentDirectory = TEST_PATH;
        String lsOutput = lsApplication.listFolderContent(false, false, false, ".");
        String expectedOutput = "file4.xml\nfolder1\nfolder3";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void lsTestRelativePathShouldReturnAllFilesAndDirectories() throws LsException {
        String path = "./" + TEST_FOLDER_NAME + "folder3/../folder1/.";
        String lsOutput = lsApplication.listFolderContent(false, false, false, path);
        String expectedOutput = "file1.txt\nfile2.iml\nfolder2";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void lsInvalidPathShouldReturnInvalidPathStringError() throws LsException {
        String path = TEST_FOLDER_NAME + "invalidFile.txt";
        String lsOutput = lsApplication.listFolderContent(false, false, false,  path);
        String expectedOutput = "ls: cannot access 'tmpTestFolder/invalidFile.txt': No such file or directory";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void lsValidMultiplePathShouldReturnValidPathString() throws LsException {
        String validPath1 = TEST_FOLDER_NAME + "folder1";
        String validPath2 = TEST_FOLDER_NAME + "folder1/file1.txt";
        String validPath3 = TEST_FOLDER_NAME + "folder3/folder4";
        String lsOutput = lsApplication.listFolderContent(false, false, false,  validPath1, validPath2, validPath3);
        String expectedOutput = "tmpTestFolder/folder1:\nfile1.txt\nfile2.iml\nfolder2\n\n" +
                "tmpTestFolder/folder1/file1.txt\n\n" +
                "tmpTestFolder/folder3/folder4:\nfile3.txt";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void lsValidInvalidMultiplePathShouldReturnValidInvalidPathString() throws LsException {
        String validPath1 = TEST_FOLDER_NAME + "folder5";
        String validPath2 = TEST_FOLDER_NAME + "folder1/folder2/hello.txt";
        String validPath3 = TEST_FOLDER_NAME + "folder3/folder4/file3.txt";
        String lsOutput = lsApplication.listFolderContent(false, false, false,  validPath1, validPath2, validPath3);
        String expectedOutput = "ls: cannot access 'tmpTestFolder/folder5': No such file or directory\n" +
                "ls: cannot access 'tmpTestFolder/folder1/folder2/hello.txt': No such file or directory\n" +
                "tmpTestFolder/folder3/folder4/file3.txt";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void lsFilesLongPathShouldReturnChosenFiles() throws LsException {
        String validPath = TEST_FOLDER_NAME + "folder3/folder4/file3.txt";
        String lsOutput = lsApplication.listFolderContent(false, false, false, validPath);
        String expectedOutput = "tmpTestFolder/folder3/folder4/file3.txt";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void lsFileCurrentDirectoryShouldReturnChosenFiles() throws LsException {
        Environment.currentDirectory = TEST_PATH;
        String lsOutput = lsApplication.listFolderContent(false, false, false, "file4.xml");
        String expectedOutput = "file4.xml";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void lsFolderOnlyShouldReturnOnlyDirectories() throws LsException {
        String isFolderOnlyOutput = lsApplication.listFolderContent(true, false, false, TEST_PATH);
        String expectedOutput = "folder1\nfolder3";
        assertEquals(isFolderOnlyOutput, expectedOutput);
    }

    @Test
    void lsRecursiveShouldReturnFilesAndDirectoriesRecursively() throws LsException {
        String isFolderOnlyOutput = lsApplication.listFolderContent(false, true, false, TEST_PATH);
        String expectedOutput = "tmpTestFolder:\nfile4.xml\nfolder1\nfolder3\n\n" +
                "tmpTestFolder/folder1:\nfile1.txt\nfile2.iml\nfolder2\n\n" +
                "tmpTestFolder/folder1/folder2:\n\n" +
                "tmpTestFolder/folder3:\nfolder4\n\n" +
                "tmpTestFolder/folder3/folder4:\nfile3.txt";
        assertEquals(isFolderOnlyOutput, expectedOutput);
    }

    @Test
    void lsRecursiveSortShouldSortAndReturnFilesAndDirectoriesRecursively() throws LsException {
        String isFolderOnlyOutput = lsApplication.listFolderContent(false, true, true, TEST_PATH);
        String expectedOutput = "tmpTestFolder:\nfolder1\nfolder3\nfile4.xml\n\n" +
                "tmpTestFolder/folder1:\nfolder2\nfile2.iml\nfile1.txt\n\n" +
                "tmpTestFolder/folder1/folder2:\n\n" +
                "tmpTestFolder/folder3:\nfolder4\n\n" +
                "tmpTestFolder/folder3/folder4:\nfile3.txt";
        assertEquals(isFolderOnlyOutput, expectedOutput);
    }

    @Test
    void passNullStdinShouldPassed() {
        String[] emptyArgs = new String[]{};
        assertDoesNotThrow(() -> lsApplication.run(emptyArgs, null, System.out));
    }

    @Test
    void passNullArgsShouldThrowLsException() {
        assertThrows(LsException.class, () -> lsApplication.run(null, System.in, System.out));
    }

    @Test
    void passValidArgsShouldThrowLsException() {
        String[] emptyArgs = new String[]{ TEST_PATH, "-dRX" };
        assertDoesNotThrow(() -> lsApplication.run(emptyArgs, System.in, System.out));
    }

    @Test
    void passInvalidArgsShouldThrowLsException() {
        String[] emptyArgs = new String[]{ TEST_PATH, "-abc" };
        assertThrows(LsException.class, () -> lsApplication.run(emptyArgs, System.in, System.out));
    }

    @Test
    void passNullStdoutShouldThrowLsException() {
        String[] emptyArgs = new String[]{};
        assertThrows(LsException.class, () -> lsApplication.run(emptyArgs, System.in, null));
    }
}