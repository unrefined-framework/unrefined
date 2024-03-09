package unrefined.util;

import java.io.Serializable;

public class MutableShort implements Serializable {

    private static final long serialVersionUID = 7707957763287551459L;

    private short value;

    public void set(short newValue) {
        this.value = newValue;
    }

    public short get() {
        return value;
    }

    public MutableShort() {
    }

    public MutableShort(short initialValue) {
        this.value = initialValue;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
}
