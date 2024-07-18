package unrefined.json;

import unrefined.io.BundleInput;
import unrefined.io.Bundleable;
import unrefined.json.parse.JSONParseException;
import unrefined.math.FastMath;
import unrefined.util.Rational;
import unrefined.util.function.BiSlot;
import unrefined.util.function.FunctionTargetException;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class JSONBundleReader extends FilterReader implements JSONBundleInput {

    private final Deque<JSONObject> current = new ArrayDeque<>();
    private JSONObject root;
    private boolean parsed = false;

    public JSONBundleReader(Reader in) {
        super(in);
    }

    private void ensureParse() throws IOException {
        if (!parsed) {
            parsed = true;
            try {
                root = (JSONObject) JSON.parse(this);
                current.push(root);
            } catch (JSONParseException e) {
                throw new IOException(e);
            } catch (ClassCastException e) {
                throw new IOException("not a valid json object");
            }
        }
    }

    private JSONObject current() {
        return current.peek();
    }

    @Override
    public String get(String key, String defaultValue) throws IOException {
        ensureParse();
        return current().getJSONString(key, defaultValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) throws IOException {
        ensureParse();
        return current().getJSONBoolean(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) throws IOException {
        ensureParse();
        return current().getJSONInteger(key, defaultValue);
    }

    @Override
    public double getDouble(String key, double defaultValue) throws IOException {
        ensureParse();
        return current().getJSONDecimal(key, defaultValue);
    }

    @Override
    public <T extends Bundleable> void getBundleable(String key, T obj) throws IOException {
        ensureParse();
        current.push((JSONObject) current().getJSONObject(Objects.requireNonNull(key), null));
        try {
            obj.readFromBundle(this);
        }
        finally {
            current.pop();
        }
    }

    @Override
    public <T> void getObject(String key, T obj, BiSlot<T, BundleInput> readProc) throws IOException {
        ensureParse();
        current.push((JSONObject) current().getJSONObject(Objects.requireNonNull(key), null));
        try {
            readProc.accept(obj, this);
        }
        catch (FunctionTargetException e) {
            if (e.getTargetException() instanceof IOException) throw (IOException) e.getTargetException();
        }
        finally {
            current.pop();
        }
    }

    @Override
    public Rational getRational(String key, Rational defaultValue) throws IOException {
        ensureParse();
        Number number = current().getJSONNumber(key, null);
        if (number instanceof Long) return new Rational(unrefined.util.Objects.toIntExact((Long) number), 1);
        else if (number instanceof Double) {
            double num = (double) number;
            int count = 0;
            int base = 10;
            while (num != Math.floor(num)) {
                num *= base;
                count ++;
            }
            base = (int) Math.pow(base, count);
            int nor = (int) num;
            int gcd = FastMath.gcd(nor, base);
            return new Rational(nor / gcd, base / gcd);
        }
        else return defaultValue;
    }

    @Override
    public void close() throws IOException {
        root.clear();
        root = null;
        current.clear();
        super.close();
    }

}
