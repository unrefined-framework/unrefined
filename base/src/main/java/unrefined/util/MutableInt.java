package unrefined.util;

import java.io.Serializable;

public class MutableInt implements Serializable {

    private static final long serialVersionUID = 2930278024964700158L;

    private int value;

    public void set(int newValue) {
        this.value = newValue;
    }

    public int get() {
        return value;
    }

    public MutableInt() {
    }

    public MutableInt(int initialValue) {
        this.value = initialValue;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
}
