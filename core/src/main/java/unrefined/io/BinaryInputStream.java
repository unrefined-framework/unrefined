package unrefined.io;

import unrefined.math.FastMath;
import unrefined.util.GrowableByteArray;
import unrefined.util.function.BiSlot;
import unrefined.util.function.FunctionTargetException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;

public class BinaryInputStream extends DataInputStream implements BinaryInput {

    public BinaryInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void readPortable(Portable obj) throws IOException {
        obj.readPortable(this);
    }

    @Override
    public <T> void readObject(T obj, BiSlot<T, BinaryInput> readProc) throws IOException {
        try {
            readProc.accept(obj, this);
        }
        catch (FunctionTargetException e) {
            if (e.getTargetException() instanceof IOException) throw (IOException) e.getTargetException();
        }
    }

    @Override
    public long readUnsignedInt() throws IOException {
        return Integer.toUnsignedLong(readInt());
    }

    @Override
    public BigInteger readUnsignedLong() throws IOException {
        return FastMath.unsign(readLong());
    }

    @Override
    public String readString(Charset charset) throws IOException {
        if (charset == null) charset = Charset.defaultCharset();
        GrowableByteArray bytes = new GrowableByteArray();
        byte[] terminator = "\0".getBytes(charset);
        for (int i = 0; i < terminator.length; i ++) {
            bytes.add(readByte());
        }
        int index = 0;
        outerLoop:
        while (true) {
            for (int i = 0; i < terminator.length; i ++) {
                if (bytes.get(index + i) != terminator[i]) {
                    bytes.add(readByte());
                    index ++;
                    continue outerLoop;
                }
            }
            return bytes.asString(0, bytes.size() - terminator.length, charset);
        }
    }

}
