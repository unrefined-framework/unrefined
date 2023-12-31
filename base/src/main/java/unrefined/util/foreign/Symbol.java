package unrefined.util.foreign;

import java.util.List;

public abstract class Symbol {

    public abstract long address();

    public abstract List<Class<?>> getParameterTypes();
    public abstract Class<?> getReturnType();
    public abstract boolean isVarargs();

    public abstract void invokeVoid(Object... args);
    public abstract boolean invokeBoolean(Object... args);
    public abstract byte invokeByte(Object... args);
    public abstract char invokeChar(Object... args);
    public abstract short invokeShort(Object... args);
    public abstract int invokeInt(Object... args);
    public abstract long invokeNativeInt(Object... args);
    public abstract long invokeLong(Object... args);
    public abstract long invokeNativeLong(Object... args);
    public abstract float invokeFloat(Object... args);
    public abstract double invokeDouble(Object... args);
    public abstract long invokeAddress(Object... args);
    public abstract Object invoke(Object... args);

}
