package unrefined.io;

import java.io.InputStream;

public abstract class OffsetCountInputStream extends InputStream {

    protected long offset = 0;

    public long offset() {
        return offset;
    }

}
