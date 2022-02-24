package sg.edu.nus.comp.cs4218.impl.app;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.exception.MvException;

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

    private static MvApplication mvApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;

    private static final String SRC_FOLDER_NAME = "srcFolder";
    private static final String SRC_FOLDER1_NAME = "srcFolder1";
    private static final String SRC_FOLDER2_NAME = "srcFolder2";
    private static final String FILE1_NAME = "file1.txt";
    private static final String FILE2_NAME = "file2.txt";
    private static final String FILE3_NAME = "file3.xml";
    private static final String DEST_FOLDER_NAME = "destFolder";

    private static final String NE_FILE_NAME = "nonExistent.txt";

    private static final String SRC_FOLDER_PATH = SRC_FOLDER_NAME;
    private static final String SRC_FOLDER1_PATH = SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME;
    private static final String SRC_FOLDER2_PATH = SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + SRC_FOLDER2_NAME;
    private static final String FILE1_PATH = SRC_FOLDER_NAME + CHAR_FILE_SEP + FILE1_NAME;
    private static final String FILE2_PATH = SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + FILE2_NAME;
    private static final String FILE3_PATH = SRC_FOLDER_NAME + CHAR_FILE_SEP + SRC_FOLDER1_NAME + CHAR_FILE_SEP + SRC_FOLDER2_NAME + CHAR_FILE_SEP + FILE3_NAME;
    private static final String DEST_FOLDER_PATH = "destFolder";


    @BeforeAll
    static void setUp() {
        mvApplication = new MvApplication();
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
        Files.deleteIfExists(Paths.get(ROOT_PATH + CHAR_FILE_SEP + NE_FILE_NAME));
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
    void testMv_moveSrcFileToDestFileValid_shouldOverwriteDestFileContent() throws MvException {
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
    void testMv_moveSrcFileToNonExistentDestFile_shouldRenameSrcFileToDestFile() throws MvException {
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            mvApplication.mvSrcFileToDestFile(true, FILE1_PATH, NE_FILE_NAME);
            assertFalse(Files.exists(Paths.get(FILE1_PATH)));
            assertTrue(Files.exists(Paths.get(NE_FILE_NAME)));
            String newFileContent = readString(Paths.get(NE_FILE_NAME));
            assertEquals(file1Content, newFileContent);
        } catch (Exception e) {
            throw new MvException(e);
        }
    }


    @Test
    void testMv_moveNonExistentSrcFileToDestFile_shouldThrowMvException() {
        assertThrows(MvException.class, () -> mvApplication.mvSrcFileToDestFile(true, NE_FILE_NAME, FILE1_PATH));
    }


    @Test
    void testMv_moveSrcFileToDestFolderDoNotOverwrite_shouldNotMoveSrcFile() throws MvException {
        try {
            Files.createFile(Paths.get(FILE1_NAME));
            List<String> srcFileLines = Arrays.asList("This is", "src file");
            Files.write(Paths.get(FILE1_NAME), srcFileLines, StandardCharsets.UTF_8);
            String srcFileContent = readString(Paths.get(FILE1_NAME));
            String destFileContent = readString(Paths.get(FILE1_PATH));

            mvApplication.mvFilesToFolder(false, SRC_FOLDER_NAME, FILE1_NAME);

            assertEquals(srcFileContent, readString(Paths.get(FILE1_NAME))); // assert no change
            assertEquals(destFileContent, readString(Paths.get(FILE1_PATH))); // assert no change
            assertNotEquals(readString(Paths.get(FILE1_NAME)), readString(Paths.get(FILE1_PATH)));
            Files.delete(Paths.get(FILE1_NAME));

        } catch (Exception e) {
            throw new MvException(e);
        }
    }


    @Test
    void testMv_moveSrcFilesToDestFolderValid_shouldMoveSrcFile() throws MvException {
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String file2Content = readString(Paths.get(FILE2_PATH));
            mvApplication.mvFilesToFolder(true, FILE1_PATH, DEST_FOLDER_PATH);

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
    void testMv_moveSrcFolderToDestFolder_shouldMoveWholeFolder() throws MvException {
        try {
            String file1Content = readString(Paths.get(FILE1_PATH));
            String file2Content = readString(Paths.get(FILE2_PATH));
            String file3Content = readString(Paths.get(FILE3_PATH));

            mvApplication.mvFilesToFolder(true, DEST_FOLDER_PATH, SRC_FOLDER_PATH);
            assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER_PATH)));
            assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER1_PATH)));
            assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + SRC_FOLDER2_PATH)));
            assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_PATH)));
            assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE2_PATH)));
            assertTrue(Files.exists(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE3_PATH)));

            assertFalse(Files.exists(Paths.get(SRC_FOLDER_PATH)));
            assertFalse(Files.exists(Paths.get(SRC_FOLDER1_PATH)));
            assertFalse(Files.exists(Paths.get(SRC_FOLDER2_PATH)));
            assertFalse(Files.exists(Paths.get(FILE1_PATH)));
            assertFalse(Files.exists(Paths.get(FILE2_PATH)));
            assertFalse(Files.exists(Paths.get(FILE3_PATH)));

            String newFile1Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE1_PATH));
            assertEquals(file1Content, newFile1Content);
            String newFile2Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE2_PATH));
            assertEquals(file2Content, newFile2Content);
            String newFile3Content = readString(Paths.get(DEST_FOLDER_PATH + CHAR_FILE_SEP + FILE3_PATH));
            assertEquals(file3Content, newFile3Content);

        } catch (Exception e) {
            throw new MvException(e);
        }
    }


    @Test
    void testMv_moveNonExistentSrcFileToDestFolder_shouldThrowMvException() {
        assertThrows(MvException.class, () -> mvApplication.mvFilesToFolder(true, DEST_FOLDER_PATH, NE_FILE_NAME));
    }


    @Test
    void testMv_moveSrcFilesToNonExistentDestFolder_shouldThrowMvException() {
        String[] files = {FILE1_PATH, FILE2_PATH};
        assertThrows(MvException.class, () -> mvApplication.mvFilesToFolder(true, "nonExistent", files));
    }
}
