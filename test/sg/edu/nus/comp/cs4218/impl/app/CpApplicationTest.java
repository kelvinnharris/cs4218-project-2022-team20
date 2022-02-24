package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.exception.CpException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;

public class CpApplicationTest {
    /* before each file path:
        > srcFolder
           > file1.txt
           > srcFolder1
              > file2.txt
              > srcFolder2
                > file3.xml
        > destFolder
     */

    private static CpApplication cpApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;

    private static final String SRC_FOLDER_NAME = "srcFolder";
    private static final String SRC_FOLDER1_NAME = "srcFolder1";
    private static final String SRC_FOLDER2_NAME = "srcFolder2";
    private static final String FILE1_NAME = "file1.txt";
    private static final String FILE2_NAME = "file2.txt";
    private static final String FILE3_NAME = "file3.xml";
    private static final String DEST_FOLDER_NAME = "destFolder";

    private static final String SRC_FOLDER_PATH = SRC_FOLDER_NAME;
    private static final String SRC_FOLDER1_PATH = SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME;
    private static final String SRC_FOLDER2_PATH = SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + "srcFolder2";
    private static final String FILE1_PATH = SRC_FOLDER_NAME + CHAR_FILE_SEP + "file1.txt";
    private static final String FILE2_PATH = SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + "file2.txt";
    private static final String FILE3_PATH = SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + "srcFolder2" + CHAR_FILE_SEP + "file3.xml";
    private static final String DEST_FOLDER_PATH = "destFolder";


    @BeforeAll
    static void setUp() {
        cpApplication = new CpApplication();
    }

    @BeforeEach
    void setUpEach() throws IOException {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(SRC_FOLDER_PATH));
        deleteDir(new File(DEST_FOLDER_PATH));
        Files.createDirectories(Paths.get(SRC_FOLDER_PATH));
        Files.createDirectories(Paths.get(SRC_FOLDER1_PATH));
        Files.createDirectories(Paths.get(SRC_FOLDER2_PATH));
        Files.createFile(Paths.get(FILE1_PATH));
        Files.createFile(Paths.get(FILE2_PATH));
        Files.createFile(Paths.get(FILE3_PATH));

        Files.createDirectories(Paths.get(DEST_FOLDER_PATH));

        List<String> lines = Arrays.asList("The first line", "The second line");
        Files.write(Paths.get(FILE1_PATH), lines, StandardCharsets.UTF_8);
    }

    @AfterAll
    static void tearDown() throws IOException {
        deleteDir(new File(SRC_FOLDER_PATH));
        deleteDir(new File(DEST_FOLDER_PATH));
        Files.deleteIfExists(Paths.get(ROOT_PATH + CHAR_FILE_SEP + "nonExistent.txt"));
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

    static String readString(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    @Test
    void testCp_copyFileContentValid_shouldOverwriteDestFileContent() throws CpException {
        cpApplication.cpSrcFileToDestFile(false, FILE1_PATH, FILE2_PATH);
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String file2Content = readString(Paths.get(FILE2_PATH));
            assertEquals(file1Content, file2Content);
        } catch (Exception e) {
            throw new CpException(e);
        }
    }

    @Test
    void testCp_copyFileContentSameSourceAndDest_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.cpSrcFileToDestFile(false, FILE1_PATH, FILE1_PATH));
    }

    @Test
    void testCp_copyFileContentDestDoesNotExist_shouldCreateNewFileAndCopyContent() throws CpException {
        cpApplication.cpSrcFileToDestFile(false, FILE1_PATH, "nonExistent.txt");
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String file2Content = readString(Paths.get("nonExistent.txt"));
            assertEquals(file1Content, file2Content);
        } catch (Exception e) {
            throw new CpException(e);
        }
    }

    @Test
    void testCp_copyFilesToFolderSingleFileValid_shouldCopyFilesOver() throws CpException {
        String newFilePath = DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_NAME;
        cpApplication.cpFilesToFolder(false, DEST_FOLDER_PATH, FILE1_PATH);
        assertTrue(Files.exists(Paths.get(newFilePath)));
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String file2Content = readString(Paths.get(newFilePath));
            assertEquals(file1Content, file2Content);
        } catch (Exception e) {
            throw new CpException(e);
        }
    }

    @Test
    void testCp_copyFilesToFolderMutipleFilesValid_shouldCopyFilesOver() throws CpException {
        String newFile1Path = DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_NAME;
        String newFile2Path = DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE2_NAME;
        String[] srcFiles = {FILE1_PATH, FILE2_PATH};
        cpApplication.cpFilesToFolder(false, DEST_FOLDER_PATH, srcFiles);
        assertTrue(Files.exists(Paths.get(newFile1Path)));
        assertTrue(Files.exists(Paths.get(newFile2Path)));
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String newFile1Content = readString(Paths.get(newFile1Path));
            assertEquals(file1Content, newFile1Content);
            String file2Content = readString(Paths.get(FILE2_PATH));
            String newFile2Content = readString(Paths.get(newFile2Path));
            assertEquals(file2Content, newFile2Content);
        } catch (Exception e) {
            throw new CpException(e);
        }
    }

    @Test
    void testCp_copyFileToNonExistentFolder_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.cpFilesToFolder(true, "nonExistentFolder", FILE1_PATH));
    }

    @Test
    void testCp_copyFolderToFolderNotRecursive_shouldThrowCpException() throws CpException {
        assertThrows(CpException.class, () -> cpApplication.cpFilesToFolder(false, DEST_FOLDER_PATH, SRC_FOLDER_PATH));
    }

    @Test
    void testCp_copyFolderToEmptyFolderRecursive_shouldCopyWholeFolderOver() throws CpException {
        cpApplication.cpFilesToFolder(true, DEST_FOLDER_PATH, SRC_FOLDER_PATH);
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_PATH)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER1_PATH)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER2_PATH)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_PATH)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE2_PATH)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE3_PATH)));
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String newFile1Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_PATH));
            assertEquals(file1Content, newFile1Content);
            String file2Content = readString(Paths.get(FILE2_PATH));
            String newFile2Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE2_PATH));
            assertEquals(file2Content, newFile2Content);
            String file3Content = readString(Paths.get(FILE3_PATH));
            String newFile3Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE3_PATH));
            assertEquals(file3Content, newFile3Content);
        } catch (Exception e) {
            throw new CpException(e);
        }
    }

    @Test
    void testCp_copyFolderToEmptyFolderSelfRecursive_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.cpFilesToFolder(true, SRC_FOLDER_PATH, SRC_FOLDER_PATH));
    }

    @Test
    void testCp_copyFolderToNonEmptyFolder_shouldOverwritePreviousCopy() throws CpException {
        cpApplication.cpFilesToFolder(true, DEST_FOLDER_PATH, SRC_FOLDER2_PATH);
        cpApplication.cpFilesToFolder(true, DEST_FOLDER_PATH, SRC_FOLDER2_PATH);
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER2_NAME)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER2_NAME + CHAR_FILE_SEP + FILE3_NAME)));
        try {
            String file3Content = readString(Paths.get(FILE3_PATH));
            String newFile3Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER2_NAME + CHAR_FILE_SEP + FILE3_NAME));
            assertEquals(file3Content, newFile3Content);
        } catch (Exception e) {
            throw new CpException(e);
        }
    }
}
