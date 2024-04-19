package unrefined.util;

import unrefined.util.function.FunctionTargetException;

import java.util.concurrent.Callable;

public interface RunnableCallable extends Runnable, Callable<Void> {

    @Override
    default void run() {
        try {
            call();
        } catch (Exception e) {
            throw new FunctionTargetException(e);
        }
    }

    default Void call() throws Exception {
        exceptional();
        return null;
    }

    void exceptional() throws Exception;

}
