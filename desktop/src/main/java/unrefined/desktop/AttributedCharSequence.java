/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package unrefined.desktop;

import java.text.Annotation;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * Holds a CharSequence with attributes describing the characters of
 * this CharSequence.
 */
public class AttributedCharSequence {

    public CharSequence getText() {
        return text;
    }

    CharSequence text;

    Map<Attribute, List<Range>> attributeMap;

    static class Range {
        int start;

        int end;

        Object value;

        Range(int s, int e, Object v) {
            start = s;
            end = e;
            value = v;
        }
    }

    static class AttributedIterator implements AttributedCharacterIterator {

        private final int begin;
        private final int end;
        private int offset;

        private final AttributedCharSequence attrCharSequence;

        private HashSet<Attribute> attributesAllowed;

        AttributedIterator(AttributedCharSequence attrCharSequence) {
            this.attrCharSequence = attrCharSequence;
            begin = 0;
            end = attrCharSequence.text.length();
            offset = 0;
        }

        AttributedIterator(AttributedCharSequence attrCharSequence,
                           Attribute[] attributes, int begin,
                           int end) {
            if (begin < 0 || end > attrCharSequence.text.length() || begin > end) {
                throw new IllegalArgumentException();
            }
            this.begin = begin;
            this.end = end;
            offset = begin;
            this.attrCharSequence = attrCharSequence;
            if (attributes != null) {
                HashSet<Attribute> set = new HashSet<Attribute>(
                        (attributes.length * 4 / 3) + 1);
                for (int i = attributes.length; --i >= 0;) {
                    set.add(attributes[i]);
                }
                attributesAllowed = set;
            }
        }

        /**
         * Returns a new {@code AttributedIterator} with the same source CharSequence,
         * begin, end, and current index as this attributed iterator.
         * 
         * @return a shallow copy of this attributed iterator.
         * @see Cloneable
         */
        @Override
        @SuppressWarnings("unchecked")
        public Object clone() {
            try {
                AttributedIterator clone = (AttributedIterator) super.clone();
                if (attributesAllowed != null) {
                    clone.attributesAllowed = (HashSet<Attribute>) attributesAllowed
                            .clone();
                }
                return clone;
            } catch (CloneNotSupportedException e) {
                return null;
            }
        }

        public char current() {
            if (offset == end) {
                return DONE;
            }
            return attrCharSequence.text.charAt(offset);
        }

        public char first() {
            if (begin == end) {
                return DONE;
            }
            offset = begin;
            return attrCharSequence.text.charAt(offset);
        }

        /**
         * Returns the begin index in the source CharSequence.
         * 
         * @return the index of the first character to iterate.
         */
        public int getBeginIndex() {
            return begin;
        }

        /**
         * Returns the end index in the source CharSequence.
         * 
         * @return the index one past the last character to iterate.
         */
        public int getEndIndex() {
            return end;
        }

        /**
         * Returns the current index in the source CharSequence.
         * 
         * @return the current index.
         */
        public int getIndex() {
            return offset;
        }

        private boolean inRange(Range range) {
            if (!(range.value instanceof Annotation)) {
                return true;
            }
            return range.start >= begin && range.start < end
                    && range.end > begin && range.end <= end;
        }

        private boolean inRange(List<Range> ranges) {
            Iterator<Range> it = ranges.iterator();
            while (it.hasNext()) {
                Range range = it.next();
                if (range.start >= begin && range.start < end) {
                    return !(range.value instanceof Annotation)
                            || (range.end > begin && range.end <= end);
                } else if (range.end > begin && range.end <= end) {
                    return !(range.value instanceof Annotation)
                            || (range.start >= begin && range.start < end);
                }
            }
            return false;
        }

        /**
         * Returns a set of attributes present in the {@code AttributedCharSequence}.
         * An empty set returned indicates that no attributes where defined.
         *
         * @return a set of attribute keys that may be empty.
         */
        public Set<Attribute> getAllAttributeKeys() {
            if (begin == 0 && end == attrCharSequence.text.length()
                    && attributesAllowed == null) {
                return attrCharSequence.attributeMap.keySet();
            }

            Set<Attribute> result = new HashSet<Attribute>(
                    (attrCharSequence.attributeMap.size() * 4 / 3) + 1);
            Iterator<Map.Entry<Attribute, List<Range>>> it = attrCharSequence.attributeMap
                    .entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Attribute, List<Range>> entry = it.next();
                if (attributesAllowed == null
                        || attributesAllowed.contains(entry.getKey())) {
                    List<Range> ranges = entry.getValue();
                    if (inRange(ranges)) {
                        result.add(entry.getKey());
                    }
                }
            }
            return result;
        }

        private Object currentValue(List<Range> ranges) {
            Iterator<Range> it = ranges.iterator();
            while (it.hasNext()) {
                Range range = it.next();
                if (offset >= range.start && offset < range.end) {
                    return inRange(range) ? range.value : null;
                }
            }
            return null;
        }

        public Object getAttribute(
                Attribute attribute) {
            if (attributesAllowed != null
                    && !attributesAllowed.contains(attribute)) {
                return null;
            }
            ArrayList<Range> ranges = (ArrayList<Range>) attrCharSequence.attributeMap
                    .get(attribute);
            if (ranges == null) {
                return null;
            }
            return currentValue(ranges);
        }

        public Map<Attribute, Object> getAttributes() {
            Map<Attribute, Object> result = new HashMap<Attribute, Object>(
                    (attrCharSequence.attributeMap.size() * 4 / 3) + 1);
            Iterator<Map.Entry<Attribute, List<Range>>> it = attrCharSequence.attributeMap
                    .entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Attribute, List<Range>> entry = it.next();
                if (attributesAllowed == null
                        || attributesAllowed.contains(entry.getKey())) {
                    Object value = currentValue(entry.getValue());
                    if (value != null) {
                        result.put(entry.getKey(), value);
                    }
                }
            }
            return result;
        }

        public int getRunLimit() {
            return getRunLimit(getAllAttributeKeys());
        }

        private int runLimit(List<Range> ranges) {
            int result = end;
            ListIterator<Range> it = ranges.listIterator(ranges.size());
            while (it.hasPrevious()) {
                Range range = it.previous();
                if (range.end <= begin) {
                    break;
                }
                if (offset >= range.start && offset < range.end) {
                    return inRange(range) ? range.end : result;
                } else if (offset >= range.end) {
                    break;
                }
                result = range.start;
            }
            return result;
        }

        public int getRunLimit(Attribute attribute) {
            if (attributesAllowed != null
                    && !attributesAllowed.contains(attribute)) {
                return end;
            }
            ArrayList<Range> ranges = (ArrayList<Range>) attrCharSequence.attributeMap
                    .get(attribute);
            if (ranges == null) {
                return end;
            }
            return runLimit(ranges);
        }

        public int getRunLimit(Set<? extends Attribute> attributes) {
            int limit = end;
            Iterator<? extends Attribute> it = attributes.iterator();
            while (it.hasNext()) {
                Attribute attribute = it.next();
                int newLimit = getRunLimit(attribute);
                if (newLimit < limit) {
                    limit = newLimit;
                }
            }
            return limit;
        }

        public int getRunStart() {
            return getRunStart(getAllAttributeKeys());
        }

        private int runStart(List<Range> ranges) {
            int result = begin;
            Iterator<Range> it = ranges.iterator();
            while (it.hasNext()) {
                Range range = it.next();
                if (range.start >= end) {
                    break;
                }
                if (offset >= range.start && offset < range.end) {
                    return inRange(range) ? range.start : result;
                } else if (offset < range.start) {
                    break;
                }
                result = range.end;
            }
            return result;
        }

        public int getRunStart(Attribute attribute) {
            if (attributesAllowed != null
                    && !attributesAllowed.contains(attribute)) {
                return begin;
            }
            ArrayList<Range> ranges = (ArrayList<Range>) attrCharSequence.attributeMap
                    .get(attribute);
            if (ranges == null) {
                return begin;
            }
            return runStart(ranges);
        }

        public int getRunStart(Set<? extends Attribute> attributes) {
            int start = begin;
            Iterator<? extends Attribute> it = attributes.iterator();
            while (it.hasNext()) {
                Attribute attribute = it.next();
                int newStart = getRunStart(attribute);
                if (newStart > start) {
                    start = newStart;
                }
            }
            return start;
        }

        public char last() {
            if (begin == end) {
                return DONE;
            }
            offset = end - 1;
            return attrCharSequence.text.charAt(offset);
        }

        public char next() {
            if (offset >= (end - 1)) {
                offset = end;
                return DONE;
            }
            return attrCharSequence.text.charAt(++offset);
        }

        public char previous() {
            if (offset == begin) {
                return DONE;
            }
            return attrCharSequence.text.charAt(--offset);
        }

        public char setIndex(int location) {
            if (location < begin || location > end) {
                throw new IllegalArgumentException();
            }
            offset = location;
            if (offset == end) {
                return DONE;
            }
            return attrCharSequence.text.charAt(offset);
        }
    }

    /**
     * Creates an {@code AttributedCharSequence} from the given text.
     *
     * @param value
     *            the text to take as base for this attributed CharSequence.
     */
    public AttributedCharSequence(CharSequence value) {
        if (value == null) {
            throw new NullPointerException();
        }
        text = value;
        attributeMap = new HashMap<Attribute, List<Range>>(11);
    }

    /**
     * Creates an {@code AttributedCharSequence} from the given text and the
     * attributes. The whole text has the given attributes applied.
     *
     * @param value
     *            the text to take as base for this attributed CharSequence.
     * @param attributes
     *            the attributes that the text is associated with.
     * @throws IllegalArgumentException
     *             if the length of {@code value} is 0 but the size of {@code
     *             attributes} is greater than 0.
     * @throws NullPointerException
     *             if {@code value} is {@code null}.
     */
    public AttributedCharSequence(CharSequence value,
                                  Map<? extends Attribute, ?> attributes) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (value.length() == 0 && !attributes.isEmpty()) {
            throw new IllegalArgumentException("Cannot add attributes to empty CharSequence"); //$NON-NLS-1$
        }
        text = value;
        attributeMap = new HashMap<Attribute, List<Range>>(
                (attributes.size() * 4 / 3) + 1);
        Iterator<?> it = attributes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
            ArrayList<Range> ranges = new ArrayList<Range>(1);
            ranges.add(new Range(0, text.length(), entry.getValue()));
            attributeMap.put((Attribute) entry
                    .getKey(), ranges);
        }
    }

    /**
     * Applies a given attribute to this CharSequence.
     *
     * @param attribute
     *            the attribute that will be applied to this CharSequence.
     * @param value
     *            the value of the attribute that will be applied to this
     *            CharSequence.
     * @throws IllegalArgumentException
     *             if the length of this attributed CharSequence is 0.
     * @throws NullPointerException
     *             if {@code attribute} is {@code null}.
     */
    public void addAttribute(Attribute attribute,
                             Object value) {
        if (null == attribute) {
            throw new NullPointerException();
        }
        if (text.length() == 0) {
            throw new IllegalArgumentException();
        }

        List<Range> ranges = attributeMap.get(attribute);
        if (ranges == null) {
            ranges = new ArrayList<Range>(1);
            attributeMap.put(attribute, ranges);
        } else {
            ranges.clear();
        }
        ranges.add(new Range(0, text.length(), value));
    }

    /**
     * Applies a given attribute to the given range of this CharSequence.
     *
     * @param attribute
     *            the attribute that will be applied to this CharSequence.
     * @param value
     *            the value of the attribute that will be applied to this
     *            CharSequence.
     * @param start
     *            the start of the range where the attribute will be applied.
     * @param end
     *            the end of the range where the attribute will be applied.
     * @throws IllegalArgumentException
     *             if {@code start < 0}, {@code end} is greater than the length
     *             of this CharSequence, or if {@code start >= end}.
     * @throws NullPointerException
     *             if {@code attribute} is {@code null}.
     */
    public void addAttribute(Attribute attribute,
                             Object value, int start, int end) {
        if (null == attribute) {
            throw new NullPointerException();
        }
        if (start < 0 || end > text.length() || start >= end) {
            throw new IllegalArgumentException();
        }

        if (value == null) {
            return;
        }

        List<Range> ranges = attributeMap.get(attribute);
        if (ranges == null) {
            ranges = new ArrayList<Range>(1);
            ranges.add(new Range(start, end, value));
            attributeMap.put(attribute, ranges);
            return;
        }
        ListIterator<Range> it = ranges.listIterator();
        while (it.hasNext()) {
            Range range = it.next();
            if (end <= range.start) {
                it.previous();
                break;
            } else if (start < range.end
                    || (start == range.end && value.equals(range.value))) {
                Range r1 = null, r3;
                it.remove();
                r1 = new Range(range.start, start, range.value);
                r3 = new Range(end, range.end, range.value);

                while (end > range.end && it.hasNext()) {
                    range = it.next();
                    if (end <= range.end) {
                        if (end > range.start
                                || (end == range.start && value.equals(range.value))) {
                            it.remove();
                            r3 = new Range(end, range.end, range.value);
                            break;
                        }
                    } else {
                        it.remove();
                    }
                }

                if (value.equals(r1.value)) {
                    if (value.equals(r3.value)) {
                        it.add(new Range(r1.start < start ? r1.start : start,
                                r3.end > end ? r3.end : end, r1.value));
                    } else {
                        it.add(new Range(r1.start < start ? r1.start : start,
                                end, r1.value));
                        if (r3.start < r3.end) {
                            it.add(r3);
                        }
                    }
                } else {
                    if (value.equals(r3.value)) {
                        if (r1.start < r1.end) {
                            it.add(r1);
                        }
                        it.add(new Range(start, r3.end > end ? r3.end : end,
                                r3.value));
                    } else {
                        if (r1.start < r1.end) {
                            it.add(r1);
                        }
                        it.add(new Range(start, end, value));
                        if (r3.start < r3.end) {
                            it.add(r3);
                        }
                    }
                }
                return;
            }
        }
        it.add(new Range(start, end, value));
    }

    /**
     * Applies a given set of attributes to the given range of the CharSequence.
     *
     * @param attributes
     *            the set of attributes that will be applied to this CharSequence.
     * @param start
     *            the start of the range where the attribute will be applied.
     * @param end
     *            the end of the range where the attribute will be applied.
     * @throws IllegalArgumentException
     *             if {@code start < 0}, {@code end} is greater than the length
     *             of this CharSequence, or if {@code start >= end}.
     */
    public void addAttributes(
            Map<? extends Attribute, ?> attributes,
            int start, int end) {
        Iterator<?> it = attributes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) it.next();
            addAttribute(
                    (Attribute) entry.getKey(),
                    entry.getValue(), start, end);
        }
    }

    /**
     * Returns an {@code AttributedCharacterIterator} that gives access to the
     * complete content of this attributed CharSequence.
     *
     * @return the newly created {@code AttributedCharacterIterator}.
     */
    public AttributedCharacterIterator getIterator() {
        return new AttributedIterator(this);
    }

    /**
     * Returns an {@code AttributedCharacterIterator} that gives access to the
     * complete content of this attributed CharSequence. Only attributes contained in
     * {@code attributes} are available from this iterator if they are defined
     * for this text.
     *
     * @param attributes
     *            the array containing attributes that will be in the new
     *            iterator if they are defined for this text.
     * @return the newly created {@code AttributedCharacterIterator}.
     */
    public AttributedCharacterIterator getIterator(
            Attribute[] attributes) {
        return new AttributedIterator(this, attributes, 0, text.length());
    }

    /**
     * Returns an {@code AttributedCharacterIterator} that gives access to the
     * contents of this attributed CharSequence starting at index {@code start} up to
     * index {@code end}. Only attributes contained in {@code attributes} are
     * available from this iterator if they are defined for this text.
     *
     * @param attributes
     *            the array containing attributes that will be in the new
     *            iterator if they are defined for this text.
     * @param start
     *            the start index of the iterator on the underlying text.
     * @param end
     *            the end index of the iterator on the underlying text.
     * @return the newly created {@code AttributedCharacterIterator}.
     */
    public AttributedCharacterIterator getIterator(
            Attribute[] attributes, int start,
            int end) {
        return new AttributedIterator(this, attributes, start, end);
    }

    @Override
    public AttributedCharSequence clone() {
        AttributedCharSequence clone;
        try {
            clone = (AttributedCharSequence) super.clone();
        }
        catch (CloneNotSupportedException e) {
            clone = new AttributedCharSequence(text);
        }
        clone.attributeMap = new HashMap<>(11);
        clone.attributeMap.putAll(attributeMap);
        return clone;
    }

}
