package unrefined.util;

import java.io.Serializable;

public class MutableDouble implements Serializable {

    private static final long serialVersionUID = -8797929943338364003L;

    private double value;

    public void set(double newValue) {
        this.value = newValue;
    }

    public double get() {
        return value;
    }

    public MutableDouble() {
    }

    public MutableDouble(double initialValue) {
        this.value = initialValue;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
}
