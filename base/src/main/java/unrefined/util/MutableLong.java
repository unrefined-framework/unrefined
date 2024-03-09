package unrefined.util;

import java.io.Serializable;

public class MutableLong implements Serializable {

    private static final long serialVersionUID = 4671709440590868285L;

    private long value;

    public void set(long newValue) {
        this.value = newValue;
    }

    public long get() {
        return value;
    }

    public MutableLong() {
    }

    public MutableLong(long initialValue) {
        this.value = initialValue;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
    
}
