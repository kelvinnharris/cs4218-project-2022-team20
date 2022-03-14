package sg.edu.nus.comp.cs4218.app;

import javafx.util.Pair;
import sg.edu.nus.comp.cs4218.Application;

import java.io.InputStream;
import java.util.List;

public interface CutInterface extends Application {

    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param ranges   list of pairs containing the start and end indeces for cut
     * @param fileName Array of String of file names
     * @return
     * @throws Exception
     */
    String cutFromFiles(Boolean isCharPo, Boolean isBytePo,
                        List<Pair<Integer, Integer>> ranges, String... fileName) throws Exception;


    /**
     * Cuts out selected portions of each line
     *
     * @param isCharPo Boolean option to cut by character position
     * @param isBytePo Boolean option to cut by byte position
     * @param ranges   list of pairs containing the start and end indeces for cut
     * @param stdin    InputStream containing arguments from Stdin
     * @return
     * @throws Exception
     */
    String cutFromStdin(Boolean isCharPo, Boolean isBytePo,
                        List<Pair<Integer, Integer>> ranges, InputStream stdin) throws Exception;
}
