package unrefined.io;

import unrefined.util.function.BiSlot;
import unrefined.util.function.FunctionTargetException;

import java.io.BufferedWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class BundleOutputStream extends FilterOutputStream implements BundleOutput {

    private final BufferedWriter writer;
    private final Deque<String> prefixes = new ArrayDeque<>();

    /**
     * Creates an output stream filter built on top of the specified
     * underlying output stream.
     *
     * @param out the underlying output stream to be assigned to
     *            the field {@code this.out} for later use, or
     *            {@code null} if this instance is to be
     *            created without an underlying stream.
     */
    public BundleOutputStream(OutputStream out, Charset charset) {
        super(out);
        writer = new BufferedWriter(new OutputStreamWriter(out, charset));
    }

    public BundleOutputStream(OutputStream out) {
        this(out, Charset.defaultCharset());
    }

    @Override
    public void put(String key, String value) throws IOException {
        Objects.requireNonNull(key);
        if (value != null) {
            StringBuilder builder = new StringBuilder(key);
            for (String prefix : prefixes) {
                builder.insert(0, prefix);
            }
            key = builder.toString();
            writer.write(key);
            writer.write('\0');
            writer.write(value);
            writer.write('\0');
        }
    }

    @Override
    public void putBundleable(String key, Bundleable obj) throws IOException {
        prefixes.push(Objects.requireNonNull(key));
        try {
            obj.writeToBundle(this);
        }
        finally {
            prefixes.pop();
        }
    }

    @Override
    public <T> void putObject(String key, T obj, BiSlot<T, BundleOutput> writeProc) throws IOException {
        prefixes.push(Objects.requireNonNull(key));
        try {
            writeProc.accept(obj, this);
        }
        catch (FunctionTargetException e) {
            if (e.getTargetException() instanceof IOException) throw (IOException) e.getTargetException();
        }
        finally {
            prefixes.pop();
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

}
