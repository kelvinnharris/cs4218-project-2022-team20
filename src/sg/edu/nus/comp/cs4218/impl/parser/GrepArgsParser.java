package sg.edu.nus.comp.cs4218.impl.parser;

import sg.edu.nus.comp.cs4218.exception.InvalidArgsException;

public class GrepArgsParser extends ArgsParser {
    private final static char FLAG_IS_CASE_INSENSITIVE = 'i';
    private final static char FLAG_IS_COUNT_ONLY = 'c';
    private final static char FLAG_IS_PRINT_FILENAME = 'H';

    public GrepArgsParser() {
        super();
        legalFlags.add(FLAG_IS_CASE_INSENSITIVE);
        legalFlags.add(FLAG_IS_COUNT_ONLY);
        legalFlags.add(FLAG_IS_PRINT_FILENAME);
    }

    public Boolean isCaseInsensitive() {
        return flags.contains(FLAG_IS_CASE_INSENSITIVE);
    }

    public Boolean isCountOnly() {
        return flags.contains(FLAG_IS_COUNT_ONLY);
    }

    public Boolean isPrintFilename() {
        return flags.contains(FLAG_IS_PRINT_FILENAME);
    }

    public String getPattern() {
        return nonFlagArgs.isEmpty() ? null : nonFlagArgs.get(0);
    }

    public String[] getFileNames() {
        return nonFlagArgs.size() <= 1 ? null : nonFlagArgs.subList(1, nonFlagArgs.size())
                .toArray(new String[0]);
    }
}
