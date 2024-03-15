package unrefined.io;

import java.io.IOException;

public interface Bundleable {

    void writeToBundle(BundleOutput out) throws IOException;
    void readFromBundle(BundleInput in) throws IOException;

}
