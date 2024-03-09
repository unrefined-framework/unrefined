package unrefined.util;

public interface Cacheable extends Copyable, Swappable, Resettable {

    static boolean isCacheable(Object object) {
        return object instanceof Copyable && object instanceof Swappable && object instanceof Resettable;
    }

}
