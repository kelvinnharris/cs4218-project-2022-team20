package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class UniqArgsParser extends ArgsParser {
    private final static char CHAR_COUNT = 'c';
    private final static char CHAR_REPEATED = 'd';
    private final static char CHAR_ALL_REPEATED = 'D';

    public UniqArgsParser() {
        super();
        legalFlags.add(CHAR_COUNT);
        legalFlags.add(CHAR_REPEATED);
        legalFlags.add(CHAR_ALL_REPEATED);
    }

    public List<String> getFiles() {
        return nonFlagArgs;
    }

    public String getInputFile() {
        if (nonFlagArgs.size() == 0) {
            return null;
        }
        return nonFlagArgs.get(0);
    }

    public String getOutputFile() {
        if (nonFlagArgs.size() <= 1) {
            return null;
        }
        return nonFlagArgs.get(1);
    }

    public boolean isCount() {
        return flags.contains(CHAR_COUNT);
    }

    public boolean isRepeated() {
        return flags.contains(CHAR_REPEATED);
    }

    public boolean isAllRepeated() {
        return flags.contains(CHAR_ALL_REPEATED);
    }
}
