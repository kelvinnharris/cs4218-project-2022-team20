package sg.edu.nus.comp.cs4218.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class MvException extends AbstractApplicationException {

    private static final long serialVersionUID = -4730922172179294678L;

    public MvException(String message) {
        super("mv: " + message);
    }

    public MvException(Exception exception) {
        super("mv: " + exception.getMessage());
    }
}
