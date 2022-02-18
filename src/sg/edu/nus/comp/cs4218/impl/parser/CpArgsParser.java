package sg.edu.nus.comp.cs4218.impl.parser;

public class CpArgsParser extends ArgsParser {
    private final static char FLAG_IS_RECURSIVE = 'r';
    private final static char FLAG_IS_RECURSIVE_UPPERCASE = 'R';

    public CpArgsParser() {
        super();
        legalFlags.add(FLAG_IS_RECURSIVE);
        legalFlags.add(FLAG_IS_RECURSIVE_UPPERCASE);
    }

    public Boolean isRecursive() {
        return flags.contains(FLAG_IS_RECURSIVE) || flags.contains(FLAG_IS_RECURSIVE_UPPERCASE);
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
