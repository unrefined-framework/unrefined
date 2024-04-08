package unrefined.util.foreign;

import unrefined.util.NotInstantiableError;

import java.util.List;

public abstract class Symbol {

    public static final class Option {
        private Option() {
            throw new NotInstantiableError(Option.class);
        }
        public static final int DEFAULT      = 0;
        public static final int ALT_CALL     = 1;
        public static final int TRIVIAL_CALL = 1 << 1;
        public static final int SAVE_ERRNO   = 1 << 2;
        public static final int THROW_ERRNO  = 1 << 3;
        public static int removeUnusedBits(int options) {
            return options << 28 >>> 28;
        }
        public static String toString(int options) {
            options = removeUnusedBits(options);
            if (options == DEFAULT) return "[DEFAULT]";
            else {
                StringBuilder builder = new StringBuilder("[");
                if ((options & ALT_CALL) != 0) builder.append("ALT_CALL, ");
                if ((options & TRIVIAL_CALL) != 0) builder.append("TRIVIAL_CALL, ");
                if ((options & SAVE_ERRNO) != 0) builder.append("SAVE_ERRNO, ");
                if ((options & THROW_ERRNO) != 0) builder.append("THROW_ERRNO, ");
                builder.setLength(builder.length() - 2);
                builder.append("]");
                return builder.toString();
            }
        }
    }

    public abstract long address();

    public abstract List<Object> getParameterTypes();
    public abstract Object getReturnType();
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
    public abstract Aggregate invokeAggregate(Object... args);
    public abstract Aggregate invokeDescriptor(Object... args);
    public abstract Object invoke(Object... args);

}
