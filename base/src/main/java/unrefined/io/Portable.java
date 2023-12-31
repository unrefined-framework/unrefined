package unrefined.io;

import java.io.IOException;

public interface Portable {

    void writePortable(BinaryOutput out) throws IOException;
    void readPortable(BinaryInput in) throws IOException;

}
