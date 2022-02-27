package sg.edu.nus.comp.cs4218.impl.parser;

public class GrepArgsParser extends ArgsParser {
    private final static char FLAG_CASE_INSEN = 'i'; // case insensitive
    private final static char FLAG_COUNT_ONLY = 'c';
    private final static char FLAG_IS_PRINT = 'H'; // print file name

    public GrepArgsParser() {
        super();
        legalFlags.add(FLAG_CASE_INSEN);
        legalFlags.add(FLAG_COUNT_ONLY);
        legalFlags.add(FLAG_IS_PRINT);
    }

    public Boolean isCaseInsensitive() {
        return flags.contains(FLAG_CASE_INSEN);
    }

    public Boolean isCountOnly() {
        return flags.contains(FLAG_COUNT_ONLY);
    }

    public Boolean isPrintFilename() {
        return flags.contains(FLAG_IS_PRINT);
    }

    public String getPattern() {
        return nonFlagArgs.isEmpty() ? null : nonFlagArgs.get(0);
    }

    public String[] getFileNames() {
        return nonFlagArgs.size() <= 1 ? null : nonFlagArgs.subList(1, nonFlagArgs.size())
                .toArray(new String[0]);
    }
}
