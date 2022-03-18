package sg.edu.nus.comp.cs4218.impl.parser;

import javafx.util.Pair;
import sg.edu.nus.comp.cs4218.exception.CutException;
import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

import java.util.ArrayList;
import java.util.List;

public class CutArgsParser extends ArgsParser {

    private final static char CHAR_BY_CHAR_PO = 'c';
    private final static char CHAR_BY_BYTE_PO = 'b';

    List<Pair<Integer, Integer>> ranges;
    private int startIdx, endIdx;
    private String[] indexString;

    public CutArgsParser() {
        super();
        legalFlags.add(CHAR_BY_CHAR_PO);
        legalFlags.add(CHAR_BY_BYTE_PO);
        ranges = new ArrayList<>();
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
        String currString = null;
        String[] tempIndexString;
        try {
            String arg = nonFlagArgs.get(0);

            if (arg.contains(",")) {
                indexString = arg.split(",");
                for (int j = 0; j < indexString.length; j++) {
                    Pair<Integer, Integer> pair;
                    currString = indexString[j];
                    long count = currString.chars().filter(ch -> ch == '-').count();
                    if (count == 1) {
                        tempIndexString = currString.split("-");
                        startIdx = Integer.parseInt(tempIndexString[0]) - 1;
                        endIdx = Integer.parseInt(tempIndexString[1]) - 1;
                    } else if (count == 0){
                        startIdx = Integer.parseInt(currString) - 1;
                        endIdx = startIdx;
                    } else {
                        throw new CutException("invalid byte, character or field list");
                    }
                    pair = new Pair<>(startIdx, endIdx);
                    ranges.add(pair);
                }
            } else if (arg.contains("-")) {
                indexString = arg.split("-");
                long count = arg.chars().filter(ch -> ch == '-').count();
                if (count == 1) {
                    startIdx = Integer.parseInt(indexString[0]) - 1;
                    endIdx = Integer.parseInt(indexString[1]) - 1;
                } else {
                    throw new CutException("invalid byte, character or field list");
                }
                Pair<Integer, Integer> pair = new Pair<>(startIdx, endIdx);
                ranges.add(pair);
            } else {
                startIdx = Integer.parseInt(arg) - 1;
                endIdx = startIdx;
                Pair<Integer, Integer> pair = new Pair<>(startIdx, endIdx);
                ranges.add(pair);
            }
        } catch (IndexOutOfBoundsException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidArgsException(e.getMessage());//NOPMD - suppressed PreserveStackTrace - No reason to preserve stackTrace as reason is contained in message
        }
    }

    public List<Pair<Integer, Integer>> getRanges() {
        return ranges;
    }

}
