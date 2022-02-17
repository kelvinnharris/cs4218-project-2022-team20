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
    void testLs_emptyPath_shouldReturnFileNotFound() throws LsException {
        String lsOutput = lsApplication.listFolderContent(false, false, false, "");
        String expectedOutput = "ls: cannot access '': No such file or directory";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void testLs_FullPath_shouldReturnAllFilesAndDirectories() throws LsException {
        String lsOutput = lsApplication.listFolderContent(false, false, false, TEST_PATH);
        String expectedOutput = "file4.xml\nfolder1\nfolder3";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void testLs_PathNoArg_shouldReturnAllFilesAndDirectories() throws LsException {
        Environment.currentDirectory = TEST_PATH;
        String lsOutput = lsApplication.listFolderContent(false, false, false, ".");
        String expectedOutput = "file4.xml\nfolder1\nfolder3";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void testLs_RelativePath_shouldReturnAllFilesAndDirectories() throws LsException {
        String path = "./" + TEST_FOLDER_NAME + "folder3/../folder1/.";
        String lsOutput = lsApplication.listFolderContent(false, false, false, path);
        String expectedOutput = "file1.txt\nfile2.iml\nfolder2";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void testLs_invalidPath_shouldReturnInvalidPathStringError() throws LsException {
        String path = TEST_FOLDER_NAME + "invalidFile.txt";
        String lsOutput = lsApplication.listFolderContent(false, false, false,  path);
        String expectedOutput = "ls: cannot access 'tmpLsTestFolder/invalidFile.txt': No such file or directory";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void testLs_validMultiplePath_shouldReturnValidPathString() throws LsException {
        String validPath1 = TEST_FOLDER_NAME + "folder1";
        String validPath2 = TEST_FOLDER_NAME + "folder1/file1.txt";
        String validPath3 = TEST_FOLDER_NAME + "folder3/folder4";
        String lsOutput = lsApplication.listFolderContent(false, false, false,  validPath1, validPath2, validPath3);
        String expectedOutput = "tmpLsTestFolder/folder1:\nfile1.txt\nfile2.iml\nfolder2\n\n" +
                "tmpLsTestFolder/folder1/file1.txt\n\n" +
                "tmpLsTestFolder/folder3/folder4:\nfile3.txt";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void testLs_validInvalidMultiplePath_shouldReturnValidInvalidPathString() throws LsException {
        String validPath1 = TEST_FOLDER_NAME + "folder5";
        String validPath2 = TEST_FOLDER_NAME + "folder1/folder2/hello.txt";
        String validPath3 = TEST_FOLDER_NAME + "folder3/folder4/file3.txt";
        String lsOutput = lsApplication.listFolderContent(false, false, false,  validPath1, validPath2, validPath3);
        String expectedOutput = "ls: cannot access 'tmpLsTestFolder/folder5': No such file or directory\n" +
                "ls: cannot access 'tmpLsTestFolder/folder1/folder2/hello.txt': No such file or directory\n" +
                "tmpLsTestFolder/folder3/folder4/file3.txt";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void testLs_fileWithLongPath_shouldReturnChosenFiles() throws LsException {
        String validPath = TEST_FOLDER_NAME + "folder3/folder4/file3.txt";
        String lsOutput = lsApplication.listFolderContent(false, false, false, validPath);
        String expectedOutput = "tmpLsTestFolder/folder3/folder4/file3.txt";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void testLs_fileOnCurrentDirectory_shouldReturnChosenFiles() throws LsException {
        Environment.currentDirectory = TEST_PATH;
        String lsOutput = lsApplication.listFolderContent(false, false, false, "file4.xml");
        String expectedOutput = "file4.xml";
        assertEquals(lsOutput, expectedOutput);
    }

    @Test
    void testLs_folderOnly_shouldReturnOnlyDirectories() throws LsException {
        String isFolderOnlyOutput = lsApplication.listFolderContent(true, false, false, TEST_PATH);
        String expectedOutput = "folder1\nfolder3";
        assertEquals(isFolderOnlyOutput, expectedOutput);
    }

    @Test
    void testLs_recursiveOnly_shouldReturnFilesAndDirectoriesRecursively() throws LsException {
        String isFolderOnlyOutput = lsApplication.listFolderContent(false, true, false, TEST_PATH);
        String expectedOutput = "tmpLsTestFolder:\nfile4.xml\nfolder1\nfolder3\n\n" +
                "tmpLsTestFolder/folder1:\nfile1.txt\nfile2.iml\nfolder2\n\n" +
                "tmpLsTestFolder/folder1/folder2:\n\n" +
                "tmpLsTestFolder/folder3:\nfolder4\n\n" +
                "tmpLsTestFolder/folder3/folder4:\nfile3.txt";
        assertEquals(isFolderOnlyOutput, expectedOutput);
    }

    @Test
    void testLs_recursiveSort_shouldSortAndReturnFilesAndDirectoriesRecursively() throws LsException {
        String isFolderOnlyOutput = lsApplication.listFolderContent(false, true, true, TEST_PATH);
        String expectedOutput = "tmpLsTestFolder:\nfolder1\nfolder3\nfile4.xml\n\n" +
                "tmpLsTestFolder/folder1:\nfolder2\nfile2.iml\nfile1.txt\n\n" +
                "tmpLsTestFolder/folder1/folder2:\n\n" +
                "tmpLsTestFolder/folder3:\nfolder4\n\n" +
                "tmpLsTestFolder/folder3/folder4:\nfile3.txt";
        assertEquals(isFolderOnlyOutput, expectedOutput);
    }

    @Test
    void testLs_passNullStdin_shouldPassed() {
        String[] emptyArgs = new String[]{};
        assertDoesNotThrow(() -> lsApplication.run(emptyArgs, null, System.out));
    }

    @Test
    void testLs_passNullArgs_shouldThrowLsException() {
        assertThrows(LsException.class, () -> lsApplication.run(null, System.in, System.out));
    }

    @Test
    void testLs_passValidArgs_shouldThrowLsException() {
        String[] emptyArgs = new String[]{ TEST_PATH, "-dRX" };
        assertDoesNotThrow(() -> lsApplication.run(emptyArgs, System.in, System.out));
    }

    @Test
    void testLs_passInvalidArgs_shouldThrowLsException() {
        String[] emptyArgs = new String[]{ TEST_PATH, "-abc" };
        assertThrows(LsException.class, () -> lsApplication.run(emptyArgs, System.in, System.out));
    }

    @Test
    void testLs_passNullStdout_shouldThrowLsException() {
        String[] emptyArgs = new String[]{};
        assertThrows(LsException.class, () -> lsApplication.run(emptyArgs, System.in, null));
    }
}