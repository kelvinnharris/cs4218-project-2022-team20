package sg.edu.nus.comp.cs4218.impl.app.args;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

public class CutArguments {

    public static final char CHAR_BY_CHAR_PO = 'c';
    public static final char CHAR_BY_BYTE_PO = 'b';

    private final List<String> files;
    private boolean charPo, bytePo, range;
    private int startIdx, endIdx;
    private int[] index;
    private String[] indexString;

    public CutArguments() {
        this.charPo = false;
        this.bytePo = false;
        this.range = false;
        this.startIdx = 0;
        this.endIdx = 0;
        this.files = new ArrayList<>();
    }

    /**
     * Handles argument list parsing for the `cut` application.
     *
     * @param args Array of arguments to parse
     * @throws Exception
     */
    public void parse(String... args) {
        boolean parsingFlag = true, skip = false, parsingIndex = true, firstNumber = true;
        // Parse arguments
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.isEmpty()) {
                    continue;
                }
                // parse flag
                if (i == 0) {
                    if (arg.length() == 2 && arg.charAt(1) == CHAR_BY_CHAR_PO) {
                        charPo = true;
                        bytePo = false;
                    } else if (arg.length() == 2 && arg.charAt(1) == CHAR_BY_BYTE_PO) {
                        bytePo = true;
                        charPo = false;
                    } else {
                        //error?
                    }
                    continue;
                }

                // parse index
                if (i == 1) {
                    if (arg.contains(",")) {
                        range = false;
                        indexString = arg.split(",");
                        index = new int[indexString.length];
                        for (int j = 0; j < indexString.length; j++) {
                            index[j] = Integer.parseInt(indexString[j]) - 1;
                        }
                        startIdx = index[0];
                        endIdx = index[index.length - 1] + 1;
                    } else if (arg.contains("-")) {
                        range = true;
                        indexString = arg.split("-");
                        index = new int[indexString.length];
                        for (int j = 0; j < indexString.length; j++) {
                            index[j] = Integer.parseInt(indexString[j]) - 1;
                        }
                        startIdx = index[0];
                        endIdx = index[1] + 1;
                    } else {
                        range = false;
                        index = new int[1];
                        index[0] = Integer.parseInt(arg) - 1;
                        startIdx = index[0];
                        endIdx = index[0] + 1;
                    }
                    continue;
                }

                // parse files
                if (i >= 2) {
                    this.files.add(arg.trim());
                }
            }
        }
    }

    public List<String> getFiles() {
        return files;
    }

    public boolean isCharPo() {
        return charPo;
    }

    public boolean isBytePo() {
        return bytePo;
    }

    public boolean isRange() {
        return range;
    }

    public int getStartIdx() { return startIdx; }

    public int getEndIdx() { return endIdx; }

    public int[] getIndex() { return index; }

}
