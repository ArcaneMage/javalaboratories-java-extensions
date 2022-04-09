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
import java.util.*;
import java.util.function.BiFunction;

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
     * Constructs a copy of the {@code map} object.
     * <p>
     * Method will throw a {@link NullPointerException} if map is {@code null}.
     * @param map LRUCacheMap object to copy.
     * @throws NullPointerException if parameters is {@code null}
     */
    public LRUCacheMap(final LRUCacheMap<K,V> map) {
        this(Objects.requireNonNull(map,"Requires map parameter").capacity);
        this.set.addAll(map.set);
        this.queue.addAll(map.queue);
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
    @Override
    public void clear() {
        super.clear();
        queue.clear();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Object clone() {
        return new LRUCacheMap<>(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<K,V>> entrySet() {
        return set;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!shallowEquals(o)) return false;
        LRUCacheMap<?, ?> that = (LRUCacheMap<?, ?>) o;
        return capacity == that.capacity && queue.equals(that.queue) && set.equals(that.set);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(final Object key) {
        if (this.containsKey(key)) {
            @SuppressWarnings("unchecked")
            K k = (K) key;
            V result = super.get(k);
            queue.remove(k);
            queue.addFirst(k);
            return result;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), queue, set, capacity);
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
    public boolean nudge(final K key) {
        return get(key) != null;
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
        return put(key, value, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(final Object key) {
        if (this.containsKey(key)) {
            @SuppressWarnings("unchecked")
            K k = (K) key;
            V result = super.remove(k);
            queue.remove(k);
            return result;
        }
        return null;
    }

    /**
     * This method returns a new {@link LRUCacheMap} with each entry supplied
     * with a new {@code key}.
     * <p>
     * The {@code function} parameter calculates the new {@code key} to be
     * assigned to the current {@code value}. It is important that the
     * calculated key is unique, otherwise data would be lost in the resultant
     * {@link LRUCacheMap}.
     *
     * @param function the {@code key mapping} function.
     * @param <R> the resultant {@code key}.
     * @return a new {@link LRUCacheMap} with new keys for each value.
     */
    public <R extends K> LRUCacheMap<K,V> resetKeys(final BiFunction<? super K,? super V,? extends R> function) {
        BiFunction<? super K,? super V,? extends R> f = Objects.requireNonNull(function,"Expected function");
        LRUCacheMap<K,V> result = new LRUCacheMap<>(capacity);
        for (Entry<K,V> e : entrySet()) {
            R key = Objects.requireNonNull(f.apply(e.getKey(),e.getValue()));
            result.put(key,e.getValue(),false);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",","[","]");
        queue.forEach(e -> joiner.add(String.format("[%s -> %s]",e.toString(),
                toString(super.get(e)))));
        return joiner.toString();
    }

    private V put(final K key, final V value, boolean lru) {
        if (this.containsKey(key)) {
            queue.remove(key);
        } else {
            if (queue.size() == capacity) {
                K lastKey = queue.removeLast();
                this.remove(lastKey);
            }
        }
        V result = putValue(key,value);
        if (lru) queue.addFirst(key);
        else queue.addLast(key);
        return result;
    }

    private V putValue(final K key, final V value) {
        return set.stream()
            .filter(k -> k.getKey().equals(key))
            .map(e -> e.setValue(value))
            .findAny()
            .orElseGet(() -> {set.add(new SimpleEntry<>(key,value));return null;});
    }

    private boolean shallowEquals(final Object o) {
        @SuppressWarnings("unchecked")
        LRUCacheMap<K,V> m = (LRUCacheMap<K,V>) o;
        if (m.size() != size())
            return false;
        for (Entry<K,V> e : entrySet()) {
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

    private String toString(final V value) {
        return value == null ? "Null" : value.toString();
    }
}