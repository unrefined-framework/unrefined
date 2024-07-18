package unrefined.io;

import unrefined.util.function.BiSlot;
import unrefined.util.function.FunctionTargetException;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class BundleWriter extends FilterWriter implements BundleOutput {

    private final Deque<String> prefixes = new ArrayDeque<>();

    public BundleWriter(Writer out) {
        super(out);
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
            write(key);
            write('\0');
            write(value);
            write('\0');
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

}
