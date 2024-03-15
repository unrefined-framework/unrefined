package unrefined.util;

import java.io.Serializable;

public class MutableChar implements Serializable {

    private static final long serialVersionUID = -5920221401984769463L;

    private char value;

    public void set(char newValue) {
        this.value = newValue;
    }

    public char get() {
        return value;
    }

    public MutableChar() {
    }

    public MutableChar(char initialValue) {
        this.value = initialValue;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
}
