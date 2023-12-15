package unrefined.util;

public class UnexpectedError extends Error {

    private static final long serialVersionUID = -7313961951503724715L;

    public UnexpectedError() {
    }

    public UnexpectedError(String message) {
        super(message);
    }

    public UnexpectedError(Throwable cause) {
        super(cause);
    }

}
