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
package org.javalaboratories.core.util.Holders;

import org.javalaboratories.core.Applicative;
import org.javalaboratories.core.Eval;
import org.javalaboratories.core.Monad;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This is a container object that holds a value.
 * <p>
 * Generally used in lambda expressions to mutate objects declared as effectively
 * final.
 * <p>
 * Implementations of this interface must enforce thread-safety where possible.
 * Ideally, the variable within the container should also be thread safe, because
 * the {@code Holder} container only guarantees that the reference of the
 * contained object is writable by one thread only.
 * <p>
 * Class is replaced with a pure alternative, namely {@link Eval},
 * which does not have to rely on side effects. Refer to the {@link Eval
 * #cpeek(Consumer)} and {@link Eval#cpeek(Predicate, Consumer)}  methods for
 * details.
 *
 * @param <T> type of variable to hold.
 *
 * @see ReadWriteHolder
 * @see ReadOnlyHolder
 */
public abstract class Holder<T> extends Applicative<T> implements Monad<T>, Iterable<T>, Serializable {
    @Serial
    private static final long serialVersionUID = -3480539403374331932L;

    protected T value;
    private final ReentrantLock lock;

    /**
     * Constructs the {@code Holder} container, encapsulating
     * the {@code value}.
     *
     * @param value to be held
     */
    public Holder(final T value) {
        this.value = value;
        this.lock = new ReentrantLock();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holder<?> holder = (Holder<?>) o;
        lock.lock();
        try {
            return Objects.equals(value, holder.get());
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        lock.lock();
        try {
            return Objects.hash(value);
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getOrElse(T other) {
        lock.lock();
        try {
            return value == null ? get() : other;
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Holder<U> flatMap(final Function<? super T, ? extends Monad<U>> mapper) {
        return (Holder<U>) Monad.super.flatMap(mapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Holder<U> flatten() {
        return (Holder<U>) Monad.super.<U>flatten();
    }

    /**
     * Returns value in this {@code Holder} container.
     */
    public T get() {
        lock.lock();
        try {
            return value;
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Holder<R> map(final Function<? super T, ? extends R> mapper) {
        return (Holder<R>) super.<R>map(mapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Holder<T> peek(final Consumer<? super T> consumer) {
        return (Holder<T>) super.peek(consumer);
    }

    /**
     * Sets {@code value} for this {@code holder}. Default implementation
     * is {@link UnsupportedOperationException}. Not all {@code holder}
     * implementations implement this method.
     * @param value value
     */
    public void set(T value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        lock.lock();
        try {
            return STR."Holder[value=\{value}\{']'}";
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns this {@code Reentrant} lock.
     * <p>
     * To be used by derived classes to read or write current value with thread
     * safety.
     *
     * @return this {@code Reentrant} lock.
     */
    protected ReentrantLock getLock() {
        return lock;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return Collections.singletonList(get()).iterator();
    }
}
