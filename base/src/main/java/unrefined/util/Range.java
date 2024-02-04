/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package unrefined.util;

import java.util.Objects;

/**
 * Immutable class for describing the range of two numeric values.
 * <p>
 * A range (or "interval") defines the inclusive boundaries around a contiguous span of
 * values of some {@link Comparable} type; for example,
 * "integers from 1 to 100 inclusive."
 * </p>
 * <p>
 * All ranges are bounded, and the left side of the range is always {@code <=}
 * the right side of the range.
 * </p>
 *
 * <p>Although the implementation itself is immutable, there is no restriction that objects
 * stored must also be immutable. If mutable objects are stored here, then the range
 * effectively becomes mutable. </p>
 */
public final class Range<T extends Comparable<T>> {

    private final T lowerEndpoint;
    private final T upperEndpoint;

    /**
     * Create a new immutable range.
     *
     * <p>
     * The endpoints are {@code [lower, upper]}; that
     * is the range is bounded. {@code lower} must be {@link Comparable#compareTo lesser or equal}
     * to {@code upper}.
     * </p>
     *
     * @param lower The lower endpoint (inclusive)
     * @param upper The upper endpoint (inclusive)
     *
     * @throws NullPointerException if {@code lower} or {@code upper} is {@code null}
     */
    public Range(T lower, T upper) {
        lowerEndpoint = Objects.requireNonNull(lower, "lower must not be null");
        upperEndpoint = Objects.requireNonNull(upper, "upper must not be null");

        if (lower.compareTo(upper) > 0) {
            throw new IllegalArgumentException("lower must be less than or equal to upper");
        }
    }

    /**
     * Create a new immutable range, with the argument types inferred.
     *
     * <p>
     * The endpoints are {@code [lower, upper]}; that
     * is the range is bounded. {@code lower} must be {@link Comparable#compareTo lesser or equal}
     * to {@code upper}.
     * </p>
     *
     * @param lower The lower endpoint (inclusive)
     * @param upper The upper endpoint (inclusive)
     *
     * @throws NullPointerException if {@code lower} or {@code upper} is {@code null}
     */
    public static <T extends Comparable<T>> Range<T> of(T lower, T upper) {
        return new Range<>(lower, upper);
    }

    /**
     * Get the lower endpoint.
     *
     * @return a non-{@code null} {@code T} reference
     */
    public T getLower() {
        return lowerEndpoint;
    }

    /**
     * Get the upper endpoint.
     *
     * @return a non-{@code null} {@code T} reference
     */
    public T getUpper() {
        return upperEndpoint;
    }

    /**
     * Checks if the {@code value} is within the bounds of this range.
     *
     * <p>A value is considered to be within this range if it's {@code >=}
     * the lower endpoint <i>and</i> {@code <=} the upper endpoint (using the {@link Comparable}
     * interface.)</p>
     *
     * @param value a non-{@code null} {@code T} reference
     * @return {@code true} if the value is within this inclusive range, {@code false} otherwise
     *
     * @throws NullPointerException if {@code value} was {@code null}
     */
    public boolean contains(T value) {
        Objects.requireNonNull(value, "value must not be null");

        boolean gteLower = value.compareTo(lowerEndpoint) >= 0;
        boolean lteUpper  = value.compareTo(upperEndpoint) <= 0;

        return gteLower && lteUpper;
    }

    /**
     * Checks if another {@code range} is within the bounds of this range.
     *
     * <p>A range is considered to be within this range if both of its endpoints
     * are within this range.</p>
     *
     * @param range a non-{@code null} {@code T} reference
     * @return {@code true} if the range is within this inclusive range, {@code false} otherwise
     *
     * @throws NullPointerException if {@code range} was {@code null}
     */
    public boolean contains(Range<T> range) {
        Objects.requireNonNull(range, "value must not be null");

        boolean gteLower = range.lowerEndpoint.compareTo(lowerEndpoint) >= 0;
        boolean lteUpper = range.upperEndpoint.compareTo(upperEndpoint) <= 0;

        return gteLower && lteUpper;
    }

    /**
     * Compare two ranges for equality.
     *
     * <p>A range is considered equal if and only if both the lower and upper endpoints
     * are also equal.</p>
     *
     * @return {@code true} if the ranges are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (obj instanceof Range) {
            @SuppressWarnings("rawtypes")
            Range other = (Range) obj;
            return lowerEndpoint.equals(other.lowerEndpoint) && upperEndpoint.equals(other.upperEndpoint);
        }
        return false;
    }

    /**
     * Clamps {@code value} to this range.
     *
     * <p>If the value is within this range, it is returned.  Otherwise, if it
     * is {@code <} than the lower endpoint, the lower endpoint is returned,
     * else the upper endpoint is returned. Comparisons are performed using the
     * {@link Comparable} interface.</p>
     *
     * @param value a non-{@code null} {@code T} reference
     * @return {@code value} clamped to this range.
     */
    public T clamp(T value) {
        Objects.requireNonNull(value, "value must not be null");

        if (value.compareTo(lowerEndpoint) < 0) {
            return lowerEndpoint;
        } else if (value.compareTo(upperEndpoint) > 0) {
            return upperEndpoint;
        } else {
            return value;
        }
    }

    /**
     * Returns the intersection of this range and another {@code range}.
     * <p>
     * E.g. if a {@code <} b {@code <} c {@code <} d, the
     * intersection of [a, c] and [b, d] ranges is [b, c].
     * As the endpoints are object references, there is no guarantee
     * which specific endpoint reference is used from the input ranges:</p>
     * <p>
     * E.g. if a {@code ==} a' {@code <} b {@code <} c, the
     * intersection of [a, b] and [a', c] ranges could be either
     * [a, b] or ['a, b], where [a, b] could be either the exact
     * input range, or a newly created range with the same endpoints.</p>
     *
     * @param range a non-{@code null} {@code Range<T>} reference
     * @return the intersection of this range and the other range.
     *
     * @throws NullPointerException if {@code range} was {@code null}
     * @throws IllegalArgumentException if the ranges are disjoint.
     */
    public Range<T> intersect(Range<T> range) {
        Objects.requireNonNull(range, "range must not be null");

        int cmpLower = range.lowerEndpoint.compareTo(lowerEndpoint);
        int cmpUpper = range.upperEndpoint.compareTo(upperEndpoint);

        if (cmpLower <= 0 && cmpUpper >= 0) {
            // range includes this
            return this;
        } else if (cmpLower >= 0 && cmpUpper <= 0) {
            // this inludes range
            return range;
        } else {
            return Range.of(
                    cmpLower <= 0 ? lowerEndpoint : range.lowerEndpoint,
                    cmpUpper >= 0 ? upperEndpoint : range.upperEndpoint);
        }
    }

    /**
     * Returns the intersection of this range and the inclusive range
     * specified by {@code [lower, upper]}.
     * <p>
     * See {@link #intersect(Range)} for more details.</p>
     *
     * @param lower a non-{@code null} {@code T} reference
     * @param upper a non-{@code null} {@code T} reference
     * @return the intersection of this range and the other range
     *
     * @throws NullPointerException if {@code lower} or {@code upper} was {@code null}
     * @throws IllegalArgumentException if the ranges are disjoint.
     */
    public Range<T> intersect(T lower, T upper) {
        Objects.requireNonNull(lower, "lower must not be null");
        Objects.requireNonNull(upper, "upper must not be null");

        int cmpLower = lower.compareTo(lowerEndpoint);
        int cmpUpper = upper.compareTo(upperEndpoint);

        if (cmpLower <= 0 && cmpUpper >= 0) {
            // [lower, upper] includes this
            return this;
        } else {
            return Range.of(
                    cmpLower <= 0 ? lowerEndpoint : lower,
                    cmpUpper >= 0 ? upperEndpoint : upper);
        }
    }

    /**
     * Returns the smallest range that includes this range and
     * another {@code range}.
     * <p>
     * E.g. if a {@code <} b {@code <} c {@code <} d, the
     * extension of [a, c] and [b, d] ranges is [a, d].
     * As the endpoints are object references, there is no guarantee
     * which specific endpoint reference is used from the input ranges:</p>
     * <p>
     * E.g. if a {@code ==} a' {@code <} b {@code <} c, the
     * extension of [a, b] and [a', c] ranges could be either
     * [a, c] or ['a, c], where ['a, c] could be either the exact
     * input range, or a newly created range with the same endpoints.</p>
     *
     * @param range a non-{@code null} {@code Range<T>} reference
     * @return the extension of this range and the other range.
     *
     * @throws NullPointerException if {@code range} was {@code null}
     */
    public Range<T> extend(Range<T> range) {
        Objects.requireNonNull(range, "range must not be null");

        int cmpLower = range.lowerEndpoint.compareTo(lowerEndpoint);
        int cmpUpper = range.upperEndpoint.compareTo(upperEndpoint);

        if (cmpLower <= 0 && cmpUpper >= 0) {
            // other includes this
            return range;
        } else if (cmpLower >= 0 && cmpUpper <= 0) {
            // this inludes other
            return this;
        } else {
            return Range.of(
                    cmpLower >= 0 ? lowerEndpoint : range.lowerEndpoint,
                    cmpUpper <= 0 ? upperEndpoint : range.upperEndpoint);
        }
    }

    /**
     * Returns the smallest range that includes this range and
     * the inclusive range specified by {@code [lower, upper]}.
     * <p>
     * See {@link #extend(Range)} for more details.</p>
     *
     * @param lower a non-{@code null} {@code T} reference
     * @param upper a non-{@code null} {@code T} reference
     * @return the extension of this range and the other range.
     *
     * @throws NullPointerException if {@code lower} or {@code
     *                              upper} was {@code null}
     */
    public Range<T> extend(T lower, T upper) {
        Objects.requireNonNull(lower, "lower must not be null");
        Objects.requireNonNull(upper, "upper must not be null");

        int cmpLower = lower.compareTo(lowerEndpoint);
        int cmpUpper = upper.compareTo(upperEndpoint);

        if (cmpLower >= 0 && cmpUpper <= 0) {
            // this inludes other
            return this;
        } else {
            return Range.of(
                    cmpLower >= 0 ? lowerEndpoint : lower,
                    cmpUpper <= 0 ? upperEndpoint : upper);
        }
    }

    /**
     * Returns the smallest range that includes this range and
     * the {@code value}.
     * <p>
     * See {@link #extend(Range)} for more details, as this method is
     * equivalent to {@code extend(Range.create(value, value))}.</p>
     *
     * @param value a non-{@code null} {@code T} reference
     * @return the extension of this range and the value.
     *
     * @throws NullPointerException if {@code value} was {@code null}
     */
    public Range<T> extend(T value) {
        Objects.requireNonNull(value, "value must not be null");
        return extend(value, value);
    }

    /**
     * Return the range as a string representation {@code "[lower, upper]"}.
     *
     * @return string representation of the range
     */
    @Override
    public String toString() {
        return String.format("[%s, %s]", lowerEndpoint, upperEndpoint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = lowerEndpoint.hashCode();
        result = 31 * result + upperEndpoint.hashCode();
        return result;
    }

}
