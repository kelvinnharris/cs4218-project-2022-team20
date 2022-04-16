package sg.edu.nus.comp.cs4218.impl.Hackathon;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.impl.app.CatApplication;
import sg.edu.nus.comp.cs4218.impl.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.createFile;
import static sg.edu.nus.comp.cs4218.impl.util.TestUtils.deleteDir;

public class HackathonBugs3 {
    private static CatApplication catApplication;
    private static final String ROOT_PATH = Environment.currentDirectory;
    private static final String TEST_FOLDER_NAME = "tmpCatTestFolder" + StringUtils.CHAR_FILE_SEP;
    private static final String TEST_PATH = ROOT_PATH + StringUtils.CHAR_FILE_SEP + TEST_FOLDER_NAME;

    private static final String FILE_NAME_3 = "test3.txt";
    private static final String FILE_PATH_3 = TEST_PATH + FILE_NAME_3;

    @BeforeAll
    static void setUp() throws IOException {
        deleteDir(new File(TEST_PATH));
        Files.createDirectories(Paths.get(TEST_PATH));

        createFile(FILE_PATH_3, "");
    }

    @BeforeEach
    void setUpEach() {
        // Instantiate here because every time there is a call command, a new instance of the application is created
        catApplication = new CatApplication();
        Environment.currentDirectory = ROOT_PATH;
    }

    @AfterAll
    static void tearDown() {
        Environment.currentDirectory = ROOT_PATH;
        deleteDir(new File(TEST_PATH));
    }

    // command: cat tmpCatTestFolder/test1.txt tmpCatTestFolder/test2.txt
    @Test
    void testCatFiles_emptyFileInputWithoutFlag_shouldShowEmptyStringWithNoNewLine() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();
        catApplication.run(new String[]{FILE_PATH_3}, System.in, outputStream);

        assertEquals("", outputStream.toString());
    }
}
