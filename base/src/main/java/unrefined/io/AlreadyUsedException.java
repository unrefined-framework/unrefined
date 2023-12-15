package unrefined.io;

import java.io.IOException;

public class AlreadyUsedException extends IOException {

    private static final long serialVersionUID = 9126243530325685788L;

    public AlreadyUsedException() {
        super("Already used");
    }

    public AlreadyUsedException(String message) {
        super(message);
    }

    public AlreadyUsedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyUsedException(Throwable cause) {
        super(cause);
    }
    
}
