package unrefined.util;

import unrefined.util.concurrent.ByteProducer;
import unrefined.util.concurrent.Producer;
import unrefined.util.function.Function;
import unrefined.util.function.Functor;
import unrefined.util.function.ByteAssert;
import unrefined.util.function.ByteFunctor;
import unrefined.util.function.ByteOperator;
import unrefined.util.function.ByteSlot;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A container object which may or may not contain a {@code byte} value.
 *
 * @see Optional
 */
public final class OptionalByte {

    private static final OptionalByte EMPTY = new OptionalByte();

    /**
     * Returns an empty {@code OptionalByte} instance.
     *
     * @return an empty {@code OptionalByte}
     */
    public static OptionalByte empty() {
        return EMPTY;
    }

    /**
     * Returns an {@code OptionalByte} with the specified value present.
     *
     * @param value  the value to be present
     * @return an {@code OptionalByte} with the value present
     */
    public static OptionalByte of(byte value) {
        return new OptionalByte(value);
    }

    /**
     * Returns an {@code OptionalByte} with the specified value, or empty {@code OptionalByte} if value is null.
     *
     * @param value the value which can be null
     * @return an {@code OptionalByte}
     */
    public static OptionalByte of(Byte value) {
        return value == null ? EMPTY : new OptionalByte(value);
    }

    private final boolean isPresent;
    private final byte value;

    private OptionalByte() {
        this.isPresent = false;
        this.value = 0;
    }

    private OptionalByte(byte value) {
        this.isPresent = true;
        this.value = value;
    }

    /**
     * Checks value present.
     *
     * @return {@code true} if a value present, {@code false} otherwise
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
     * @param consumer  the consumer function to be executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is null
     */
    public void ifPresent(ByteSlot consumer) {
        if (isPresent) {
            consumer.accept(value);
        }
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
    public void ifPresentOrElse(ByteSlot consumer, Runnable emptyAction) {
        if (isPresent) {
            consumer.accept(value);
        } else {
            emptyAction.run();
        }
    }

    /**
     * Invokes consumer function with the value if present.
     * This method same as {@code ifPresent}, but does not breaks chaining
     *
     * @param consumer  consumer function
     * @return this {@code OptionalByte}
     * @see #ifPresent(ByteSlot)
     */
    public OptionalByte executeIfPresent(ByteSlot consumer) {
        ifPresent(consumer);
        return this;
    }

    /**
     * Invokes action function if value is absent.
     *
     * @param action  action that invokes if value absent
     * @return this {@code OptionalByte}
     */
    public OptionalByte executeIfAbsent(Runnable action) {
        if (!isPresent()) {
            action.run();
        }
        return this;
    }

    /**
     * Applies custom operator on {@code OptionalByte}.
     *
     * @param <R> the type of the result
     * @param function  a transforming function
     * @return a result of the transforming function
     * @throws NullPointerException if {@code function} is null
     */
    public <R> R custom(Functor<OptionalByte, R> function) {
        Objects.requireNonNull(function);
        return function.apply(this);
    }

    /**
     * Performs filtering on inner value if it is present.
     *
     * @param predicate  a predicate function
     * @return this {@code OptionalByte} if the value is present and matches predicate,
     *         otherwise an empty {@code OptionalByte}
     */
    public OptionalByte filter(ByteAssert predicate) {
        if (!isPresent()) return this;
        return predicate.test(value) ? this : OptionalByte.empty();
    }

    /**
     * Performs negated filtering on inner value if it is present.
     *
     * @param predicate  a predicate function
     * @return this {@code OptionalByte} if the value is present and doesn't matches predicate,
     *              otherwise an empty {@code OptionalByte}
     */
    public OptionalByte filterNot(ByteAssert predicate) {
        return filter(Function.negate(predicate));
    }

    /**
     * Invokes the given mapping function on inner value if present.
     *
     * @param mapper  mapping function
     * @return an {@code OptionalByte} with transformed value if present,
     *         otherwise an empty {@code OptionalByte}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public OptionalByte map(ByteOperator mapper) {
        if (!isPresent()) {
            return empty();
        }
        Objects.requireNonNull(mapper);
        return OptionalByte.of(mapper.applyAsByte(value));
    }

    /**
     * Invokes the given mapping function on inner value if present.
     *
     * @param <U> the type of result value
     * @param mapper  mapping function
     * @return an {@code Optional} with transformed value if present,
     *         otherwise an empty {@code Optional}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public <U> Optional<U> mapTo(ByteFunctor<U> mapper) {
        if (!isPresent()) {
            return Optional.empty();
        }
        Objects.requireNonNull(mapper);
        return Optional.of(mapper.apply(value));
    }

    /**
     * Returns current {@code OptionalByte} if value is present, otherwise
     * returns an {@code OptionalByte} produced by supplier function.
     *
     * @param supplier  supplier function that produces an {@code OptionalByte} to be returned
     * @return this {@code OptionalByte} if value is present, otherwise
     *         an {@code OptionalByte} produced by supplier function
     * @throws NullPointerException if value is not present and
     *         {@code supplier} or value produced by it is {@code null}
     */
    public OptionalByte or(Producer<OptionalByte> supplier) {
        if (isPresent()) return this;
        Objects.requireNonNull(supplier);
        return Objects.requireNonNull(supplier.get());
    }

    /**
     * Returns inner value if present, otherwise returns {@code other}.
     *
     * @param other  the value to be returned if there is no value present
     * @return the value, if present, otherwise {@code other}
     */
    public byte orElse(byte other) {
        return isPresent ? value : other;
    }

    /**
     * Returns the value if present, otherwise returns value produced by supplier function.
     *
     * @param other  supplier function that produces value if inner value is not present
     * @return the value if present otherwise the result of {@code other.getAsByte()}
     * @throws NullPointerException if value is not present and {@code other} is null
     */
    public byte orElseGet(ByteProducer other) {
        return isPresent ? value : other.getAsByte();
    }

    /**
     * Returns inner value if present, otherwise throws {@code NoSuchElementException}.
     *
     * @return inner value if present
     * @throws NoSuchElementException if inner value is not present
     */
    public byte orElseThrow() {
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
    public <X extends Throwable> byte orElseThrow(Producer<X> exceptionSupplier) throws X {
        if (isPresent) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OptionalByte)) {
            return false;
        }

        OptionalByte other = (OptionalByte) obj;
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
                ? String.format("OptionalByte[%s]", value)
                : "OptionalByte.empty";
    }

}
