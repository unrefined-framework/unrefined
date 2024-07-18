package unrefined.util;

import java.io.Serializable;
import java.util.Date;

public class Timestamp implements Serializable, Cloneable, Comparable<Timestamp> {

    public static final Timestamp EPOCH = new Timestamp(0, 0);
    public static final Timestamp MIN = new Timestamp(-31557014167219200L, 0);
    public static final Timestamp MAX = new Timestamp(31556889864403199L, 999_999_999);

    private static final long serialVersionUID = -2385148509521130156L;

    private final long seconds;
    private final int nanos;

    public Timestamp(long seconds, int nanos) {
        this.seconds = seconds;
        this.nanos = nanos;
    }

    public Timestamp(long millis) {
        this(millis / 1000L, (int) (millis % 1000L) * 1000);
    }

    public Timestamp(Date date) {
        this(date == null ? System.currentTimeMillis() : date.getTime(), 0);
    }

    public Timestamp() {
        this(DateTime.getInstance().nowSeconds(), DateTime.getInstance().nowNanos());
    }

    public long seconds() {
        return seconds;
    }

    public int nanos() {
        return nanos;
    }

    public boolean isBefore(Timestamp timestamp) {
        return (seconds < timestamp.seconds) || (seconds == timestamp.seconds && nanos < timestamp.nanos);
    }

    public boolean isAfter(Timestamp timestamp) {
        return (seconds > timestamp.seconds) || (seconds == timestamp.seconds && nanos > timestamp.nanos);
    }

    @Override
    public int compareTo(Timestamp o) {
        return equals(o) ? 0 : (isBefore(o) ? -1 : 1);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Timestamp)) return false;

        Timestamp timestamp = (Timestamp) object;

        if (seconds != timestamp.seconds) return false;
        return nanos == timestamp.nanos;
    }

    @Override
    public int hashCode() {
        int result = (int) (seconds ^ (seconds >>> 32));
        result = 31 * result + nanos;
        return result;
    }

    @Override
    public Timestamp clone() {
        try {
            return (Timestamp) super.clone();
        }
        catch (CloneNotSupportedException e) {
            return new Timestamp(seconds, nanos);
        }
    }

}
