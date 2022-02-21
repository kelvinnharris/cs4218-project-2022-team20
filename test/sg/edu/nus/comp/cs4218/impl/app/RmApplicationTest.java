package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.RmException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

class RmApplicationTest {
    private static RmApplication rmApplication;

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpRmTestFolder" + CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + TEST_FOLDER_NAME;

    @BeforeAll
    static void setUp() throws IOException {
        rmApplication = new RmApplication();

        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH + "folder1" + CHAR_FILE_SEP + "folder2"));
        Files.createDirectories(Paths.get(TEST_PATH + "folder3" + CHAR_FILE_SEP + "folder4"));
        Files.createDirectories(Paths.get(TEST_PATH + "folder5"));
        Files.createDirectories(Paths.get(TEST_PATH + "folder6" + CHAR_FILE_SEP + "folder7"));
        Files.createFile(Paths.get(TEST_PATH + "folder1" + CHAR_FILE_SEP + "file1.txt"));
        Files.createFile(Paths.get(TEST_PATH + "folder1" + CHAR_FILE_SEP + "file2.iml"));
        Files.createFile(Paths.get(TEST_PATH + "file4.xml"));
        Files.createFile(Paths.get(TEST_PATH + "file5.txt"));
        Files.createFile(Paths.get(TEST_PATH + "folder3" + CHAR_FILE_SEP + "folder4" + CHAR_FILE_SEP + "file3.txt"));
        Files.createFile(Paths.get(TEST_PATH + "folder6" + CHAR_FILE_SEP + "folder7" + CHAR_FILE_SEP + "file6.txt"));

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
    void remove_emptyFolderNotRecursiveFolder_removeEmptyFolder() throws Exception {
        String path = TEST_PATH + "folder5";
        rmApplication.remove(true, false, new String[] {path});
        File tempFile = new File(path);
        assertEquals(false, tempFile.exists());

    }

    @Test
    void remove_emptyFolderNotRecursiveFile_removeFile() throws Exception {
        String path = TEST_PATH + "file4.xml";
        rmApplication.remove(true, false, new String[] {path});
        File tempFile = new File(path);
        assertEquals(false, tempFile.exists());

    }

    @Test
    void remove_notEmptyFolderRecursiveFolder_removeFolderRecursively() throws Exception {
        String path = TEST_PATH + "folder1";
        rmApplication.remove(false, true, new String[] {path});
        File tempFile = new File(path);
        assertEquals(false, tempFile.exists());
    }

    @Test
    void remove_notEmptyFolderRecursiveFile_removeFile() throws Exception {
        String path = TEST_PATH + "file5.txt";
        rmApplication.remove(false, true, new String[] {path});
        File tempFile = new File(path);
        assertEquals(false, tempFile.exists());
    }

    @Test
    void remove_emptyFolderRecursiveFolder_removeFolderRecursively() throws Exception {
        String path = TEST_PATH + "folder3";
        rmApplication.remove(true, true, new String[] {path});
        File tempFile = new File(path);
        assertEquals(false, tempFile.exists());
    }

    @Test
    void remove_notEmptyFolderNotRecursiveFolder_removeFile() throws Exception {
        String path = TEST_PATH + "folder6" + CHAR_FILE_SEP + "folder7" + CHAR_FILE_SEP + "file6.txt";
        rmApplication.remove(false, false, new String[] {path});
        File tempFile = new File(path);
        assertEquals(false, tempFile.exists());
    }

    @Test
    void remove_notEmptyFolderNotRecursiveFolder_throwsError() throws Exception {
        String path = TEST_PATH + "folder6" + CHAR_FILE_SEP + "folder7";
        assertThrows(Exception.class, () -> rmApplication.remove(false, false, new String[] {path}));
    }
}