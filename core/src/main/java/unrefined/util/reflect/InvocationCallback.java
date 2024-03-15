package unrefined.util.reflect;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
public interface InvocationCallback {
    void call() throws InvocationTargetException;
}
