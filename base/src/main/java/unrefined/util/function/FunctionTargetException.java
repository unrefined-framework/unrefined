package unrefined.util.function;

public class FunctionTargetException extends RuntimeException {

    private static final long serialVersionUID = 2158316205055777151L;

    private final Throwable target;

    protected FunctionTargetException() {
        super((Throwable) null);  // Disallow initCause
        this.target = null;
    }

    public FunctionTargetException(Throwable target) {
        super((Throwable) null);  // Disallow initCause
        this.target = target;
    }

    public FunctionTargetException(Throwable target, String message) {
        super(message, null);  // Disallow initCause
        this.target = target;
    }

    public Throwable getTargetException() {
        return target;
    }

    @Override
    public Throwable getCause() {
        return target;
    }

}
