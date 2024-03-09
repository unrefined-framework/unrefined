package unrefined.util;

import java.io.Serializable;

public class MutableReference<T> implements Serializable {

    private static final long serialVersionUID = 90884134363562400L;

    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public MutableReference() {
    }

    public MutableReference(T initialValue) {
        this.value = initialValue;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
