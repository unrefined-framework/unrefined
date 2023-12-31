package unrefined.io;

import unrefined.util.function.BiSlot;
import unrefined.util.function.FunctionTargetException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;

public class BinaryOutputStream extends DataOutputStream implements BinaryOutput {

    public BinaryOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void writePortable(Portable obj) throws IOException {
        obj.writePortable(this);
    }

    @Override
    public <T> void writeObject(T obj, BiSlot<T, BinaryOutput> writeProc) throws IOException {
        try {
            writeProc.accept(obj, this);
        }
        catch (FunctionTargetException e) {
            if (e.getTargetException() instanceof IOException) throw (IOException) e.getTargetException();
        }
    }

    @Override
    public void writeUnsignedByte(int v) throws IOException {
        writeByte(v);
    }

    @Override
    public void writeUnsignedShort(int v) throws IOException {
        writeShort(v);
    }

    @Override
    public void writeUnsignedInt(long v) throws IOException {
        writeInt((int) v);
    }

    @Override
    public void writeUnsignedLong(BigInteger v) throws IOException {
        writeLong(v.longValue());
    }

}
