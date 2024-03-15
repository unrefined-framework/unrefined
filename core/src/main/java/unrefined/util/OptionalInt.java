package unrefined.util;

import unrefined.util.concurrent.IntProducer;
import unrefined.util.concurrent.Producer;
import unrefined.util.function.Function;
import unrefined.util.function.Functor;
import unrefined.util.function.IntAssert;
import unrefined.util.function.IntFunctor;
import unrefined.util.function.IntOperator;
import unrefined.util.function.IntSlot;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A container object which may or may not contain a {@code int} value.
 * 
 * @see Optional
 */
public final class OptionalInt {
    
    private static final OptionalInt EMPTY = new OptionalInt();

    /**
     * Returns an empty {@code OptionalInt} instance.  
     *
     * @return an empty {@code OptionalInt}
     */
    public static OptionalInt empty() {
        return EMPTY;
    }

    /**
     * Return an {@code OptionalInt} with the specified value present.
     *
     * @param value the value to be present
     * @return an {@code OptionalInt} with the value present
     */
    public static OptionalInt of(int value) {
        return new OptionalInt(value);
    }

    /**
     * Returns an {@code OptionalInt} with the specified value, or empty {@code OptionalInt} if value is null.
     *
     * @param value the value which can be null
     * @return an {@code OptionalInt}
     */
    public static OptionalInt of(Integer value) {
        return value == null ? EMPTY : new OptionalInt(value);
    }
    
    private final boolean isPresent;
    private final int value;
    
    private OptionalInt() {
        this.isPresent = false;
        this.value = 0;
    }
    
    private OptionalInt(int value) {
        this.isPresent = true;
        this.value = value;
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    public boolean isPresent() {
        return isPresent;
    }

    /**
     * Checks the value is not present.
     *
     * @return {@code true} if a value is not present, {@code false} otherwise
     */
    public boolean isEmpty() {
        return !isPresent;
    }

    /**
     * Invokes consumer function with value if present, otherwise does nothing.
     *
     * @param consumer block to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     *         null
     */
    public void ifPresent(IntSlot consumer) {
        if (isPresent)
            consumer.accept(value);
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the empty-based action.
     *
     * @param consumer  the consumer function to be executed, if a value is present
     * @param emptyAction  the empty-based action to be performed, if no value is present
     * @throws NullPointerException if a value is present and the given consumer function is null,
     *         or no value is present and the given empty-based action is null.
     */
    public void ifPresentOrElse(IntSlot consumer, Runnable emptyAction) {
        if (isPresent) {
            consumer.accept(value);
        } else {
            emptyAction.run();
        }
    }

    /**
     * Invokes consumer function with value if present.
     * This method same as {@code ifPresent}, but does not break chaining
     *
     * @param consumer  consumer function
     * @return this {@code OptionalInt}
     * @see #ifPresent(IntSlot)
     */
    public OptionalInt executeIfPresent(IntSlot consumer) {
        ifPresent(consumer);
        return this;
    }

    /**
     * Invokes action function if value is absent.
     *
     * @param action  action that invokes if value absent
     * @return this {@code OptionalInt}
     */
    public OptionalInt executeIfAbsent(Runnable action) {
        if (!isPresent()) {
            action.run();
        }
        return this;
    }

    /**
     * Applies custom operator on {@code OptionalInt}.
     *
     * @param <R> the type of the result
     * @param function  a transforming function
     * @return a result of the transforming function
     * @throws NullPointerException if {@code function} is null
     */
    public <R> R custom(Functor<OptionalInt, R> function) {
        Objects.requireNonNull(function);
        return function.apply(this);
    }

    /**
     * Performs filtering on inner value if it is present.
     *
     * @param predicate  a predicate function
     * @return this {@code OptionalInt} if the value is present and matches predicate,
     *         otherwise an empty {@code OptionalInt}
     */
    public OptionalInt filter(IntAssert predicate) {
        if (!isPresent()) return this;
        return predicate.test(value) ? this : OptionalInt.empty();
    }

    /**
     * Performs negated filtering on inner value if it is present.
     *
     * @param predicate  a predicate function
     * @return this {@code OptionalInt} if the value is present and doesn't matches predicate,
     *              otherwise an empty {@code OptionalInt}
     */
    public OptionalInt filterNot(IntAssert predicate) {
        return filter(Function.negate(predicate));
    }

    /**
     * Invokes mapping function on inner value if present.
     *
     * @param mapper  mapping function
     * @return an {@code OptionalInt} with transformed value if present,
     *         otherwise an empty {@code OptionalInt}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public OptionalInt map(IntOperator mapper) {
        if (!isPresent()) return empty();
        return OptionalInt.of(mapper.applyAsInt(value));
    }

    /**
     * Invokes mapping function on inner value if present.
     *
     * @param <U> the type of result value
     * @param mapper  mapping function
     * @return an {@code Optional} with transformed value if present,
     *         otherwise an empty {@code Optional}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public <U> Optional<U> mapTo(IntFunctor<U> mapper) {
        if (!isPresent()) return Optional.empty();
        return Optional.of(mapper.apply(value));
    }

    /**
     * Returns current {@code OptionalInt} if value is present, otherwise
     * returns an {@code OptionalInt} produced by supplier function.
     *
     * @param supplier  supplier function that produces an {@code OptionalInt} to be returned
     * @return this {@code OptionalInt} if value is present, otherwise
     *         an {@code OptionalInt} produced by supplier function
     * @throws NullPointerException if value is not present and
     *         {@code supplier} or value produced by it is {@code null}
     */
    public OptionalInt or(Producer<OptionalInt> supplier) {
        if (isPresent()) return this;
        Objects.requireNonNull(supplier);
        return Objects.requireNonNull(supplier.get());
    }

    /**
     * Returns the value if present, otherwise returns {@code other}.
     *
     * @param other  the value to be returned if there is no value present
     * @return the value, if present, otherwise {@code other}
     */
    public int orElse(int other) {
        return isPresent ? value : other;
    }

    /**
     * Returns the value if present, otherwise invokes {@code other} and returns
     * the result of that invocation.
     *
     * @param other a {@code IntSupplier} whose result is returned if no value
     *              is present
     * @return the value if present otherwise the result of {@code other.getAsInt()}
     * @throws NullPointerException if value is not present and {@code other} is
     *         null
     */
    public int orElseGet(IntProducer other) {
        return isPresent ? value : other.getAsInt();
    }

    /**
     * Returns inner value if present, otherwise throws {@code NoSuchElementException}.
     *
     * @return inner value if present
     * @throws NoSuchElementException if inner value is not present
     */
    public int orElseThrow() {
        if (!isPresent) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * Returns the value if present, otherwise throws an exception provided by supplier function.
     *
     * @param <X> the type of exception to be thrown
     * @param exceptionSupplier  supplier function that produces an exception to be thrown
     * @return inner value if present
     * @throws X if inner value is not present
     */
    public <X extends Throwable> int orElseThrow(Producer<X> exceptionSupplier) throws X {
        if (isPresent) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(!(obj instanceof OptionalInt)) {
            return false;
        }

        OptionalInt other = (OptionalInt) obj;
        return (isPresent && other.isPresent)
                ? value == other.value
                : isPresent == other.isPresent;
    }
    
    @Override
    public int hashCode() {
        return isPresent ? value : 0;
    }
    
    @Override
    public String toString() {
        return isPresent
                ? String.format("OptionalInt[%s]", value)
                : "OptionalInt.empty";
    }

}
