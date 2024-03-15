package unrefined.util;

public class NotInstantiableError extends AssertionError {

    private static final long serialVersionUID = -4931611382965558909L;

    public NotInstantiableError() {
        this((String) null);
    }

    public NotInstantiableError(String message) {
        super((message == null || message.isEmpty()) ? "This class is not instantiable" : "No " + message + " instances for you");
    }

    public NotInstantiableError(Class<?> clazz) {
        this(clazz == null ? null : clazz.getName());
    }

}
