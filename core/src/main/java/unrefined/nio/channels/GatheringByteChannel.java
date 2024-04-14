package unrefined.nio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;


public interface GatheringByteChannel extends WritableByteChannel {

    long write(ByteBuffer[] srcs, int offset, int length) throws IOException;
    long write(ByteBuffer[] srcs) throws IOException;

}
