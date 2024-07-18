package unrefined.desktop;

import unrefined.util.NotInstantiableError;
import unrefined.util.Timestamp;

import java.time.Instant;

public final class DateTimeSupport {

    private DateTimeSupport() {
        throw new NotInstantiableError(DateTimeSupport.class);
    }

    public static Instant toInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.seconds(), timestamp.nanos());
    }

    public static Timestamp toTimestamp(Instant instant) {
        return new Timestamp(instant.getEpochSecond(), instant.getNano());
    }

}
