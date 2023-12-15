package unrefined.io;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;

public interface RandomAccessDataOutput extends DataOutput, Closeable {

    void write(int b) throws IOException;
    void write(byte[] b) throws IOException;
    void write(byte[] b, int off, int len) throws IOException;

    long length() throws IOException;
    void seek(long pos) throws IOException;
    long position() throws IOException;
    void setLength(long newLength) throws IOException;
    void flush() throws IOException;

}
