package unrefined.runtime;

import unrefined.util.DateTime;
import unrefined.util.Timestamp;

import java.time.Instant;

public class DesktopDateTime extends DateTime {

    @Override
    public long nowSeconds() {
        return Instant.now().getEpochSecond();
    }

    @Override
    public int nowNanos() {
        return Instant.now().getNano();
    }

    @Override
    public Timestamp nowTimestamp() {
        Instant instant = Instant.now();
        return new Timestamp(instant.getEpochSecond(), instant.getNano());
    }

}
