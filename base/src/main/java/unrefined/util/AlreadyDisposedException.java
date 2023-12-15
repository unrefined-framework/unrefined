package unrefined.util;

public class AlreadyDisposedException extends IllegalStateException {

    private static final long serialVersionUID = 4898537196798678978L;

    public AlreadyDisposedException() {
    }

    public AlreadyDisposedException(String message) {
        super(message);
    }

    public AlreadyDisposedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyDisposedException(Throwable cause) {
        super(cause);
    }

}
