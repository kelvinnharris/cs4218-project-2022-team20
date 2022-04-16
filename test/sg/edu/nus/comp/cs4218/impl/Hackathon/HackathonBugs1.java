package sg.edu.nus.comp.cs4218.impl.Hackathon;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sg.edu.nus.comp.cs4218.Command;
import sg.edu.nus.comp.cs4218.Environment;
import sg.edu.nus.comp.cs4218.exception.CpException;
import sg.edu.nus.comp.cs4218.exception.MvException;
import sg.edu.nus.comp.cs4218.impl.util.ApplicationRunner;
import sg.edu.nus.comp.cs4218.impl.util.CommandBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HackathonBugs1 {

    private static final String INITIAL_DIR = Environment.currentDirectory;
    private static final String TEMP = "temp-command";
    private static final String[] DIRECTORY_NAMES = {
            "dir-1", "dir-2", "dir-3", "dir-1-1", "dir-1-2"
    };
    private static final String[] FILE_NAMES = {
            "file-1.a", "file-2.b", "file-3.a",
            "file-1-1.c", "file-1-2.d", "file-2-1.e", "file-1-1-1.f"
    };
    private static final String[] FILE_CONTENTS = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.\r\n"
                    + "Velit euismod in pellentesque massa.\r\n"
                    + "Volutpat blandit aliquam etiam erat velit scelerisque in.\r\n"
                    + "Ullamcorper eget nulla facilisi etiam dignissim diam quis.\r\n"
                    + "Enim sit amet venenatis urna cursus.",
            "Non curabitur gravida arcu ac tortor dignissim convallis aenean et.\r"
                    + "Nunc sed augue lacus viverra vitae congue eu.\r"
                    + "Sit amet mauris commodo quis imperdiet.\r"
                    + "Tortor id aliquet lectus proin.\r"
                    + "Pretium nibh ipsum consequat nisl vel pretium.",
            "Eget egestas purus viverra accumsan in nisl.\n"
                    + "Ultrices gravida dictum fusce ut placerat orci.\n"
                    + "Turpis massa tincidunt dui ut ornare lectus.\n"
                    + "Sapien eget mi proin sed libero enim.\n"
                    + "Facilisis gravida neque convallis a cras semper auctor.",
            "Cras tincidunt lobortis feugiat vivamus.\r\n"
                    + "Nibh nisl condimentum id venenatis.\r\n"
                    + "Vel eros donec ac odio tempor orci dapibus.\r\n"
                    + "Tincidunt praesent semper feugiat nibh sed pulvinar.\r\n"
                    + "Nec nam aliquam sem et.\r\n",
            "Dui accumsan sit amet nulla facilisi morbi tempus.\r"
                    + "At augue eget arcu dictum varius duis at consectetur.\r"
                    + "Congue quisque egestas diam in arcu cursus euismod.\r"
                    + "Sed risus ultricies tristique nulla aliquet.\r"
                    + "Nec sagittis aliquam malesuada bibendum arcu vitae elementum.\r",
            "Morbi quis commodo odio aenean.\n"
                    + "Mattis vulputate enim nulla aliquet porttitor lacus.\n"
                    + "Vestibulum sed arcu non odio euismod lacinia at quis.\n"
                    + "Praesent semper feugiat nibh sed pulvinar proin.\n"
                    + "Consequat interdum varius sit amet mattis vulputate enim nulla.\n",
            "Euismod in pellentesque massa placerat duis ultricies lacus.\r\n"
                    + "Sociis natoque penatibus et magnis.\r\n"
                    + "Viverra accumsan in nisl nisi scelerisque eu ultrices.\r\n"
                    + "Pharetra massa massa ultricies mi quis.\r\n"
                    + "Velit aliquet sagittis id consectetur purus ut faucibus.\r\n"
    };
    private static final String BYTE_FILENAME = "byte-file.a";
    private static final byte[] BYTE_FILE_CONTENT = {(byte) 255};
    private static final Path PERMS_DIR = Paths.get("/tmp", "perms");
    private static final Path UNREADABLE_DIR = Paths.get(
            PERMS_DIR.toString(), "unreadable-dir");
    private static final Path UNWRITABLE_DIR = Paths.get(
            PERMS_DIR.toString(), "unwritable-dir");
    private static final Path UNEXECUTABLE_DIR = Paths.get(
            PERMS_DIR.toString(), "unexecutable-dir");
    private static final Path UNREADABLE_FILE = Paths.get(
            PERMS_DIR.toString(), "unreadable-file");
    private static final Path UNWRITABLE_FILE = Paths.get(
            PERMS_DIR.toString(), "unwritable-file");
    private static Path testRoot;
    private static Path[] directoryPaths;
    private static Path[] filePaths;

    private ApplicationRunner appCreator;
    private Command command;
    private ByteArrayInputStream istream;
    private ByteArrayOutputStream ostream;

    @BeforeAll
    static void setUpBeforeAll() {
        testRoot = Paths.get(INITIAL_DIR, TEMP);
        directoryPaths = new Path[]{
                Paths.get(testRoot.toString(), DIRECTORY_NAMES[0]),
                Paths.get(testRoot.toString(), DIRECTORY_NAMES[1]),
                Paths.get(testRoot.toString(), DIRECTORY_NAMES[2]),
                Paths.get(testRoot.toString(), DIRECTORY_NAMES[0], DIRECTORY_NAMES[3]),
                Paths.get(testRoot.toString(), DIRECTORY_NAMES[0], DIRECTORY_NAMES[4])
        };
        filePaths = new Path[] {
                Paths.get(testRoot.toString(), FILE_NAMES[0]),
                Paths.get(testRoot.toString(), FILE_NAMES[1]),
                Paths.get(testRoot.toString(), FILE_NAMES[2]),
                Paths.get(testRoot.toString(), DIRECTORY_NAMES[0], FILE_NAMES[3]),
                Paths.get(testRoot.toString(), DIRECTORY_NAMES[0], FILE_NAMES[4]),
                Paths.get(testRoot.toString(), DIRECTORY_NAMES[1], FILE_NAMES[5]),
                Paths.get(testRoot.toString(), DIRECTORY_NAMES[0], DIRECTORY_NAMES[3], FILE_NAMES[6]),
        };
    }

    void setUpPerms() throws IOException {
        if (isWindowsOS()) {
            return;
        }
        Set<PosixFilePermission> unreadablePerms = PosixFilePermissions
                .fromString("-wx-wx-wx");
        Set<PosixFilePermission> unwritablePerms = PosixFilePermissions
                .fromString("r-xr-xr-x");
        Set<PosixFilePermission> unexecutablePerms = PosixFilePermissions
                .fromString("rw-rw-rw-");

        Files.createDirectories(PERMS_DIR);

        Files.createDirectories(UNREADABLE_DIR);
        Files.setPosixFilePermissions(UNREADABLE_DIR, unreadablePerms);

        Files.createDirectories(UNWRITABLE_DIR);
        Files.setPosixFilePermissions(UNWRITABLE_DIR, unwritablePerms);

        Files.createDirectories(UNEXECUTABLE_DIR);
        Files.setPosixFilePermissions(UNEXECUTABLE_DIR, unexecutablePerms);

        Files.createFile(UNREADABLE_FILE);
        Files.writeString(UNREADABLE_FILE, "Enim");
        Files.setPosixFilePermissions(UNREADABLE_FILE, unreadablePerms);

        Files.createFile(UNWRITABLE_FILE);
        Files.writeString(UNWRITABLE_FILE, "unwritable file");
        Files.setPosixFilePermissions(UNWRITABLE_FILE, unwritablePerms);
    }

    @BeforeEach
    void setUp() throws Exception {
        appCreator = new ApplicationRunner();
        istream = new ByteArrayInputStream(new byte[0]);
        ostream = new ByteArrayOutputStream();
        Files.createDirectory(testRoot);
        Environment.currentDirectory = testRoot.toString();

        for (Path dirPath : directoryPaths) {
            Files.createDirectories(dirPath);
        }
        for (int i = 0; i < filePaths.length; i++) {
            Path filePath = filePaths[i];
            Files.createFile(filePath);

            FileOutputStream fileOutputStream =
                    new FileOutputStream(filePath.toFile());
            fileOutputStream.write(FILE_CONTENTS[i].getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();
        }
        Path byteFilePath = Paths.get(testRoot.toString(), BYTE_FILENAME);
        Files.createFile(byteFilePath);
        FileOutputStream fileOutputStream =
                new FileOutputStream(byteFilePath.toFile());
        fileOutputStream.write(BYTE_FILE_CONTENT);
        fileOutputStream.close();

        setUpPerms();
    }

    @AfterEach
    void tearDown() {
        Environment.currentDirectory = INITIAL_DIR;
        deleteDir(testRoot.toFile());

        if (!isWindowsOS()) {
            deleteDir(PERMS_DIR.toFile());
        }
    }

    private static void deleteDir(File directory) {
        File[] contents = directory.listFiles();
        if (contents == null) {
            directory.delete();
            return;
        }

        for (File file : contents) {
            if (Files.isSymbolicLink(file.toPath())) {
                file.delete();
                continue;
            }

            deleteDir(file);
        }

        directory.delete();
    }

    private boolean isWindowsOS() {
        return System.getProperty("os.name").startsWith("Windows");
    }

    @Test
    void evaluateFromP11_CpSrcFileWithNoDestFile_shouldCpToNewFile() throws Exception {
        String newFileName = "file";
        Path newFilePath = Paths.get(testRoot.toString(), newFileName);
        String commandString = String.format("cp %s %s", FILE_NAMES[2], newFileName);
        String expected = "";
        String expectedContent = Files.readString(filePaths[2]);
        command = CommandBuilder.parseCommand(commandString, appCreator);

        assertFalse(newFilePath.toFile().exists());
        Path fileName2 = Paths.get(FILE_NAMES[2]);

        command.evaluate(istream, ostream);

        assertTrue(newFilePath.toFile().exists());

        String actual = ostream.toString();
        String actualContent = Files.readString(newFilePath);

        assertEquals(expectedContent, actualContent);
        assertEquals(expected, actual);
    }

    @Test
    void evaluateFromP13_CpAbsoluteSrcDirWithNoDestDir_shouldCpToNewDir() throws Exception {
        String newDirName = "dir";
        Path newDirPath = Paths.get(testRoot.toString(), newDirName);
        Path newFilePath = Paths.get(testRoot.toString(), newDirName, FILE_NAMES[5]);
        String commandString = String.format("cp -r \"%s\" %s", directoryPaths[1], newDirName);
        String expected = "";
        String expectedContent = Files.readString(filePaths[5]);
        command = CommandBuilder.parseCommand(commandString, appCreator);

        assertFalse(newFilePath.toFile().exists());

        command.evaluate(istream, ostream);

        String actual = ostream.toString();

        assertTrue(newDirPath.toFile().exists());
        assertTrue(newFilePath.toFile().exists());

        String actualContent = Files.readString(newFilePath);

        assertEquals(expectedContent, actualContent);
        assertEquals(expected, actual);
    }

    @Test
    void evaluateFromS19_CpSrcDirWithNoDestDir_shouldCpToNewDir() throws Exception {
        String newDirName = "dir";
        Path newDirPath = Paths.get(testRoot.toString(), newDirName);
        Path newFilePath = Paths.get(testRoot.toString(), newDirName, FILE_NAMES[5]);
        String commandString = String.format("cp -r %s %s", DIRECTORY_NAMES[1], newDirName);
        String expected = "";
        String expectedContent = Files.readString(filePaths[5]);
        command = CommandBuilder.parseCommand(commandString, appCreator);

        assertFalse(newFilePath.toFile().exists());

        command.evaluate(istream, ostream);

        String actual = ostream.toString();

        assertTrue(newDirPath.toFile().exists());
        assertTrue(newFilePath.toFile().exists());

        String actualContent = Files.readString(newFilePath);

        assertEquals(expectedContent, actualContent);
        assertEquals(expected, actual);
    }

    @Test
    void evaluateFromS10_MvWithTooManyArguments_shouldThrowError() throws Exception {
        String commandString = String.format("mv %s %s %s", FILE_NAMES[0], FILE_NAMES[1], FILE_NAMES[2]);
        command = CommandBuilder.parseCommand(commandString, appCreator);
        assertThrows(MvException.class, () -> command.evaluate(istream, ostream));
    }

    @Test
    void evaluateFromS20_CpNoSrcFileWithNoDestFile_shouldThrowError() throws Exception {
        String newFileName = "file";
        Path newFilePath = Paths.get(testRoot.toString(), newFileName);
        String commandString = String.format("cp %s %s", "nonExistent.txt", newFileName);
        String expected = "";
        String expectedContent = Files.readString(filePaths[2]);
        command = CommandBuilder.parseCommand(commandString, appCreator);

        assertFalse(newFilePath.toFile().exists());
        assertThrows(CpException.class, () -> command.evaluate(istream, ostream));
    }
}
