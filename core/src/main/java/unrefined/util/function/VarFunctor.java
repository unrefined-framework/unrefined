package unrefined.util.function;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface VarFunctor<R> {

    static VarFunctor<Void> cast(VarSlot slot) {
        return args -> {
            slot.actuate(args);
            return null;
        };
    }

    static <V> VarFunctor<V> cast(Callable<V> callable) {
        return args -> {
            try {
                return callable.call();
            } catch (Exception e) {
                if (e instanceof RuntimeException) throw (RuntimeException) e;
                else throw new FunctionTargetException(e);
            }
        };
    }

    static VarFunctor<Void> cast(Runnable runnable) {
        return args -> {
            runnable.run();
            return null;
        };
    }

    R actuate(Object... args);

}
