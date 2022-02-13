package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class RmArgsParser extends ArgsParser {
    private final static char CHAR_RECURSIVE = 'r';
    private final static char CHAR_EMPTY_DIR = 'd';

    public RmArgsParser() {
        super();
        legalFlags.add(CHAR_RECURSIVE);
        legalFlags.add(CHAR_EMPTY_DIR);
    }

    public boolean isRecursive() {
        return flags.contains(CHAR_RECURSIVE);
    }

    public boolean isEmptyDir() {
        return flags.contains(CHAR_EMPTY_DIR);
    }

    public List<String> getFiles() {
        return nonFlagArgs;
    }
}
