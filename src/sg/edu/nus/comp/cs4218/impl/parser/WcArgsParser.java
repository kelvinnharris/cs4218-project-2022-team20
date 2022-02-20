package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class WcArgsParser extends ArgsParser{
    private final static char FLAG_LINES = 'l';
    private final static char FLAG_BYTES = 'c';
    private final static char FLAG_WORDS = 'w';

    public WcArgsParser() {
        super();
        legalFlags.add(FLAG_LINES);
        legalFlags.add(FLAG_BYTES);
        legalFlags.add(FLAG_WORDS);
    }

    public boolean isLines() {
        if (flags.isEmpty()) {
            return true;
        }
        return flags.contains(FLAG_LINES);
    }

    public boolean isWords() {
        if (flags.isEmpty()) {
            return true;
        }
        return flags.contains(FLAG_WORDS);
    }

    public boolean isBytes() {
        if (flags.isEmpty()) {
            return true;
        }
        return flags.contains(FLAG_BYTES);
    }

    public List<String> getFiles() {
        return nonFlagArgs;
    }
}
