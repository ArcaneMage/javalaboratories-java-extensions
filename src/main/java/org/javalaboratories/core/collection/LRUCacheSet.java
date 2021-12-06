/*
 * Copyright 2020 Kevin Henry
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.javalaboratories.core.collection;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * {@link LRUCacheSet} is a cache that implements the {@code Least Recently Used}
 * policy. The least recently used cache {@code entry} will be evicted from
 * the {@code Set}. This {@code Set} has a fixed {@code capacity} that is set
 * in the constructor and cannot be altered.
 *
 * @param <T> type of Key
 */
public class LRUCacheSet<T> extends LinkedHashSet<T> implements Serializable {

    private static final long serialVersionUID = 449257218556904931L;

    public static final int DEFAULT_CAPACITY = 16;

    private final int capacity;

    /**
     * Constructs this {@link LRUCacheSet} with {@code DEFAULT_CAPACITY} of 16
     * entries.
     */
    public LRUCacheSet() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs this {@link LRUCacheSet} with a given {@code capacity}.
     *
     * @param capacity maximum size of this {@link LRUCacheSet}.
     */
    public LRUCacheSet(final int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(final T key) {
        if (!nudge(key)) {
            if (this.size() == this.capacity) {
                T k = this.iterator().next();
                this.remove(k);
            }
            return super.add(key);
        } else {
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LRUCacheSet<?> that = (LRUCacheSet<?>) o;
        return capacity == that.capacity;
    }

    /**
     * Returns {@code key} if it exists in this {@code Set} and {@code nudges}
     * it to the "front" of the list, thus making it most recently used.
     *
     * @param key element
     * @return element, if {@code key} exists otherwise {@code null} is
     * returned.
     */
    public T get(final T key) {
        return nudge(key) ? key : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), capacity);
    }

    /**
     * Returns current capacity of this {@link LRUCacheSet}.
     *
     * @return current capacity. This is always a non-zero, positive value.
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Moves the key to the "front", thus making it the most recently used
     * {@code key}.
     * <p>
     * Returns {@code true} of the {@code key} is successfully moved to the
     * "front" of the list, otherwise {@code false} is returned.
     *
     * @param key to nudge
     * @return true if successfully nudged.
     */
    public boolean nudge(final T key) {
        if (this.contains(key)) {
            this.remove(key);
            this.add(key);
            return true;
        }
        return false;
    }

    /**
     * Retrieves a {@code value} from this {@link LRUCacheSet} that is at
     * {@code index} position in the {@code LRU queue}.
     *
     * @param index of subscript in LRU queue.
     * @return value value in {@code index} position.
     *
     * @throws IndexOutOfBoundsException if {@code index} is out of bounds.
     */
    public T peekAt(final int index) {
        if (index < 0 || index > this.size() -1)
            throw new IndexOutOfBoundsException();
        T key = null;
        Iterator<T> iter = iterator();
        for (int i = size() -1; i >= index && iter.hasNext(); i--)
            key = iter.next();
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",","[","]");
        Iterator<T> iter = new LinkedList<>(this).descendingIterator();
        iter.forEachRemaining(key -> joiner.add(key.toString()));
        return joiner.toString();
    }
}
