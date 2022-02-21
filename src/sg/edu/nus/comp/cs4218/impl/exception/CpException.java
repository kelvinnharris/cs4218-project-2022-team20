package sg.edu.nus.comp.cs4218.impl.exception;

import sg.edu.nus.comp.cs4218.exception.AbstractApplicationException;

public class CpException extends AbstractApplicationException {

    private static final long serialVersionUID = -4730922172179294678L; // what is this ya

    public CpException(String message) {
        super("cp: " + message);
    }
}
