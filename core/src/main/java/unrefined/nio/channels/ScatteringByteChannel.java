package unrefined.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public interface ScatteringByteChannel extends ReadableByteChannel {

    long read(ByteBuffer[] dsts, int offset, int length) throws IOException;
    long read(ByteBuffer[] dsts) throws IOException;

}
