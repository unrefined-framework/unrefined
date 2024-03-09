package unrefined.io;

import unrefined.util.function.BiSlot;

import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;

public interface BinaryOutput extends DataOutput {

    void writePortable(Portable obj) throws IOException;
    <T> void writeObject(T obj, BiSlot<T, BinaryOutput> writeProc) throws IOException;

    void writeUnsignedByte(int v) throws IOException;
    void writeUnsignedShort(int v) throws IOException;
    void writeUnsignedInt(long v) throws IOException;
    void writeUnsignedLong(BigInteger v) throws IOException;
    void writeString(String text, Charset charset) throws IOException;
    default void writeString(String text) throws IOException {
        writeString(text, Charset.defaultCharset());
    }

}
