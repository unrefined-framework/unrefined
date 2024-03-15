package unrefined.io;

import java.io.IOException;

public class AlreadyClosedException extends IOException {

    private static final long serialVersionUID = -6528350563360938050L;

    public AlreadyClosedException() {
        super("Already closed");
    }

    public AlreadyClosedException(String message) {
        super(message);
    }

    public AlreadyClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyClosedException(Throwable cause) {
        super(cause);
    }

}
