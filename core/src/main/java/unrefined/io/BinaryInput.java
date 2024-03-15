package unrefined.io;

import unrefined.util.function.BiSlot;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;

public interface BinaryInput extends DataInput {

    void readPortable(Portable obj) throws IOException;
    <T> void readObject(T obj, BiSlot<T, BinaryInput> readProc) throws IOException;

    long readUnsignedInt() throws IOException;
    BigInteger readUnsignedLong() throws IOException;
    String readString(Charset charset) throws IOException;
    default String readString() throws IOException {
        return readString(Charset.defaultCharset());
    }
    default String readBytes(int length) throws IOException {
        char[] chars = new char[length];
        for (int i = 0; i < length; i ++) {
            chars[i] = (char) readByte();
        }
        return new String(chars);
    }
    default String readChars(int length) throws IOException {
        char[] chars = new char[length];
        for (int i = 0; i < length; i ++) {
            chars[i] = readChar();
        }
        return new String(chars);
    }

}
