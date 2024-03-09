package unrefined.util;

import java.io.Serializable;

public class MutableBoolean implements Serializable {

    private static final long serialVersionUID = 2617534589449051928L;

    private boolean value;

    public void set(boolean newValue) {
        this.value = newValue;
    }

    public boolean get() {
        return value;
    }

    public MutableBoolean() {
    }

    public MutableBoolean(boolean initialValue) {
        this.value = initialValue;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
