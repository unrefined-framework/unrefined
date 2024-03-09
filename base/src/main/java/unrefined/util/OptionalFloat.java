package unrefined.util;

import unrefined.util.concurrent.FloatProducer;
import unrefined.util.concurrent.Producer;
import unrefined.util.function.FloatAssert;
import unrefined.util.function.FloatFunctor;
import unrefined.util.function.FloatOperator;
import unrefined.util.function.FloatSlot;
import unrefined.util.function.Function;
import unrefined.util.function.Functor;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A container object which may or may not contain a {@code float} value.
 *
 * @see Optional
 */
public final class OptionalFloat {

    private static final OptionalFloat EMPTY = new OptionalFloat();

    /**
     * Returns an empty {@code OptionalFloat} instance.
     *
     * @return an empty {@code OptionalFloat}
     */
    public static OptionalFloat empty() {
        return EMPTY;
    }

    /**
     * Returns an {@code OptionalFloat} with the specified value present.
     *
     * @param value  the value to be present
     * @return an {@code OptionalFloat} with the value present
     */
    public static OptionalFloat of(float value) {
        return new OptionalFloat(value);
    }

    /**
     * Returns an {@code OptionalFloat} with the specified value, or empty {@code OptionalFloat} if value is null.
     *
     * @param value the value which can be null
     * @return an {@code OptionalFloat}
     */
    public static OptionalFloat of(Float value) {
        return value == null ? EMPTY : new OptionalFloat(value);
    }

    private final boolean isPresent;
    private final float value;

    private OptionalFloat() {
        this.isPresent = false;
        this.value = 0;
    }

    private OptionalFloat(float value) {
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
    public void ifPresent(FloatSlot consumer) {
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
    public void ifPresentOrElse(FloatSlot consumer, Runnable emptyAction) {
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
     * @return this {@code OptionalFloat}
     * @see #ifPresent(FloatSlot)
     */
    public OptionalFloat executeIfPresent(FloatSlot consumer) {
        ifPresent(consumer);
        return this;
    }

    /**
     * Invokes action function if value is absent.
     *
     * @param action  action that invokes if value absent
     * @return this {@code OptionalFloat}
     */
    public OptionalFloat executeIfAbsent(Runnable action) {
        if (!isPresent()) {
            action.run();
        }
        return this;
    }

    /**
     * Applies custom operator on {@code OptionalFloat}.
     *
     * @param <R> the type of the result
     * @param function  a transforming function
     * @return a result of the transforming function
     * @throws NullPointerException if {@code function} is null
     */
    public <R> R custom(Functor<OptionalFloat, R> function) {
        Objects.requireNonNull(function);
        return function.apply(this);
    }

    /**
     * Performs filtering on inner value if it is present.
     *
     * @param predicate  a predicate function
     * @return this {@code OptionalFloat} if the value is present and matches predicate,
     *         otherwise an empty {@code OptionalFloat}
     */
    public OptionalFloat filter(FloatAssert predicate) {
        if (!isPresent()) return this;
        return predicate.test(value) ? this : OptionalFloat.empty();
    }

    /**
     * Performs negated filtering on inner value if it is present.
     *
     * @param predicate  a predicate function
     * @return this {@code OptionalFloat} if the value is present and doesn't matches predicate,
     *              otherwise an empty {@code OptionalFloat}
     */
    public OptionalFloat filterNot(FloatAssert predicate) {
        return filter(Function.negate(predicate));
    }

    /**
     * Invokes the given mapping function on inner value if present.
     *
     * @param mapper  mapping function
     * @return an {@code OptionalFloat} with transformed value if present,
     *         otherwise an empty {@code OptionalFloat}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public OptionalFloat map(FloatOperator mapper) {
        if (!isPresent()) {
            return empty();
        }
        Objects.requireNonNull(mapper);
        return OptionalFloat.of(mapper.applyAsFloat(value));
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
    public <U> Optional<U> mapTo(FloatFunctor<U> mapper) {
        if (!isPresent()) {
            return Optional.empty();
        }
        Objects.requireNonNull(mapper);
        return Optional.of(mapper.apply(value));
    }

    /**
     * Returns current {@code OptionalFloat} if value is present, otherwise
     * returns an {@code OptionalFloat} produced by supplier function.
     *
     * @param supplier  supplier function that produces an {@code OptionalFloat} to be returned
     * @return this {@code OptionalFloat} if value is present, otherwise
     *         an {@code OptionalFloat} produced by supplier function
     * @throws NullPointerException if value is not present and
     *         {@code supplier} or value produced by it is {@code null}
     */
    public OptionalFloat or(Producer<OptionalFloat> supplier) {
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
    public float orElse(float other) {
        return isPresent ? value : other;
    }

    /**
     * Returns the value if present, otherwise returns value produced by supplier function.
     *
     * @param other  supplier function that produces value if inner value is not present
     * @return the value if present otherwise the result of {@code other.getAsFloat()}
     * @throws NullPointerException if value is not present and {@code other} is null
     */
    public float orElseGet(FloatProducer other) {
        return isPresent ? value : other.getAsFloat();
    }

    /**
     * Returns inner value if present, otherwise throws {@code NoSuchElementException}.
     *
     * @return inner value if present
     * @throws NoSuchElementException if inner value is not present
     */
    public float orElseThrow() {
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
    public <X extends Throwable> float orElseThrow(Producer<X> exceptionSupplier) throws X {
        if (isPresent) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OptionalFloat)) {
            return false;
        }

        OptionalFloat other = (OptionalFloat) obj;
        return (isPresent && other.isPresent)
                ? Float.compare(value, other.value) == 0
                : isPresent == other.isPresent;
    }

    @Override
    public int hashCode() {
        return isPresent ? (value != 0.0f ? Float.floatToIntBits(value) : 0) : 0;
    }

    @Override
    public String toString() {
        return isPresent
                ? String.format("OptionalFloat[%s]", value)
                : "OptionalFloat.empty";
    }

}
