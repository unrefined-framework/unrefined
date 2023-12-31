package unrefined.io;

import unrefined.util.Cacheable;

import java.io.IOException;

public interface Bundleable extends Cacheable {

    void writeToBundle(BundleOutput out) throws IOException;
    void readFromBundle(BundleInput in) throws IOException;

}
