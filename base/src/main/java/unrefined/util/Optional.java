package unrefined.util;

import unrefined.util.concurrent.Producer;
import unrefined.util.function.Assert;
import unrefined.util.function.Function;
import unrefined.util.function.Functor;
import unrefined.util.function.Slot;
import unrefined.util.function.ToBooleanFunctor;
import unrefined.util.function.ToByteFunctor;
import unrefined.util.function.ToCharFunctor;
import unrefined.util.function.ToDoubleFunctor;
import unrefined.util.function.ToFloatFunctor;
import unrefined.util.function.ToIntFunctor;
import unrefined.util.function.ToLongFunctor;
import unrefined.util.function.ToShortFunctor;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A container object which may or may not contain a non-null value.
 *
 * @param <T> the type of the inner value
 */
public class Optional<T> {

    private static final Optional<?> EMPTY = new Optional<>();
    
    /**
     * Returns an {@code Optional} with the specified value, or empty {@code Optional} if value is null.
     *
     * @param <T> the type of value
     * @param value  the value which can be null
     * @return an {@code Optional}
     */
    public static <T> Optional<T> of(T value) {
        return value == null ? Optional.empty() : new Optional<>(value);
    }

    /**
     * Returns an empty {@code Optional}.
     *
     * @param <T> the type of value
     * @return an {@code Optional}
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> empty() {
        return (Optional<T>) EMPTY;
    }
    
    private final T value;

    private Optional() {
        this.value = null;
    }

    private Optional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Checks value present.
     *
     * @return {@code true} if a value present, {@code false} otherwise
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Checks the value is not present.
     *
     * @return {@code true} if a value is not present, {@code false} otherwise
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * Invokes consumer function with value if present.
     *
     * @param consumer  the consumer function
     */
    public void ifPresent(Slot<? super T> consumer) {
        if (value != null)
            consumer.accept(value);
    }

    /**
     * If a value is present, performs the given action with the value, otherwise performs the given empty-based action.
     *
     * @param consumer  the consumer function to be executed, if a value is present
     * @param emptyAction  the empty-based action to be performed, if no value is present
     *
     * @throws NullPointerException if a value is present and the given consumer function is null,
     *         or no value is present and the given empty-based action is null.
     */
    public void ifPresentOrElse(Slot<? super T> consumer, Runnable emptyAction) {
        if (value != null) {
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
     * @return this {@code Optional}
     * @see #ifPresent(Slot)
     */
    public Optional<T> executeIfPresent(Slot<? super T> consumer) {
        ifPresent(consumer);
        return this;
    }

    /**
     * Invokes action function if value is absent.
     *
     * @param action  action that invokes if value absent
     * @return this {@code Optional}
     */
    public Optional<T> executeIfAbsent(Runnable action) {
        if (value == null)
            action.run();
        return this;
    }

    /**
     * Applies custom operator on {@code Optional}.
     *
     * @param <R> the type of the result
     * @param function  a transforming function
     * @return a result of the transforming function
     * @throws NullPointerException if {@code function} is null
     */
    public <R> R custom(Functor<Optional<T>, R> function) {
        Objects.requireNonNull(function);
        return function.apply(this);
    }

    /**
     * Performs filtering on inner value if it is present.
     *
     * @param predicate  a predicate function
     * @return this {@code Optional} if the value is present and matches predicate,
     *              otherwise an empty {@code Optional}
     */
    public Optional<T> filter(Assert<? super T> predicate) {
        if (!isPresent()) return this;
        return predicate.test(value) ? this : Optional.empty();
    }

    /**
     * Performs negated filtering on inner value if it is present.
     *
     * @param predicate  a predicate function
     * @return this {@code Optional} if the value is present and doesn't matches predicate,
     *              otherwise an empty {@code Optional}
     */
    public Optional<T> filterNot(Assert<? super T> predicate) {
        return filter(Function.negate(predicate));
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
    public <U> Optional<U> map(Functor<? super T, ? extends U> mapper) {
        if (!isPresent()) return empty();
        return Optional.of(mapper.apply(value));
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
    public OptionalByte mapToByte(ToByteFunctor<? super T> mapper) {
        if (!isPresent()) return OptionalByte.empty();
        return OptionalByte.of(mapper.applyAsByte(value));
    }

    /**
     * Invokes the given mapping function on inner value if present.
     *
     * @param mapper  mapping function
     * @return an {@code OptionalChar} with transformed value if present,
     *         otherwise an empty {@code OptionalChar}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public OptionalChar mapToChar(ToCharFunctor<? super T> mapper) {
        if (!isPresent()) return OptionalChar.empty();
        return OptionalChar.of(mapper.applyAsChar(value));
    }

    /**
     * Invokes the given mapping function on inner value if present.
     *
     * @param mapper  mapping function
     * @return an {@code OptionalShort} with transformed value if present,
     *         otherwise an empty {@code OptionalShort}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public OptionalShort mapToShort(ToShortFunctor<? super T> mapper) {
        if (!isPresent()) return OptionalShort.empty();
        return OptionalShort.of(mapper.applyAsShort(value));
    }

    /**
     * Invokes the given mapping function on inner value if present.
     *
     * @param mapper  mapping function
     * @return an {@code OptionalInt} with transformed value if present,
     *         otherwise an empty {@code OptionalInt}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public OptionalInt mapToInt(ToIntFunctor<? super T> mapper) {
        if (!isPresent()) return OptionalInt.empty();
        return OptionalInt.of(mapper.applyAsInt(value));
    }

    /**
     * Invokes mapping function on inner value if present.
     *
     * @param mapper  mapping function
     * @return an {@code OptionalLong} with transformed value if present,
     *         otherwise an empty {@code OptionalLong}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public OptionalLong mapToLong(ToLongFunctor<? super T> mapper) {
        if (!isPresent()) return OptionalLong.empty();
        return OptionalLong.of(mapper.applyAsLong(value));
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
    public OptionalFloat mapToFloat(ToFloatFunctor<? super T> mapper) {
        if (!isPresent()) return OptionalFloat.empty();
        return OptionalFloat.of(mapper.applyAsFloat(value));
    }

    /**
     * Invokes mapping function on inner value if present.
     *
     * @param mapper  mapping function
     * @return an {@code OptionalDouble} with transformed value if present,
     *         otherwise an empty {@code OptionalDouble}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public OptionalDouble mapToDouble(ToDoubleFunctor<? super T> mapper) {
        if (!isPresent()) return OptionalDouble.empty();
        return OptionalDouble.of(mapper.applyAsDouble(value));
    }

    /**
     * Invokes mapping function on inner value if present.
     *
     * @param mapper  mapping function
     * @return an {@code OptionalBoolean} with transformed value if present,
     *         otherwise an empty {@code OptionalBoolean}
     * @throws NullPointerException if value is present and
     *         {@code mapper} is {@code null}
     */
    public OptionalBoolean mapToBoolean(ToBooleanFunctor<? super T> mapper) {
        if (!isPresent()) return OptionalBoolean.empty();
        return OptionalBoolean.of(mapper.applyAsBoolean(value));
    }

    /**
     * Invokes mapping function with {@code Optional} result if value is present.
     *
     * @param <U> the type of result value
     * @param mapper  mapping function
     * @return an {@code Optional} with transformed value if present, otherwise an empty {@code Optional}
     */
    public <U> Optional<U> flatMap(Functor<? super T, Optional<U>> mapper) {
        if (!isPresent()) return empty();
        return Objects.requireNonNull(mapper.apply(value));
    }

    /**
     * Keeps inner value only if is present and instance of given class.
     *
     * @param <R> a type of instance to select.
     * @param clazz a class which instance should be selected
     * @return an {@code Optional} with value of type class if present, otherwise an empty {@code Optional}
     */
    @SuppressWarnings("unchecked")
    public <R> Optional<R> select(Class<R> clazz) {
        Objects.requireNonNull(clazz);
        if (!isPresent()) return empty();
        return (Optional<R>) Optional.of(clazz.isInstance(value) ? value : null);
    }

    /**
     * Returns current {@code Optional} if value is present, otherwise
     * returns an {@code Optional} produced by supplier function.
     *
     * @param supplier  supplier function that produces an {@code Optional} to be returned
     * @return this {@code Optional} if value is present, otherwise
     *         an {@code Optional} produced by supplier function
     * @throws NullPointerException if value is not present and
     *         {@code supplier} or value produced by it is {@code null}
     */
    public Optional<T> or(Producer<Optional<T>> supplier) {
        if (isPresent()) return this;
        Objects.requireNonNull(supplier);
        return Objects.requireNonNull(supplier.get());
    }

    /**
     * Returns inner value if present, otherwise returns {@code other}.
     *
     * @param other  the value to be returned if inner value is not present
     * @return inner value if present, otherwise {@code other}
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * Returns inner value if present, otherwise returns value produced by supplier function.
     *
     * @param other  supplier function that produces value if inner value is not present
     * @return inner value if present, otherwise value produced by supplier function
     */
    public T orElseGet(Producer<? extends T> other) {
        return value != null ? value : other.get();
    }

    /**
     * Returns inner value if present, otherwise throws {@code NoSuchElementException}.
     *
     * @return inner value if present
     * @throws NoSuchElementException if inner value is not present
     */
    public T orElseThrow() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * Returns inner value if present, otherwise throws the exception provided by supplier function.
     *
     * @param <X> the type of exception to be thrown
     * @param exc  supplier function that produces an exception to be thrown
     * @return inner value if present
     * @throws X if inner value is not present
     */
    public <X extends Throwable> T orElseThrow(Producer<? extends X> exc) throws X {
        if (value != null) return value;
        else throw exc.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Optional)) {
            return false;
        }

        Optional<?> other = (Optional<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value != null
            ? String.format("Optional[%s]", value)
            : "Optional.empty";
    }

}
