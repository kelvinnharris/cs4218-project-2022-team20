package sg.edu.nus.comp.cs4218.impl.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class TestUtils {

    public static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    public static void createFile(String filePath, String fileContent) {
        File newFile = new File(filePath);

        try {
            boolean result = newFile.createNewFile();
            if (result) {
                FileWriter myWriter = new FileWriter(newFile.getCanonicalPath());
                myWriter.write(fileContent);
                myWriter.close();
            } else { // File already exists
                System.out.println("File already exist at location: "+ newFile.getCanonicalPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
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
}
