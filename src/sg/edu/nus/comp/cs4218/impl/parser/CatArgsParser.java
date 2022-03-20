package sg.edu.nus.comp.cs4218.impl.parser;

import java.util.List;

public class CatArgsParser extends ArgsParser {
    private final static char FLAG_SHOW_NUMBER = 'n';

    public CatArgsParser() {
        super();
        legalFlags.add(FLAG_SHOW_NUMBER);
    }

    public Boolean isFlagNumber() {
        return flags.contains(FLAG_SHOW_NUMBER);
    }

    public List<String> getFiles() {
        return nonFlagArgs;
    }
}
