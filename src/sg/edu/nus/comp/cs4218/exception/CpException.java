package sg.edu.nus.comp.cs4218.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class CpException extends AbstractApplicationException {

    private static final long serialVersionUID = -4730922172179294678L;

    public CpException(String message) {
        super("cp: " + message);
    }

    public CpException(Exception exception) {
        super("cp: " + exception.getMessage());
    }
}
