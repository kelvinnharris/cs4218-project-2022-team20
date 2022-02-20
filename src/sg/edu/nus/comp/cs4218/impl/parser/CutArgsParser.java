package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.ArrayList;
import java.util.List;

public class CutArgsParser extends ArgsParser {

    private final static char CHAR_BY_CHAR_PO = 'c';
    private final static char CHAR_BY_BYTE_PO = 'b';


    private boolean range;
    private int startIdx, endIdx;
    private int[] index;
    private String[] indexString;

    public CutArgsParser() {
        super();
        legalFlags.add(CHAR_BY_CHAR_PO);
        legalFlags.add(CHAR_BY_BYTE_PO);
        this.range = false;
        this.startIdx = 0;
        this.endIdx = 0;
    }

    public boolean isCharPo() {
        return flags.contains(CHAR_BY_CHAR_PO);
    }

    public boolean isBytePo() {
        return flags.contains(CHAR_BY_BYTE_PO);
    }

    public List<String> getFiles() {
        List<String> files = new ArrayList<>();
        for (int i = 1; i < nonFlagArgs.size(); i++) {
            files.add(nonFlagArgs.get(i));
        }
        return files;
    }

    public void parseIndex() throws InvalidArgsException {
        try {
            String arg = nonFlagArgs.get(0);
            if (arg.contains(",")) {
                range = false;
                indexString = arg.split(",");
                index = new int[indexString.length];
                for (int j = 0; j < indexString.length; j++) {
                    index[j] = Integer.parseInt(indexString[j]) - 1;
                }
                startIdx = index[0];
                endIdx = index[index.length - 1];
            } else if (arg.contains("-")) {
                range = true;
                indexString = arg.split("-");
                index = new int[indexString.length];
                for (int j = 0; j < indexString.length; j++) {
                    index[j] = Integer.parseInt(indexString[j]) - 1;
                }
                startIdx = index[0];
                endIdx = index[1];
            } else {
                range = false;
                index = new int[1];
                index[0] = Integer.parseInt(arg) - 1;
                startIdx = index[0];
                endIdx = index[0];
            }
        } catch (Exception e) {
            throw new InvalidArgsException(e.getMessage());
        }
    }

    public boolean isRange() {
        return range;
    }

    public int getStartIdx() { return startIdx; }

    public int getEndIdx() { return endIdx; }

    public int[] getIndex() { return index; }

}
