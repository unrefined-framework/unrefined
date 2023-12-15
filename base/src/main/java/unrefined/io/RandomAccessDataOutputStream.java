package unrefined.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class RandomAccessDataOutputStream extends OutputStream implements RandomAccessDataOutput {

    protected final RandomAccessDataOutput output;

    public RandomAccessDataOutputStream(RandomAccessDataOutput output) {
        this.output = Objects.requireNonNull(output);
    }

    @Override
    public void write(int b) throws IOException {
        output.write(b);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        output.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        output.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        output.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        output.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        output.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        output.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        output.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        output.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        output.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        output.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        output.writeUTF(s);
    }

    @Override
    public long length() throws IOException {
        return output.length();
    }

    @Override
    public void seek(long pos) throws IOException {
        output.seek(pos);
    }

    @Override
    public long position() throws IOException {
        return output.position();
    }

    @Override
    public void setLength(long newLength) throws IOException {
        output.setLength(newLength);
    }

    @Override
    public void write(byte[] b) throws IOException {
        output.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        output.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void close() throws IOException {
        output.close();
    }

}
