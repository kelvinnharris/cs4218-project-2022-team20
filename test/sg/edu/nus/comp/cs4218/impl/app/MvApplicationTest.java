package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.*;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.*;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FILE_SEP;
import static sg.edu.nus.comp.cs4218.impl.util.TestConstants.MV_FOLDER;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class MvApplicationTest {

    /* before each file path:
        > srcFolder
           > file1.txt
           > srcFolder1
              > file2.txt
              > srcFolder2
                > file3.xml
        > destFolder
     */

    private static final String SRC_FOLDER_NAME = "srcFolder";
    private static final String SRC_FOLDER1_NAME = "srcFolder1";
    private static final String SRC_FOLDER2_NAME = "srcFolder2";
    private static final String FILE1_NAME = "file1.txt";
    private static final String FILE2_NAME = "file2.txt";
    private static final String FILE3_NAME = "file3.xml";
    private static final String DEST_FOLDER_NAME = "destFolder";
    private static final String NE_FILE_NAME = "nonExistent.txt";
    private static final String TEST_PATH = Environment.currentDirectory + CHAR_FILE_SEP + MV_FOLDER;
    private static final String SRC_FOLDER_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME;
    private static final String SRC_FOLDER1_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME;
    private static final String SRC_FOLDER2_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + SRC_FOLDER2_NAME;
    private static final String FILE1_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP + FILE1_NAME;
    private static final String FILE2_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + FILE2_NAME;
    private static final String FILE3_PATH = TEST_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + SRC_FOLDER2_NAME + CHAR_FILE_SEP + FILE3_NAME;
    private static final String DEST_FOLDER_PATH = TEST_PATH + CHAR_FILE_SEP + DEST_FOLDER_NAME;
    private static final String NE_FILE_PATH = TEST_PATH + CHAR_FILE_SEP + NE_FILE_NAME;
    private static MvApplication mvApplication;

    @BeforeAll
    static void setUp() {
        mvApplication = new MvApplication();
    }

    @BeforeEach
    void setUpEach() throws IOException {
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

    @AfterEach
    void deleteAll() {
        deleteDir(new File(TEST_PATH));
    }

    @Test
    void mvSrcFileToDestFile_moveSrcFileToDestFileValid_shouldOverwriteDestFileContent() throws MvException {
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            mvApplication.mvSrcFileToDestFile(true, FILE1_PATH, FILE2_PATH);
            assertFalse(Files.exists(Paths.get(FILE1_PATH)));
            String file2Content = readString(Paths.get(FILE2_PATH));
            assertEquals(file1Content, file2Content);
        } catch (Exception e) {
            throw new MvException(e);
        }
    }


    @Test
    void mvSrcFileToDestFile_moveSrcFileToNonExistentDestFile_shouldRenameSrcFileToDestFile() throws MvException {
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            mvApplication.mvSrcFileToDestFile(true, FILE1_PATH, NE_FILE_PATH);
            assertFalse(Files.exists(Paths.get(FILE1_PATH)));
            assertTrue(Files.exists(Paths.get(NE_FILE_PATH)));
            String newFileContent = readString(Paths.get(NE_FILE_PATH));
            assertEquals(file1Content, newFileContent);
            Files.delete(Paths.get(NE_FILE_PATH));
        } catch (Exception e) {
            throw new MvException(e);
        }
    }


    @Test
    void run_moveSrcFileToNonExistentDestFile_shouldRenameSrcFileToDestFile() throws MvException {
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            mvApplication.run(new String[]{FILE1_PATH, NE_FILE_PATH}, System.in, System.out);
            assertFalse(Files.exists(Paths.get(FILE1_PATH)));
            assertTrue(Files.exists(Paths.get(NE_FILE_PATH)));
            String newFileContent = readString(Paths.get(NE_FILE_PATH));
            assertEquals(file1Content, newFileContent);
            Files.delete(Paths.get(NE_FILE_PATH));
        } catch (Exception e) {
            throw new MvException(e);
        }
    }


    @Test
    void mvSrcFileToDestFile_moveNonExistentSrcFileToDestFile_shouldThrowMvException() {
        assertThrows(MvException.class, () -> mvApplication.mvSrcFileToDestFile(true, NE_FILE_PATH, FILE1_PATH));
    }


    @Test
    void mvFilesToFolder_moveSrcFileToDestFolderDoNotOverwrite_shouldNotMoveSrcFile() throws MvException {
        try {
            Files.createFile(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_NAME));

            String fileContentBefore = readString(Paths.get(FILE1_PATH));
            assertTrue(Files.exists(Paths.get(FILE1_PATH)));
            mvApplication.mvFilesToFolder(false, DEST_FOLDER_PATH, FILE1_PATH);
            assertTrue(Files.exists(Paths.get(FILE1_PATH)));
            String fileContentAfter = readString(Paths.get(FILE1_PATH));

            assertEquals(fileContentBefore, fileContentAfter); // assert not overwritten
            assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_NAME))); // still exists, not moved

            Files.delete(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_NAME));
        } catch (Exception e) {
            throw new MvException(e);
        }
    }


    @Test
    void mvFilesToFolder_moveSrcFilesToDestFolderValid_shouldMoveSrcFile() throws MvException {
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String file2Content = readString(Paths.get(FILE2_PATH));
            String[] files = {FILE1_PATH, FILE2_PATH};
            mvApplication.mvFilesToFolder(true, DEST_FOLDER_PATH, files);

            assertFalse(Files.exists(Paths.get(FILE1_PATH)));
            assertFalse(Files.exists(Paths.get(FILE2_PATH)));
            assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_NAME)));
            assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE2_NAME)));

            String newFile1Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_NAME));
            String newFile2Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE2_NAME));
            assertEquals(file1Content, newFile1Content);
            assertEquals(file2Content, newFile2Content);

        } catch (Exception e) {
            throw new MvException(e);
        }
    }


    @Test
    void mvFilesToFolder_moveSrcFolderToDestFolder_shouldMoveWholeFolder() throws MvException {
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String file2Content = readString(Paths.get(FILE2_PATH));
            String file3Content = readString(Paths.get(FILE3_PATH));

            mvApplication.mvFilesToFolder(true, DEST_FOLDER_PATH, SRC_FOLDER_PATH);
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

            assertFalse(Files.exists(Paths.get(SRC_FOLDER_PATH)));
            assertFalse(Files.exists(Paths.get(SRC_FOLDER1_PATH)));
            assertFalse(Files.exists(Paths.get(SRC_FOLDER2_PATH)));
            assertFalse(Files.exists(Paths.get(FILE1_PATH)));
            assertFalse(Files.exists(Paths.get(FILE2_PATH)));
            assertFalse(Files.exists(Paths.get(FILE3_PATH)));

            String newFile1Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME
                    + CHAR_FILE_SEP + FILE1_NAME));
            assertEquals(file1Content, newFile1Content);
            String newFile2Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME
                    + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + FILE2_NAME));
            assertEquals(file2Content, newFile2Content);
            String newFile3Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_NAME
                    + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + SRC_FOLDER2_NAME + CHAR_FILE_SEP + FILE3_NAME));
            assertEquals(file3Content, newFile3Content);

        } catch (Exception e) {
            throw new MvException(e);
        }
    }


    @Test
    void mvFilesToFolder_moveNonExistentSrcFileToDestFolder_shouldThrowMvException() {
        assertThrows(MvException.class, () -> mvApplication.mvFilesToFolder(true, DEST_FOLDER_PATH, NE_FILE_PATH));
    }


    @Test
    void mvFilesToFolder_moveSrcFilesToNonExistentDestFolder_shouldThrowMvException() {
        String[] files = {FILE1_PATH, FILE2_PATH};
        assertThrows(MvException.class, () -> mvApplication.mvFilesToFolder(true, "nonExistent", files));
    }
}
