package unrefined.util;

import unrefined.util.concurrent.DoubleProducer;
import unrefined.util.concurrent.Producer;
import unrefined.util.function.DoubleAssert;
import unrefined.util.function.DoubleFunctor;
import unrefined.util.function.DoubleOperator;
import unrefined.util.function.DoubleSlot;
import unrefined.util.function.Function;
import unrefined.util.function.Functor;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A container object which may or may not contain a {@code double} value.
 *
 * @see Optional
 */
public final class OptionalDouble {

    private static final OptionalDouble EMPTY = new OptionalDouble();

    /**
     * Returns an empty {@code OptionalDouble} instance.
     *
     * @return an empty {@code OptionalDouble}
     */
    public static OptionalDouble empty() {
        return EMPTY;
    }

    /**
     * Returns an {@code OptionalDouble} with the specified value present.
     *
     * @param value  the value to be present
     * @return an {@code OptionalDouble} with the value present
     */
    public static OptionalDouble of(double value) {
        return new OptionalDouble(value);
    }

    /**
     * Returns an {@code OptionalDouble} with the specified value, or empty {@code OptionalDouble} if value is null.
     *
     * @param value the value which can be null
     * @return an {@code OptionalDouble}
     */
    public static OptionalDouble of(Double value) {
        return value == null ? EMPTY : new OptionalDouble(value);
    }

    private final boolean isPresent;
    private final double value;

    private OptionalDouble() {
        this.isPresent = false;
        this.value = 0;
    }

    private OptionalDouble(double value) {
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
    public void ifPresent(DoubleSlot consumer) {
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
    public void ifPresentOrElse(DoubleSlot consumer, Runnable emptyAction) {
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
     * @return this {@code OptionalDouble}
     * @see #ifPresent(DoubleSlot)
     */
    public OptionalDouble executeIfPresent(DoubleSlot consumer) {
        ifPresent(consumer);
        return this;
    }

    /**
     * Invokes action function if value is absent.
     *
     * @param action  action that invokes if value absent
     * @return this {@code OptionalDouble}
     */
    public OptionalDouble executeIfAbsent(Runnable action) {
        if (!isPresent()) {
            action.run();
        }
        return this;
    }

    /**
     * Applies custom operator on {@code OptionalDouble}.
     *
     * @param <R> the type of the result
     * @param function  a transforming function
     * @return a result of the transforming function
     * @throws NullPointerException if {@code function} is null
     */
    public <R> R custom(Functor<OptionalDouble, R> function) {
        Objects.requireNonNull(function);
        return function.apply(this);
    }

    /**
     * Performs filtering on inner value if it is present.
     *
     * @param predicate  a predicate function
     * @return this {@code OptionalDouble} if the value is present and matches predicate,
     *         otherwise an empty {@code OptionalDouble}
     */
    public OptionalDouble filter(DoubleAssert predicate) {
        if (!isPresent()) return this;
        return predicate.test(value) ? this : OptionalDouble.empty();
    }

    /**
     * Performs negated filtering on inner value if it is present.
     *
     * @param predicate  a predicate function
     * @return this {@code OptionalDouble} if the value is present and doesn't matches predicate,
     *              otherwise an empty {@code OptionalDouble}
     */
    public OptionalDouble filterNot(DoubleAssert predicate) {
        return filter(Function.negate(predicate));
    }

    /**
     * Invokes the given mapping function on inner value if present.
     *
     * @param mapper  mapping function
     * @return an {@code OptionalDouble} with transformed value if present,
     *         otherwise an empty {@code OptionalDouble}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public OptionalDouble map(DoubleOperator mapper) {
        if (!isPresent()) {
            return empty();
        }
        Objects.requireNonNull(mapper);
        return OptionalDouble.of(mapper.applyAsDouble(value));
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
    public <U> Optional<U> mapTo(DoubleFunctor<U> mapper) {
        if (!isPresent()) {
            return Optional.empty();
        }
        Objects.requireNonNull(mapper);
        return Optional.of(mapper.apply(value));
    }

    /**
     * Returns current {@code OptionalDouble} if value is present, otherwise
     * returns an {@code OptionalDouble} produced by supplier function.
     *
     * @param supplier  supplier function that produces an {@code OptionalDouble} to be returned
     * @return this {@code OptionalDouble} if value is present, otherwise
     *         an {@code OptionalDouble} produced by supplier function
     * @throws NullPointerException if value is not present and
     *         {@code supplier} or value produced by it is {@code null}
     */
    public OptionalDouble or(Producer<OptionalDouble> supplier) {
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
    public double orElse(double other) {
        return isPresent ? value : other;
    }

    /**
     * Returns the value if present, otherwise returns value produced by supplier function.
     *
     * @param other  supplier function that produces value if inner value is not present
     * @return the value if present otherwise the result of {@code other.getAsDouble()}
     * @throws NullPointerException if value is not present and {@code other} is null
     */
    public double orElseGet(DoubleProducer other) {
        return isPresent ? value : other.getAsDouble();
    }

    /**
     * Returns inner value if present, otherwise throws {@code NoSuchElementException}.
     *
     * @return inner value if present
     * @throws NoSuchElementException if inner value is not present
     */
    public double orElseThrow() {
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
    public <X extends Throwable> double orElseThrow(Producer<X> exceptionSupplier) throws X {
        if (isPresent) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof OptionalDouble)) {
            return false;
        }

        OptionalDouble other = (OptionalDouble) obj;
        return (isPresent && other.isPresent)
                ? Double.compare(value, other.value) == 0
                : isPresent == other.isPresent;
    }

    @Override
    public int hashCode() {
        if (isPresent) {
            long temp = Double.doubleToLongBits(value);
            return (int) (temp ^ temp >>> 32);
        }
        else return 0;
    }

    @Override
    public String toString() {
        return isPresent
                ? String.format("OptionalDouble[%s]", value)
                : "OptionalDouble.empty";
    }

}
