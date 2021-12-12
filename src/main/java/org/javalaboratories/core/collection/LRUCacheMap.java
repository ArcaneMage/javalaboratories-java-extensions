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
import java.util.AbstractMap;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

/**
 * {@link LRUCacheMap} is a cache that implements the {@code Least Recently Used}
 * policy. The least recently used cache {@code entry} will be evicted from
 * the {@code Map}. This {@code Map} has a fixed {@code capacity} that is set
 * in the constructor and cannot be altered.
 *
 * @param <K> type of Key
 * @param <V> type of Value
 */
public class LRUCacheMap<K,V> extends AbstractMap<K,V> implements Cloneable, Serializable {

    private static final long serialVersionUID = 2605224651817155131L;

    public static final int DEFAULT_CAPACITY = 16;

    private final Deque<K> queue;
    private final Set<Entry<K,V>> set;
    private final int capacity;

    /**
     * Constructs this {@link LRUCacheMap} with {@code DEFAULT_CAPACITY} of 16
     * entries.
     */
    public LRUCacheMap() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs this {@link LRUCacheMap} with given capacity.
     * <p>
     * An {@link IllegalArgumentException} exception is thrown if the capacity
     * is less than or equal to zero.
     *
     * @param capacity Non-zero, positive value.
     */
    public LRUCacheMap(final int capacity) {
        super();
        if (capacity < 1)
            throw new IllegalArgumentException("Expected > 0 capacity");
        this.queue = new LinkedList<>();
        this.set = new HashSet<>();
        this.capacity = capacity;
    }

    /**
     * Returns current capacity of this {@link LRUCacheMap}.
     *
     * @return current capacity. This is always a non-zero, positive value.
     */
    public int capacity() {
        return this.capacity;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Object clone() throws CloneNotSupportedException {
        LRUCacheMap<K,V> result = new LRUCacheMap<>(capacity);
        result.clear();
        result.set.addAll(set);
        result.queue.addAll(queue);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        super.clear();
        queue.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return set;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!mapEquals(o)) return false;
        LRUCacheMap<?, ?> that = (LRUCacheMap<?, ?>) o;
        return capacity == that.capacity && queue.equals(that.queue) && set.equals(that.set);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), queue, set, capacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(final Object key) {
        if (this.containsKey(key)) {
            V result = super.get(key);
            queue.remove(key);
            queue.addFirst((K)key);
            return result;
        }
        return null;
    }

    /**
     * Similar to the {@link LRUCacheMap#get(Object)}, except elements are not
     * evicted, neither the LRU queue is affected.
     * <p>
     * More formally, if this map contains a mapping from a key k to a value v
     * such that (key==null ? k==null : key.equals(k)), then this method returns
     * v; otherwise it returns null. (There can be at most one such mapping.)
     *
     * @param key of object.
     * @return value mapped to {@code key}
     */
    public V peek(final K key) {
        return super.get(key);
    }

    /**
     * Retrieves a {@code value} from this {@link LRUCacheMap} that is at
     * {@code index} position in the {@code LRU queue}.
     *
     * @param index of subscript in LRU queue.
     * @return value value in {@code index} position.
     *
     * @throws IndexOutOfBoundsException if {@code index} is out of bounds.
     */
    public V peekAt(final int index) {
        if (index < 0 || index > this.size() -1)
            throw new IndexOutOfBoundsException();
        K key = null;
        Iterator<K> iter = queue.iterator();
        for (int i = 0; i <= index && iter.hasNext(); i++)
            key = iter.next();
        return super.get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(final K key, final V value) {
        if (this.containsKey(key)) {
            queue.remove(key);
        } else {
            if (queue.size() == capacity) {
                K k = queue.removeLast();
                this.remove(k);
            }
        }
        V result = putValue(key,value);
        queue.addFirst(key);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(final Object key) {
        if (this.containsKey(key)) {
            V result = super.remove(key);
            queue.remove(key);
            return result;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",","[","]");
        queue.forEach(e -> joiner.add(String.format("(%s -> %s)",e.toString(),
                super.get(e).toString())));
        return joiner.toString();
    }

    private V putValue(final K key, final V value) {
        V result = set.stream()
            .filter(k -> k.getKey().equals(key))
            .map(e -> e.setValue(value))
            .findFirst()
            .orElse(null);
        if (result == null) {
            set.add(new SimpleEntry<>(key,value));
        }
        return result;
    }

    private boolean mapEquals(final Object o) {
        @SuppressWarnings("unchecked")
        LRUCacheMap<K,V> m = (LRUCacheMap<K, V>) o;
        if (m.size() != size())
            return false;
        for (Entry<K, V> e : entrySet()) {
            K key = e.getKey();
            V value = e.getValue();
            if (value == null) {
                if (!(m.peek(key) == null && m.containsKey(key)))
                    return false;
            } else {
                if (!value.equals(m.peek(key)))
                    return false;
            }
        }
        return true;
    }
}