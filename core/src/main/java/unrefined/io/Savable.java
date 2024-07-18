package unrefined.io;

import unrefined.util.Cacheable;

public interface Savable extends Portable, Bundleable, Cacheable {

    static boolean isSavable(Object object) {
        return object instanceof Portable && object instanceof Bundleable && object instanceof Cacheable;
    }

}
