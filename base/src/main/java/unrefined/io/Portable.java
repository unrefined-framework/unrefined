package unrefined.io;

import java.io.IOException;

public interface Portable {

    void writePortable(PortableOutput out) throws IOException;
    void readPortable(PortableInput in) throws IOException;

}
