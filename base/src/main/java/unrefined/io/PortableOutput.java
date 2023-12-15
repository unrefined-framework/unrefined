package unrefined.io;

import unrefined.util.function.BiSlot;

import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;

public interface PortableOutput extends DataOutput {

    void writePortable(Portable obj) throws IOException;
    <T> void writeObject(T obj, BiSlot<T, PortableOutput> writeProc) throws IOException;

    void writeUnsignedByte(int v) throws IOException;
    void writeUnsignedShort(int v) throws IOException;
    void writeUnsignedInt(long v) throws IOException;
    void writeUnsignedLong(BigInteger v) throws IOException;

}
