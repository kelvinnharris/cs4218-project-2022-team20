package sg.edu.nus.comp.cs4218.app;

import sg.edu.nus.comp.cs4218.Application;

public interface MvInterface extends Application {
    /**
     * renames the file named by the source operand to the destination path named by the target operand
     *
     * @param iOverwrite Boolean option to perform overwriting 
     * @param srcFile  of path to source file
     * @param destFile of path to destination file
     * @throws Exception
     */
    void mvSrcFileToDestFile(Boolean isOverwrite, String srcFile, String destFile) throws Exception;

    /**
     * move files to destination folder
     *
     * @param iOverwrite Boolean option to perform overwriting
     * @param destFolder of path to destination folder
     * @param fileName   Array of String of file names
     * @throws Exception
     */
    void mvFilesToFolder(Boolean isOverwrite, String destFolder, String... fileName) throws Exception;
}