package unrefined.util.function;

import unrefined.util.NotInstantiableError;
import unrefined.util.concurrent.Producer;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Callable;

public final class Function {

    private Function() {
        throw new NotInstantiableError(Function.class);
    }

    public static void capture(Runnable r, Slot<Throwable> consumer) {
        try {
            r.run();
        }
        catch (FunctionTargetException e) {
            consumer.accept(e.getTargetException());
        }
    }

    public static void capture(Callable<?> r, Slot<Throwable> consumer) {
        try {
            r.call();
        }
        catch (Throwable t) {
            consumer.accept(t);
        }
    }

    public static void fatal(Throwable e) throws FunctionTargetException {
        if (e instanceof FunctionTargetException) throw (FunctionTargetException) e;
        else throw new FunctionTargetException(e);
    }

    public static void fatal(Producer<Throwable> e) throws FunctionTargetException {
        fatal(e.get());
    }

    public static <T, U, R, V> BiFunctor<T, U, V> andThen(
            BiFunctor<? super T, ? super U, ? extends R> a,
            Functor<? super R, ? extends V> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return (t, u) -> b.apply(a.apply(t, u));
    }

    public static <T, U, R> BiFunctor<U, T, R> reverse(
            BiFunctor<? super T, ? super U, ? extends R> a) {
        Objects.requireNonNull(a);
        return (u, t) -> a.apply(t, u);
    }

    public static <T> BiOperator<T> minBy(Comparator<? super T> cmp) {
        Objects.requireNonNull(cmp);
        return (a, b) -> cmp.compare(a, b) <= 0 ? a : b;
    }

    public static <T> BiOperator<T> maxBy(Comparator<? super T> cmp) {
        Objects.requireNonNull(cmp);
        return (a, b) -> cmp.compare(a, b) >= 0 ? a : b;
    }

    public static <T, U> BiSlot<T, U> andThen(BiSlot<? super T, ? super U> a, BiSlot<? super T, ? super U> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return (t, u) -> {
            a.accept(t, u);
            b.accept(t, u);
        };
    }

    public static <T, U> BiSlot<T, U> andThen(BiSlot<? super T, ? super U> a, BiSlot<? super T, ? super U> b, BiSlot<? super T, ? super U>... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return (t, u) -> {
            a.accept(t, u);
            b.accept(t, u);
            for (BiSlot<? super T, ? super U> arg : args) {
                arg.accept(t, u);
            }
        };
    }

    public static <T, U> BiAssert<T, U> and(
            BiAssert<? super T, ? super U> a,
            BiAssert<? super T, ? super U> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return (t, u) -> a.test(t, u) && b.test(t, u);
    }

    public static <T, U> BiAssert<T, U> and(
            BiAssert<? super T, ? super U> a,
            BiAssert<? super T, ? super U> b,
            BiAssert<? super T, ? super U>... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return (t, u) -> {
            boolean result = a.test(t, u) && b.test(t, u);
            for (BiAssert<? super T, ? super U> arg : args) {
                result = result && arg.test(t, u);
            }
            return result;
        };
    }

    public static <T, U> BiAssert<T, U> or(
            BiAssert<? super T, ? super U> a,
            BiAssert<? super T, ? super U> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return (t, u) -> a.test(t, u) || b.test(t, u);
    }

    public static <T, U> BiAssert<T, U> or(
            BiAssert<? super T, ? super U> a,
            BiAssert<? super T, ? super U> b,
            BiAssert<? super T, ? super U>... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return (t, u) -> {
            boolean result = a.test(t, u) || b.test(t, u);
            for (BiAssert<? super T, ? super U> arg : args) {
                result = result || arg.test(t, u);
            }
            return result;
        };
    }

    public static <T, U> BiAssert<T, U> xor(
            BiAssert<? super T, ? super U> a,
            BiAssert<? super T, ? super U> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return (t, u) -> a.test(t, u) ^ b.test(t, u);
    }

    public static <T, U> BiAssert<T, U> xor(
            BiAssert<? super T, ? super U> a,
            BiAssert<? super T, ? super U> b,
            BiAssert<? super T, ? super U>... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return (t, u) -> {
            boolean result = a.test(t, u) ^ b.test(t, u);
            for (BiAssert<? super T, ? super U> arg : args) {
                result = result ^ arg.test(t, u);
            }
            return result;
        };
    }

    public static <T, U> BiAssert<T, U> negate(BiAssert<? super T, ? super U> a) {
        Objects.requireNonNull(a);
        return (t, u) -> !a.test(t, u);
    }

    public static <V, T, R> Functor<V, R> compose(
            Functor<? super T, ? extends R> a,
            Functor<? super V, ? extends T> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return Function.<V, T, R>andThen(b, a);
    }

    public static <T, R, V> Functor<T, V> andThen(
            Functor<? super T, ? extends R> a,
            Functor<? super R, ? extends V> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> b.apply(a.apply(t));
    }

    public static <T> Operator<T> identity() {
        return t -> t;
    }

    public static <T> Slot<T> andThen(
            Slot<? super T> a,
            Slot<? super T> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
        };
    }

    public static <T> Slot<T> andThen(
            Slot<? super T> a,
            Slot<? super T> b,
            Slot<? super T>... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
            for (Slot<? super T> arg : args) {
                arg.accept(value);
            }
        };
    }

    public static <T> Assert<T> and(
            Assert<? super T> a,
            Assert<? super T> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> a.test(t) && b.test(t);
    }

    public static <T> Assert<T> and(
            Assert<? super T> a,
            Assert<? super T> b,
            Assert<? super T>... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) && b.test(t);
            for (Assert<? super T> arg : args) {
                result = result && Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static <T> Assert<T> or(
            Assert<? super T> a,
            Assert<? super T> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> a.test(t) || b.test(t);
    }

    public static <T> Assert<T> or(
            Assert<? super T> a,
            Assert<? super T> b,
            Assert<? super T>... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) || b.test(t);
            for (Assert<? super T> arg : args) {
                result = result || Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static <T> Assert<T> xor(
            Assert<? super T> a,
            Assert<? super T> b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> a.test(t) ^ b.test(t);
    }

    public static <T> Assert<T> xor(
            Assert<? super T> a,
            Assert<? super T> b,
            Assert<? super T>... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) ^ b.test(t);
            for (Assert<? super T> arg : args) {
                result = result ^ Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static <T> Assert<T> negate(Assert<? super T> a) {
        Objects.requireNonNull(a);
        return t -> !a.test(t);
    }

    public static <T> Assert<T> nonNull() {
        return t -> t != null;
    }

    public static BooleanOperator identityAsBoolean() {
        return value -> value;
    }

    public static BooleanBiOperator minByBoolean() {
        return (a, b) -> Boolean.compare(a, b) <= 0 ? a : b;
    }

    public static BooleanBiOperator maxByBoolean() {
        return (a, b) -> Boolean.compare(a, b) >= 0 ? a : b;
    }

    public static BooleanSlot andThen(BooleanSlot a, BooleanSlot b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
        };
    }

    public static BooleanSlot andThen(BooleanSlot a, BooleanSlot b, BooleanSlot... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
            for (BooleanSlot arg : args) {
                arg.accept(value);
            }
        };
    }

    public static BooleanAssert and(BooleanAssert a, BooleanAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) && b.test(value);
    }

    public static BooleanAssert and(BooleanAssert a, BooleanAssert b, BooleanAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) && b.test(t);
            for (BooleanAssert arg : args) {
                result = result && Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static BooleanAssert or(BooleanAssert a, BooleanAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) || b.test(value);
    }

    public static BooleanAssert or(BooleanAssert a, BooleanAssert b, BooleanAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) || b.test(t);
            for (BooleanAssert arg : args) {
                result = result || Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static BooleanAssert xor(BooleanAssert a, BooleanAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) ^ b.test(value);
    }

    public static BooleanAssert xor(BooleanAssert a, BooleanAssert b, BooleanAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) ^ b.test(t);
            for (BooleanAssert arg : args) {
                result = result ^ Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static BooleanAssert negate(BooleanAssert a) {
        Objects.requireNonNull(a);
        return value -> !a.test(value);
    }

    public static ByteOperator identityAsByte() {
        return value -> value;
    }

    public static ByteBiOperator minByByte() {
        return (a, b) -> Byte.compare(a, b) <= 0 ? a : b;
    }

    public static ByteBiOperator maxByByte() {
        return (a, b) -> Byte.compare(a, b) >= 0 ? a : b;
    }
    
    public static ByteSlot andThen(ByteSlot a, ByteSlot b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
        };
    }

    public static ByteSlot andThen(ByteSlot a, ByteSlot b, ByteSlot... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
            for (ByteSlot arg : args) {
                arg.accept(value);
            }
        };
    }

    public static ByteAssert and(ByteAssert a, ByteAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) && b.test(value);
    }

    public static ByteAssert and(ByteAssert a, ByteAssert b, ByteAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) && b.test(t);
            for (ByteAssert arg : args) {
                result = result && Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static ByteAssert or(ByteAssert a, ByteAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) || b.test(value);
    }

    public static ByteAssert or(ByteAssert a, ByteAssert b, ByteAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) || b.test(t);
            for (ByteAssert arg : args) {
                result = result || Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static ByteAssert xor(ByteAssert a, ByteAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) ^ b.test(value);
    }

    public static ByteAssert xor(ByteAssert a, ByteAssert b, ByteAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) ^ b.test(t);
            for (ByteAssert arg : args) {
                result = result ^ Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static ByteAssert negate(ByteAssert a) {
        Objects.requireNonNull(a);
        return value -> !a.test(value);
    }

    public static CharOperator identityAsChar() {
        return value -> value;
    }

    public static CharBiOperator minByChar() {
        return (a, b) -> Character.compare(a, b) <= 0 ? a : b;
    }

    public static CharBiOperator maxByChar() {
        return (a, b) -> Character.compare(a, b) >= 0 ? a : b;
    }

    public static CharSlot andThen(CharSlot a, CharSlot b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
        };
    }

    public static CharSlot andThen(CharSlot a, CharSlot b, CharSlot... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
            for (CharSlot arg : args) {
                arg.accept(value);
            }
        };
    }

    public static CharAssert and(CharAssert a, CharAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) && b.test(value);
    }

    public static CharAssert and(CharAssert a, CharAssert b, CharAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) && b.test(t);
            for (CharAssert arg : args) {
                result = result && Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static CharAssert or(CharAssert a, CharAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) || b.test(value);
    }

    public static CharAssert or(CharAssert a, CharAssert b, CharAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) || b.test(t);
            for (CharAssert arg : args) {
                result = result || Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static CharAssert xor(CharAssert a, CharAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) ^ b.test(value);
    }

    public static CharAssert xor(CharAssert a, CharAssert b, CharAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) ^ b.test(t);
            for (CharAssert arg : args) {
                result = result ^ Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static CharAssert negate(CharAssert a) {
        Objects.requireNonNull(a);
        return value -> !a.test(value);
    }

    public static ShortOperator identityAsShort() {
        return value -> value;
    }

    public static ShortBiOperator minByShort() {
        return (a, b) -> Short.compare(a, b) <= 0 ? a : b;
    }

    public static ShortBiOperator maxByShort() {
        return (a, b) -> Short.compare(a, b) >= 0 ? a : b;
    }

    public static ShortSlot andThen(ShortSlot a, ShortSlot b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
        };
    }

    public static ShortSlot andThen(ShortSlot a, ShortSlot b, ShortSlot... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
            for (ShortSlot arg : args) {
                arg.accept(value);
            }
        };
    }

    public static ShortAssert and(ShortAssert a, ShortAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) && b.test(value);
    }

    public static ShortAssert and(ShortAssert a, ShortAssert b, ShortAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) && b.test(t);
            for (ShortAssert arg : args) {
                result = result && Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static ShortAssert or(ShortAssert a, ShortAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) || b.test(value);
    }

    public static ShortAssert or(ShortAssert a, ShortAssert b, ShortAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) || b.test(t);
            for (ShortAssert arg : args) {
                result = result || Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static ShortAssert xor(ShortAssert a, ShortAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) ^ b.test(value);
    }

    public static ShortAssert xor(ShortAssert a, ShortAssert b, ShortAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) ^ b.test(t);
            for (ShortAssert arg : args) {
                result = result ^ Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static ShortAssert negate(ShortAssert a) {
        Objects.requireNonNull(a);
        return value -> !a.test(value);
    }

    public static IntOperator identityAsInt() {
        return value -> value;
    }

    public static IntBiOperator minByInt() {
        return (a, b) -> Integer.compare(a, b) <= 0 ? a : b;
    }

    public static IntBiOperator maxByInt() {
        return (a, b) -> Integer.compare(a, b) >= 0 ? a : b;
    }

    public static IntSlot andThen(IntSlot a, IntSlot b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
        };
    }

    public static IntSlot andThen(IntSlot a, IntSlot b, IntSlot... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
            for (IntSlot arg : args) {
                arg.accept(value);
            }
        };
    }

    public static IntAssert and(IntAssert a, IntAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) && b.test(value);
    }

    public static IntAssert and(IntAssert a, IntAssert b, IntAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) && b.test(t);
            for (IntAssert arg : args) {
                result = result && Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }
    
    public static IntAssert or(IntAssert a, IntAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) || b.test(value);
    }

    public static IntAssert or(IntAssert a, IntAssert b, IntAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) || b.test(t);
            for (IntAssert arg : args) {
                result = result || Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }
    
    public static IntAssert xor(IntAssert a, IntAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) ^ b.test(value);
    }

    public static IntAssert xor(IntAssert a, IntAssert b, IntAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) ^ b.test(t);
            for (IntAssert arg : args) {
                result = result ^ Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }
    
    public static IntAssert negate(IntAssert a) {
        Objects.requireNonNull(a);
        return value -> !a.test(value);
    }

    public static LongOperator identityAsLong() {
        return value -> value;
    }

    public static LongBiOperator minByLong() {
        return (a, b) -> Long.compare(a, b) <= 0 ? a : b;
    }

    public static LongBiOperator maxByLong() {
        return (a, b) -> Long.compare(a, b) >= 0 ? a : b;
    }

    public static LongSlot andThen(LongSlot a, LongSlot b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
        };
    }

    public static LongSlot andThen(LongSlot a, LongSlot b, LongSlot... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
            for (LongSlot arg : args) {
                arg.accept(value);
            }
        };
    }

    public static LongAssert and(LongAssert a, LongAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) && b.test(value);
    }

    public static LongAssert and(LongAssert a, LongAssert b, LongAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) && b.test(t);
            for (LongAssert arg : args) {
                result = result && Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static LongAssert or(LongAssert a, LongAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) || b.test(value);
    }

    public static LongAssert or(LongAssert a, LongAssert b, LongAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) || b.test(t);
            for (LongAssert arg : args) {
                result = result || Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static LongAssert xor(LongAssert a, LongAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) ^ b.test(value);
    }

    public static LongAssert xor(LongAssert a, LongAssert b, LongAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) ^ b.test(t);
            for (LongAssert arg : args) {
                result = result ^ Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static LongAssert negate(LongAssert a) {
        Objects.requireNonNull(a);
        return value -> !a.test(value);
    }

    public static FloatOperator identityAsFloat() {
        return value -> value;
    }

    public static FloatBiOperator minByFloat() {
        return (a, b) -> Float.compare(a, b) <= 0 ? a : b;
    }

    public static FloatBiOperator maxByFloat() {
        return (a, b) -> Float.compare(a, b) >= 0 ? a : b;
    }

    public static FloatSlot andThen(FloatSlot a, FloatSlot b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
        };
    }

    public static FloatSlot andThen(FloatSlot a, FloatSlot b, FloatSlot... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
            for (FloatSlot arg : args) {
                arg.accept(value);
            }
        };
    }

    public static FloatAssert and(FloatAssert a, FloatAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) && b.test(value);
    }

    public static FloatAssert and(FloatAssert a, FloatAssert b, FloatAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) && b.test(t);
            for (FloatAssert arg : args) {
                result = result && Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static FloatAssert or(FloatAssert a, FloatAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) || b.test(value);
    }

    public static FloatAssert or(FloatAssert a, FloatAssert b, FloatAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) || b.test(t);
            for (FloatAssert arg : args) {
                result = result || Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static FloatAssert xor(FloatAssert a, FloatAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) ^ b.test(value);
    }

    public static FloatAssert xor(FloatAssert a, FloatAssert b, FloatAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) ^ b.test(t);
            for (FloatAssert arg : args) {
                result = result ^ Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static FloatAssert negate(FloatAssert a) {
        Objects.requireNonNull(a);
        return value -> !a.test(value);
    }

    public static DoubleOperator identityAsDouble() {
        return value -> value;
    }

    public static DoubleBiOperator minByDouble() {
        return (a, b) -> Double.compare(a, b) <= 0 ? a : b;
    }

    public static DoubleBiOperator maxByDouble() {
        return (a, b) -> Double.compare(a, b) >= 0 ? a : b;
    }

    public static DoubleSlot andThen(DoubleSlot a, DoubleSlot b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
        };
    }

    public static DoubleSlot andThen(DoubleSlot a, DoubleSlot b, DoubleSlot... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> {
            a.accept(value);
            b.accept(value);
            for (DoubleSlot arg : args) {
                arg.accept(value);
            }
        };
    }

    public static DoubleAssert and(DoubleAssert a, DoubleAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) && b.test(value);
    }

    public static DoubleAssert and(DoubleAssert a, DoubleAssert b, DoubleAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) && b.test(t);
            for (DoubleAssert arg : args) {
                result = result && Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static DoubleAssert or(DoubleAssert a, DoubleAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) || b.test(value);
    }

    public static DoubleAssert or(DoubleAssert a, DoubleAssert b, DoubleAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) || b.test(t);
            for (DoubleAssert arg : args) {
                result = result || Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static DoubleAssert xor(DoubleAssert a, DoubleAssert b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return value -> a.test(value) ^ b.test(value);
    }

    public static DoubleAssert xor(DoubleAssert a, DoubleAssert b, DoubleAssert... args) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        return t -> {
            boolean result = a.test(t) ^ b.test(t);
            for (DoubleAssert arg : args) {
                result = result ^ Objects.requireNonNull(arg).test(t);
            }
            return result;
        };
    }

    public static DoubleAssert negate(DoubleAssert a) {
        Objects.requireNonNull(a);
        return value -> !a.test(value);
    }

    public static <T> IndexedAssert<T> wrap(Assert<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return (i, value) -> predicate.test(value);
    }

    public static <T, R> IndexedFunctor<T, R> wrap(Functor<? super T, ? extends R> function) {
        Objects.requireNonNull(function);
        return (i, t) -> function.apply(t);
    }

    public static <T> IndexedSlot<T> wrap(Slot<? super T> consumer) {
        Objects.requireNonNull(consumer);
        return (i, t) -> consumer.accept(t);
    }

    public static <T> IndexedSlot<T> accept(IntSlot index, Slot<? super T> consumer) {
        return (i, value) -> {
            if (index != null) index.accept(i);
            if (consumer != null) consumer.accept(value);
        };
    }

}
