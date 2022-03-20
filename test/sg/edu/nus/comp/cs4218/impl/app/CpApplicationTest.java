package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CpException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.CP_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

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

    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_PATH = ROOT_PATH + CHAR_FILE_SEP + CP_FOLDER;
    private static final String SRC_FOLDER_NAME = "srcFolder";
    private static final String SRC_FOLDER1_NAME = "srcFolder1";
    private static final String SRC_FOLDER2_NAME = "srcFolder2";
    private static final String FILE1_NAME = "file1.txt";
    private static final String FILE2_NAME = "file2.txt";
    private static final String FILE3_NAME = "file3.xml";
    private static final String NE_FILE_NAME = "nonExistent.txt";
    private static final String DEST_FOLDER_NAME = "destFolder";
    private static final String NEW_FOLDER_NAME = "newFolder";
    private static final String NEW_FILE_NAME = "newFile.txt";

    private static final String SRC_FOLDER_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME;
    private static final String SRC_FOLDER1_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME;
    private static final String SRC_FOLDER2_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + SRC_FOLDER2_NAME;
    private static final String FILE1_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP + FILE1_NAME;
    private static final String FILE2_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + FILE2_NAME;
    private static final String FILE3_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + SRC_FOLDER2_NAME + CHAR_FILE_SEP + FILE3_NAME;
    private static final String DEST_FOLDER_PATH = TEST_PATH + CHAR_FILE_SEP + DEST_FOLDER_NAME;
    private static final String NEW_FOLDER_PATH = TEST_PATH + CHAR_FILE_SEP + NEW_FOLDER_NAME;
    private static final String NE_FILE_PATH = TEST_PATH + CHAR_FILE_SEP + NE_FILE_NAME;
    private static CpApplication cpApplication;

    @BeforeAll
    static void setUp() {
        cpApplication = new CpApplication();
    }

    @BeforeEach
    void setUpEach() throws IOException {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));
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

    @AfterEach
    void tearDownEach() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void cpSrcFileToDestFile_copyFileContentValid_shouldOverwriteDestFileContent() throws CpException {
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
    void cpSrcFileToDestFile_copyFileContentSameSourceAndDest_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.cpSrcFileToDestFile(false, FILE1_PATH, FILE1_PATH));
    }

    @Test
    void cpSrcFileToDestFile_copyFileContentDestDoesNotExist_shouldCreateNewFileAndCopyContent() throws CpException {
        cpApplication.cpSrcFileToDestFile(false, FILE1_PATH, NE_FILE_PATH);
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String file2Content = readString(Paths.get(NE_FILE_PATH));
            assertEquals(file1Content, file2Content);
        } catch (Exception e) {
            throw new CpException(e);
        }
    }

    @Test
    void cpFilesToFolder_copyFilesToFolderSingleFileValid_shouldCopyFilesOver() throws CpException {
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
    void cpFilesToFolder_copyFilesToFolderMutipleFilesValid_shouldCopyFilesOver() throws CpException {
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
    void cpFilesToFolder_copyFileToNonExistentFolder_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.cpFilesToFolder(true, "nonExistentFolder", FILE1_PATH));
    }

    @Test
    void cpFilesToFolder_copyFolderToFolderNotRecursive_shouldThrowCpException() throws CpException {
        assertThrows(CpException.class, () -> cpApplication.cpFilesToFolder(false, DEST_FOLDER_PATH, SRC_FOLDER_PATH));
    }

    @Test
    void cpFilesToFolder_copyFolderToEmptyFolderRecursive_shouldCopyWholeFolderOver() throws CpException {
        cpApplication.cpFilesToFolder(true, DEST_FOLDER_PATH, SRC_FOLDER_PATH);
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP
                + SRC_FOLDER1_NAME)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP
                + SRC_FOLDER1_NAME + CHAR_FILE_SEP + SRC_FOLDER2_NAME)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP
                + FILE1_NAME)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP
                + SRC_FOLDER1_NAME + CHAR_FILE_SEP + FILE2_NAME)));
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP
                + SRC_FOLDER1_NAME + CHAR_FILE_SEP + SRC_FOLDER2_NAME + CHAR_FILE_SEP + FILE3_NAME)));

        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String newFile1Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME
                    + CHAR_FILE_SEP + FILE1_NAME));
            assertEquals(file1Content, newFile1Content);
            String file2Content = readString(Paths.get(FILE2_PATH));
            String newFile2Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME
                    + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + FILE2_NAME));
            assertEquals(file2Content, newFile2Content);
            String file3Content = readString(Paths.get(FILE3_PATH));
            String newFile3Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME
                    + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + SRC_FOLDER2_NAME + CHAR_FILE_SEP + FILE3_NAME));
            assertEquals(file3Content, newFile3Content);
        } catch (Exception e) {
            throw new CpException(e);
        }
    }

    @Test
    void cpFilesToFolder_copyFolderToEmptyFolderSelfRecursive_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.cpFilesToFolder(true, SRC_FOLDER_PATH, SRC_FOLDER_PATH));
    }

    @Test
    void cpFilesToFolder_copyFolderToNonEmptyFolder_shouldOverwritePreviousCopy() throws CpException {
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

    @Test
    void run_emptySrcFiles_shouldThrowException() {
        assertThrows(Exception.class, () -> cpApplication.run(new String[]{null, FILE2_PATH}, System.in, System.out));
    }

    @Test
    void run_nullArgs_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.run(null, System.in, System.out));
    }

    @Test
    void run_SrcIsNull_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.run(new String[]{NEW_FILE_NAME}, System.in, System.out));
    }

    @Test
    void run_SrcIsZeroLength_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.run(new String[]{"-r", NEW_FILE_NAME}, System.in, System.out));
    }

    @Test
    void run_copyFileToNonExistentFile_shouldCreateANewFile() throws Exception {
        cpApplication.run(new String[]{FILE1_PATH, NE_FILE_PATH}, System.in, System.out);
        assertTrue(Files.exists(Paths.get(FILE1_PATH)));
        assertTrue(Files.exists(Paths.get(NE_FILE_PATH)));
        Files.delete(Paths.get(NE_FILE_PATH));
    }

    @Test
    void run_copyFolderToAnotherFolderValid_shouldCopy() throws Exception {
        cpApplication.run(new String[]{"-r", SRC_FOLDER2_PATH, DEST_FOLDER_PATH}, System.in, System.out);
        assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH, SRC_FOLDER2_NAME)));
    }

    @Test
    void run_copyFolderToAnotherFileNoFlag_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.run(new String[]{SRC_FOLDER2_PATH, FILE1_PATH}, System.in, System.out));
    }

    @Test
    void run_copyFolderToAnotherFileFlag_shouldThrowCpException() {
        assertThrows(CpException.class, () -> cpApplication.run(new String[]{"-r", SRC_FOLDER2_PATH, FILE1_PATH}, System.in, System.out));
    }

    @Test
    void run_copyFolderToNonExistentFolder_shouldCreateANewFolder() throws Exception {
        cpApplication.run(new String[]{"-r", SRC_FOLDER2_PATH, NEW_FOLDER_PATH}, System.in, System.out);
        assertTrue(Files.exists(Paths.get(SRC_FOLDER2_PATH)));
        assertTrue(Files.exists(Paths.get(NEW_FOLDER_PATH)));
        assertTrue(Files.exists(Paths.get(NEW_FOLDER_PATH, SRC_FOLDER2_NAME)));
        assertTrue(Files.exists(Paths.get(NEW_FOLDER_PATH, SRC_FOLDER2_NAME, FILE3_NAME)));
        deleteDir(Paths.get(NEW_FOLDER_PATH).toFile());
    }
}
