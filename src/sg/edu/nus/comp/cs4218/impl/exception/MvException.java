package sg.edu.nus.comp.cs4218.impl.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class MvException extends AbstractApplicationException {

    private static final long serialVersionUID = -4730922172179294678L;

    public MvException(String message) {
        super("mv: " + message);
    }

    public MvException(Exception e) {
        super("mv: " + e.getMessage());
    }
}