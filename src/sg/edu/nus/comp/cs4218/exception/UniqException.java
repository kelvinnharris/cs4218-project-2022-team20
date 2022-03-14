package sg.edu.nus.comp.cs4218.exception;

public class UniqException extends AbstractApplicationException {

    private static final long serialVersionUID = 1894758187716957490L;

    public UniqException(String message) {
        super("uniq: " + message);
    }
    public UniqException(Exception exception, String message) {
        super("uniq: " + message);
    }
}
