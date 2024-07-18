package unrefined.json;

import unrefined.io.BundleOutput;
import unrefined.io.Bundleable;
import unrefined.math.FastMath;
import unrefined.util.MutableBoolean;
import unrefined.util.function.BiSlot;
import unrefined.util.function.FunctionTargetException;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class JSONBundleWriter extends FilterWriter implements JSONBundleOutput {

    private boolean start = false, end = false;
    private final Deque<MutableBoolean> first = new ArrayDeque<>();
    {
        first.push(new MutableBoolean(true));
    }

    public JSONBundleWriter(Writer out) {
        super(out);
    }

    private boolean isFirst() {
        return first.peek().get();
    }

    @Override
    public void put(String key, String value) throws IOException {
        Objects.requireNonNull(key);
        if (value != null) {
            if (!start) {
                start = true;
                write('{');
            }
            if (!isFirst()) write(',');
            else first.peek().set(false);
            write('"');
            write(JSON.escape(key));
            write('"');
            write(':');
            write('"');
            write(JSON.escape(value));
            write('"');
        }
    }

    @Override
    public void putBoolean(String key, boolean value) throws IOException {
        Objects.requireNonNull(key);
        if (!start) {
            start = true;
            write('{');
        }
        if (!isFirst()) write(',');
        else first.peek().set(false);
        write('"');
        write(JSON.escape(key));
        write('"');
        write(':');
        write(String.valueOf(value));
    }

    @Override
    public void putLong(String key, long value) throws IOException {
        Objects.requireNonNull(key);
        if (!start) {
            start = true;
            write('{');
        }
        if (!isFirst()) write(',');
        else first.peek().set(false);
        write('"');
        write(JSON.escape(key));
        write('"');
        write(':');
        write(String.valueOf(value));
    }

    @Override
    public void putDouble(String key, double value) throws IOException {
        if (FastMath.isInteger(value)) putLong(key, Double.valueOf(value).longValue());
        else {
            Objects.requireNonNull(key);
            if (!start) {
                start = true;
                write('{');
            }
            if (!isFirst()) write(',');
            else first.peek().set(false);
            write('"');
            write(JSON.escape(key));
            write('"');
            write(':');
            write(String.valueOf(value));
        }
    }

    @Override
    public void putBundleable(String key, Bundleable obj) throws IOException {
        if (!start) {
            start = true;
            write('{');
        }
        first.push(new MutableBoolean(true));
        try {
            write('"');
            write(JSON.escape(key));
            write('"');
            write(':');
            write('{');
            obj.writeToBundle(this);
            write('}');
        }
        finally {
            first.pop();
        }
    }

    @Override
    public <T> void putObject(String key, T obj, BiSlot<T, BundleOutput> writeProc) throws IOException {
        if (!start) {
            start = true;
            write('{');
        }
        first.push(new MutableBoolean(true));
        try {
            write('"');
            write(JSON.escape(key));
            write('"');
            write(':');
            write('{');
            writeProc.accept(obj, this);
            write('}');
        }
        catch (FunctionTargetException e) {
            if (e.getTargetException() instanceof IOException) throw (IOException) e.getTargetException();
        }
        finally {
            first.pop();
        }
    }

    @Override
    public void flush() throws IOException {
        if (!end) {
            end = true;
            write('}');
        }
        super.flush();
    }

    @Override
    public void close() throws IOException {
        if (!end) {
            end = true;
            write('}');
        }
        super.close();
    }

}
