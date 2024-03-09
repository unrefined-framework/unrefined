/*
 * Copyright (C) 2009 The Android Open Source Project
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

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * Container to ease passing around a tuple of two objects. This object provides a sensible
 * implementation of equals(), returning true if equals() is true on each of the contained
 * objects.
 */
public abstract class Pair<K, V> implements Map.Entry<K, V>, Serializable {

    private static final long serialVersionUID = -577167115970896633L;

    /**
     * Convenience method for creating an appropriately typed value-mutable pair.
     * @param key the first object in the Pair
     * @param value the second object in the pair
     * @return a Pair that is templatized with the types of a and b
     */
    public static <K, V> Pair <K, V> of(K key, V value) {
        return new EntryPair<>(key, value);
    }

    /**
     * Convenience method for creating an appropriately typed mutable pair.
     * @param key the first object in the Pair
     * @param value the second object in the pair
     * @return a Pair that is templatized with the types of a and b
     */
    public static <K, V> Pair <K, V> ofMutable(K key, V value) {
        return new MutablePair<>(key, value);
    }

    /**
     * Convenience method for creating an appropriately typed immutable pair.
     * @param key the first object in the Pair
     * @param value the second object in the pair
     * @return a Pair that is templatized with the types of a and b
     */
    public static <K, V> Pair <K, V> ofImmutable(K key, V value) {
        return new ImmutablePair<>(key, value);
    }

    private static final class EntryPair<K, V> extends Pair<K, V> {
        private static final long serialVersionUID = 4450674093179447052L;
        private final K key;
        private V value;
        public EntryPair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        @Override
        public K setKey(K key) {
            throw new UnsupportedOperationException();
        }
        @Override
        public K getKey() {
            return key;
        }
        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        @Override
        public V getValue() {
            return value;
        }
    }

    private static final class MutablePair<K, V> extends Pair<K, V> {
        private static final long serialVersionUID = 6682898716695947636L;
        private K key;
        private V value;
        public MutablePair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        @Override
        public K setKey(K key) {
            K oldKey = this.key;
            this.key = key;
            return oldKey;
        }
        @Override
        public K getKey() {
            return key;
        }
        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        @Override
        public V getValue() {
            return value;
        }
    }

    private static final class ImmutablePair<K, V> extends Pair<K, V> {
        private static final long serialVersionUID = 1941757611861064699L;
        private final K key;
        private final V value;
        public ImmutablePair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        @Override
        public K setKey(K key) {
            throw new UnsupportedOperationException();
        }
        @Override
        public K getKey() {
            return key;
        }
        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
        @Override
        public V getValue() {
            return value;
        }
    }

    public abstract K setKey(K key);

    /**
     * Checks the two objects for equality by delegating to their respective
     * {@link Object#equals(Object)} methods.
     *
     * @param o the {@link Pair} to which this one is to be checked for equality
     * @return true if the underlying objects of the Pair are both considered
     *         equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Map.Entry<?, ?>)) return false;

        Map.Entry<?, ?> that = (Map.Entry<?, ?>) o;

        if (!Objects.equals(getKey(), that.getKey())) return false;
        return Objects.equals(getValue(), that.getValue());
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the Pair
     */
    @Override
    public int hashCode() {
        K key = getKey();
        V value = getValue();
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }

}
