package unrefined.io;

import unrefined.internal.NumberUtils;
import unrefined.util.function.BiSlot;
import unrefined.util.function.FunctionTargetException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class PortableInputStream extends DataInputStream implements PortableInput {

    public PortableInputStream(InputStream in) {
        super(in);
    }

    @Override
    public void readPortable(Portable obj) throws IOException {
        obj.readPortable(this);
    }

    @Override
    public <T> void readObject(T obj, BiSlot<T, PortableInput> readProc) throws IOException {
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
        return NumberUtils.toUnsignedBigInteger(readLong());
    }

}
