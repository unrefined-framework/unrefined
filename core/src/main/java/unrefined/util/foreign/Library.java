package unrefined.util.foreign;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Library {

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.PARAMETER, ElementType.METHOD})
    @interface Marshal {
        String value();
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface Options {
        int value();
    }

    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    @interface Redirect {
        String value();
    }

}
