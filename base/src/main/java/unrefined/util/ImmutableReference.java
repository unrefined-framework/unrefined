package unrefined.util;

import java.io.Serializable;

public class ImmutableReference<T> implements Serializable {

    private static final long serialVersionUID = -3880957935958102045L;

    private final T value;

    public T get() {
        return value;
    }

    public ImmutableReference(T value) {
        this.value = value;
    }

}
