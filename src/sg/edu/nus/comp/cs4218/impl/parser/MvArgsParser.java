package sg.edu.nus.comp.cs4218.impl.parser;

public class MvArgsParser extends ArgsParser {
    private final static char FLAG_IS_NOT_OVERWRITE = 'n'; // NOPMD - suppressed LongVariable - For consistency and clarity

    public MvArgsParser() {
        super();
        legalFlags.add(FLAG_IS_NOT_OVERWRITE);
    }

    public Boolean isOverWrite() {
        return !flags.contains(FLAG_IS_NOT_OVERWRITE);
    }

    public String[] getSourceFiles() {
        int len = nonFlagArgs.size() - 1;
        String[] srcFiles = new String[len];
        for (int i = 0; i < len; i++) {
            srcFiles[i] = nonFlagArgs.get(i);
        }
        return srcFiles;
    }

    public String getDestinationFile() {
        return nonFlagArgs.get(nonFlagArgs.size() - 1);
    }
}
