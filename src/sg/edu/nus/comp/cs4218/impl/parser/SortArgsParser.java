package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class SortArgsParser extends ArgsParser {
    private final static char CHAR_FIRST_W_NUM = 'n';
    private final static char CHAR_REV_ORDER = 'r';
    private final static char CHAR_CASE_IGNORE = 'f';

    public SortArgsParser() {
        super();
        legalFlags.add(CHAR_FIRST_W_NUM);
        legalFlags.add(CHAR_REV_ORDER);
        legalFlags.add(CHAR_CASE_IGNORE);
    }

    public List<String> getFiles() {
        return nonFlagArgs;
    }

    public boolean isFirstWordNumber() {
        return flags.contains(CHAR_FIRST_W_NUM);
    }

    public boolean isReverseOrder() {
        return flags.contains(CHAR_REV_ORDER);
    }

    public boolean isCaseIndependent() {
        return flags.contains(CHAR_CASE_IGNORE);
    }
}
