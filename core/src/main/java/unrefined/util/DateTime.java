package unrefined.util;

import unrefined.context.Environment;

import java.util.Date;

public abstract class DateTime {

    private static volatile DateTime INSTANCE;
    private static final Object INSTANCE_LOCK = new Object();
    public static DateTime getInstance() {
        if (INSTANCE == null) synchronized (INSTANCE_LOCK) {
            if (INSTANCE == null) INSTANCE = Environment.global.get("unrefined.runtime.dateTime", DateTime.class);
        }
        return INSTANCE;
    }

    public Date createDate(long millis) {
        return new Date(millis);
    }

    public Date createImmutableDate(long millis) {
        return new ImmutableDate(millis);
    }

    public Date createImmutableDate(Date date) {
        return new ImmutableDate(date);
    }

    public Timestamp createTimestamp(long millis, int nanos) {
        return new Timestamp(millis, nanos);
    }

    public Timestamp createTimestamp(Date date) {
        return new Timestamp(date);
    }

    public long nowMillis() {
        return System.currentTimeMillis();
    }

    public long nowMonotonic() {
        return System.nanoTime();
    }

    public long nowSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

    public abstract int nowNanos();

    public Timestamp nowTimestamp() {
        return new Timestamp();
    }

    public Date nowDate() {
        return new Date();
    }

    public ImmutableDate nowImmutableDate() {
        return new ImmutableDate();
    }

}
