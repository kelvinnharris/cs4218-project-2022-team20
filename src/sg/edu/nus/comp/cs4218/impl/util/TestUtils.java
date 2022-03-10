package sg.edu.nus.comp.cs4218.impl.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.APPEND;
import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.STRING_NEWLINE;

public class TestUtils { // NOPMD

    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    public static void createFile(String filePath, String fileContent) throws IOException {
        File newFile = new File(filePath);
        FileWriter myWriter = null;

        try {
            boolean result = newFile.createNewFile();
            if (result) {
                myWriter = new FileWriter(newFile.getCanonicalPath());
                myWriter.write(fileContent);
            } else { // File already exists
                System.out.println("File already exist at location: " + newFile.getCanonicalPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (myWriter != null) {
                myWriter.close();
            }
        }
    }

    public static void deleteFile(String filePath) {
        File newFile = new File(filePath);

        try {
            boolean result = Files.deleteIfExists(newFile.toPath());
        } catch (IOException e) {
            System.out.println("Failed to delete file");
        }
    }

    public static void appendToFile(Path file, String... lines) throws IOException {
        for (String line : lines) {
            Files.write(file, (line + STRING_NEWLINE).getBytes(), APPEND);
        }
    }
  
    public static boolean isWindowsSystem() {
        return System.getProperty("os.name").toLowerCase().contains("win"); // NOPMD
    }
}
