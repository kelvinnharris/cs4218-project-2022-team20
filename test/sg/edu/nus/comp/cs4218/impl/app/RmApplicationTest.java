package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

class RmApplicationTest {
    private static RmApplication rmApplication;

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpRmTestFolder" + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;
    private static final String FOLDER_1 = "folder1";
    private static final String FOLDER_2 = "folder2";
    private static final String FOLDER_3 = "folder3";
    private static final String FOLDER_4 = "folder4";
    private static final String FOLDER_5 = "folder5";
    private static final String FOLDER_6 = "folder6";
    private static final String FOLDER_7 = "folder7";
    private static final String FOLDER_8 = "folder8";
    private static final String FOLDER_9 = "folder9";
    private static final String FILE_1 = "file1.txt";
    private static final String FILE_2 = "file2.iml";
    private static final String FILE_3 = "file3.txt";
    private static final String FILE_4 = "file4.xml";
    private static final String FILE_5 = "file5.txt";
    private static final String FILE_6 = "file6.txt";
    private static final String FILE_7 = "file7.txt";
    private static final String FILE_8 = "file8.txt";


    @BeforeAll
    static void setUp() throws IOException {
        rmApplication = new RmApplication();

        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FOLDER_2));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_5));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_6 + CHAR_FILE_SEP + FOLDER_7));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_8));
        Files.createDirectories(Paths.get(TEST_PATH + FOLDER_9));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_1));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_1 + CHAR_FILE_SEP + FILE_2));
        Files.createFile(Paths.get(TEST_PATH + FILE_4));
        Files.createFile(Paths.get(TEST_PATH + FILE_5));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_3 + CHAR_FILE_SEP + FOLDER_4 + CHAR_FILE_SEP + FILE_3));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_6 + CHAR_FILE_SEP + FOLDER_7 + CHAR_FILE_SEP + FILE_6));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_6 + CHAR_FILE_SEP + FOLDER_7 + CHAR_FILE_SEP + FILE_7));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_6 + CHAR_FILE_SEP + FOLDER_7 + CHAR_FILE_SEP + FILE_8));
        Files.createFile(Paths.get(TEST_PATH + FOLDER_9 + CHAR_FILE_SEP + FILE_1));
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
    void remove_emptyFolderNotRecursiveFolder_removeEmptyFolder() throws Exception {
        String path = TEST_PATH + FOLDER_5;
        rmApplication.remove(true, false, path);
        File tempFile = new File(path);
        assertFalse(tempFile.exists());
    }

    @Test
    void remove_emptyFolderNotRecursiveFile_removeFile() throws Exception {
        String path = TEST_PATH + FILE_4;
        rmApplication.remove(true, false, path);
        File tempFile = new File(path);
        assertFalse(tempFile.exists());
    }

    @Test
    void remove_notEmptyFolderRecursiveFolder_removeFolderRecursively() throws Exception {
        String path = TEST_PATH + FOLDER_1;
        rmApplication.remove(false, true, path);
        File tempFile = new File(path);
        assertFalse(tempFile.exists());
    }

    @Test
    void remove_notEmptyFolderRecursiveFile_removeFile() throws Exception {
        String path = TEST_PATH + FILE_5;
        rmApplication.remove(false, true, path);
        File tempFile = new File(path);
        assertFalse(tempFile.exists());
    }

    @Test
    void remove_emptyFolderRecursiveFolder_removeFolderRecursively() throws Exception {
        String path = TEST_PATH + FOLDER_3;
        rmApplication.remove(true, true, path);
        File tempFile = new File(path);
        assertFalse(tempFile.exists());
    }

    @Test
    void remove_notEmptyFolderNotRecursiveFolder_removeFile() throws Exception {
        String path = TEST_PATH + FOLDER_6 + CHAR_FILE_SEP + FOLDER_7 + CHAR_FILE_SEP + FILE_6;
        rmApplication.remove(false, false, path);
        File tempFile = new File(path);
        assertFalse(tempFile.exists());
    }

    @Test
    void remove_notEmptyFolderNotRecursiveFolderMultipleFiles_removeFile() throws Exception {
        String path = TEST_PATH + FOLDER_6 + CHAR_FILE_SEP + FOLDER_7 + CHAR_FILE_SEP + FILE_7;
        String path2 = TEST_PATH + FOLDER_6 + CHAR_FILE_SEP + FOLDER_7 + CHAR_FILE_SEP + FILE_8;
        rmApplication.remove(false, false, new String[]{path, path2});
        File tempFile = new File(path);
        File tempFile2 = new File(path2);
        assertFalse(tempFile.exists());
        assertFalse(tempFile2.exists());
    }

    @Test
    void remove_notEmptyFolderNotRecursiveFolder_throwsError() throws Exception {
        String path = TEST_PATH + FOLDER_6 + CHAR_FILE_SEP + FOLDER_7;
        assertThrows(Exception.class, () -> rmApplication.remove(false, false, path));
    }

    @Test
    void remove_emptyFolderNotRecursiveFolderOneEmptyOneNonEmpty_removeEmptyFolderButThrowsErrorForNonEmpty() throws Exception {
        String path = TEST_PATH + FOLDER_8;
        String path2 = TEST_PATH + FOLDER_9;
        assertThrows(Exception.class, () -> rmApplication.remove(true, false, new String[]{path, path2}));
        File tempFile = new File(path);
        File tempFile2 = new File(path2);
        assertFalse(tempFile.exists());
        assertTrue(tempFile2.exists());
    }
}