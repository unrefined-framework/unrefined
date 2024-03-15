package unrefined.util;

import java.io.Serializable;

public class MutableByte implements Serializable {

    private static final long serialVersionUID = 871714895055343867L;

    private byte value;

    public void set(byte newValue) {
        this.value = newValue;
    }

    public byte get() {
        return value;
    }

    public MutableByte() {
    }

    public MutableByte(byte initialValue) {
        this.value = initialValue;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
}
