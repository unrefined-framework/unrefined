package unrefined.util;

import java.util.Date;

public class ImmutableDate extends Date {

    private static final long serialVersionUID = 5582468959193135299L;

    public ImmutableDate() {
        super();
    }

    public ImmutableDate(long date) {
        super(date);
    }

    public ImmutableDate(Date date) {
        super(date == null ? System.currentTimeMillis() : date.getTime());
    }

    @Deprecated
    public ImmutableDate(int year, int month, int date) {
        super(year, month, date);
    }

    @Deprecated
    public ImmutableDate(int year, int month, int date, int hrs, int min) {
        super(year, month, date, hrs, min);
    }

    @Deprecated
    public ImmutableDate(int year, int month, int date, int hrs, int min, int sec) {
        super(year, month, date, hrs, min, sec);
    }

    @Deprecated
    public ImmutableDate(String s) {
        super(s);
    }

    @Deprecated
    @Override
    public void setYear(int year) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setMonth(int month) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setDate(int date) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setHours(int hours) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setMinutes(int minutes) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void setSeconds(int seconds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTime(long time) {
        throw new UnsupportedOperationException();
    }

}
