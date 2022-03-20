package sg.edu.nus.comp.cs4218.exception;

public class CutException extends AbstractApplicationException {

    private static final long serialVersionUID = 3894758187716957491L;
    public static final String INVALID_CMD = "Invalid command code.";
    public static final String PROB_CUT_FILE = "Problem cut from file: ";
    public static final String PROB_CUT_STDIN = "Problem cut from stdin: ";

    public CutException(String message) {
        super("cut: " + message);
    }

    public CutException(Exception exception, String message) {
        super("cut: " + message);
    }
}
