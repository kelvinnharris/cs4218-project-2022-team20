package sg.edu.nus.comp.cs4218.impl.parser;

public class TeeArgsParser extends ArgsParser {
    private final static char FLAG_IS_APPEND = 'a';

    public TeeArgsParser() {
        super();
        legalFlags.add(FLAG_IS_APPEND);
    }

    public Boolean isAppend() {
        return flags.contains(FLAG_IS_APPEND);
    }

    public String[] getFiles() {
        int len = nonFlagArgs.size();
        String[] files = new String[len];
        for (int i = 0; i < len; i++) {
            files[i] = nonFlagArgs.get(i);
        }
        return files;
    }
}
