package sg.edu.nus.comp.cs4218.impl.app.args;

import java.util.ArrayList;
import java.util.List;

import static sg.edu.nus.comp.cs4218.impl.util.StringUtils.CHAR_FLAG_PREFIX;

public class RmArguments {
    public static final char CHAR_RECURSIVE = 'r';
    public static final char CHAR_EMPTY_DIR = 'd';
    private final List<String> files;
    private boolean recursive;
    private boolean emptyDir;

    public RmArguments() {
        this.recursive = false;
        this.emptyDir = false;
        this.files = new ArrayList<>();
    }

    /**
     * Handles argument list parsing for the `rm` application.
     *
     * @param args Array of arguments to parse
     * @throws Exception
     */
    public void parse(String... args) {
        boolean parsingFlag = true, skip = false;
        // Parse arguments
        if (args != null && args.length > 0) {
            for (String arg : args) {
                if (arg.isEmpty()) {
                    continue;
                }
                // `parsingFlag` is to ensure all flags come first, followed by files.
                if (parsingFlag && arg.charAt(0) == CHAR_FLAG_PREFIX) {
                    // Loop through to see if we have any invalid flags
                    for (char c : arg.toCharArray()) {
                        if (c == CHAR_FLAG_PREFIX || c == CHAR_RECURSIVE || c == CHAR_EMPTY_DIR) {
                            continue;
                        }
                        parsingFlag = false;
                        this.files.add(arg.trim());
                        skip = true;
                        break;//NOPMD
                    }
                    if (skip) {
                        skip = false;
                        continue;
                    }

                    for (char c : arg.toCharArray()) {
                        if (c == CHAR_FLAG_PREFIX) {
                            continue;
                        }
                        if (c == CHAR_RECURSIVE) {
                            this.recursive = true;
                        }
                        if (c == CHAR_EMPTY_DIR) {
                            this.emptyDir = true;
                        }
                    }
                } else {
                    parsingFlag = false;
                    this.files.add(arg.trim());
                }
            }
        }
    }


    public boolean isRecursive() {
        return recursive;
    }

    public boolean isEmptyDir() {
        return emptyDir;
    }

    public List<String> getFiles() {
        return files;
    }

}
