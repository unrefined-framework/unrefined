package unrefined.io;

import unrefined.util.function.BiSlot;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigInteger;

public interface BinaryInput extends DataInput {

    void readPortable(Portable obj) throws IOException;
    <T> void readObject(T obj, BiSlot<T, BinaryInput> readProc) throws IOException;

    long readUnsignedInt() throws IOException;
    BigInteger readUnsignedLong() throws IOException;

}
