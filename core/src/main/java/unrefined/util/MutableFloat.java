package unrefined.util;

import java.io.Serializable;

public class MutableFloat implements Serializable {

    private static final long serialVersionUID = 6833098626943530127L;
    
    private float value;

    public void set(float newValue) {
        this.value = newValue;
    }

    public float get() {
        return value;
    }

    public MutableFloat() {
    }

    public MutableFloat(float initialValue) {
        this.value = initialValue;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
}
