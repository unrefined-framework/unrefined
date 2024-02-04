package unrefined.io;

import unrefined.util.NotInstantiableError;
import unrefined.util.reflect.Reflection;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;

public final class IOFactory {

    private IOFactory() {
        throw new NotInstantiableError(IOFactory.class);
    }

    private static final Field outField;
    static {
        Field field;
        try {
            field = FilterOutputStream.class.getDeclaredField("out");
        } catch (NoSuchFieldException e) {
            field = null;
        }
        outField = field;
    }

    public static OutputStream getFilteredOutputStream(OutputStream out) {
        if (!(out instanceof FilterOutputStream)) return out;
        return getFilteredOutputStream((OutputStream) Reflection.getInstance().getObjectField(out, outField));
    }

    public static boolean isStandardStream(OutputStream out) {
        out = getFilteredOutputStream(out);
        if (out instanceof FileOutputStream) {
            try {
                FileDescriptor fd = ((FileOutputStream) out).getFD();
                if (fd == FileDescriptor.out || fd == FileDescriptor.err) return true;
            } catch (IOException ignored) {
            }
        }
        return false;
    }

}
