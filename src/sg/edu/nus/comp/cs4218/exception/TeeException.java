package sg.edu.nus.comp.cs4218.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class TeeException extends AbstractApplicationException {

    private static final long serialVersionUID = -4730922172179294678L;

    public TeeException(String message) {
        super("tee: " + message);
    }

    public TeeException(Exception exception) {
        super("tee: " + exception.getMessage());
    }
}
