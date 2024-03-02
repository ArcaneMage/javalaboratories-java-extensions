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
public sealed abstract class Holder<T> extends Applicative<T> implements Monad<T>, Iterable<T>, Serializable
        permits ReadOnlyHolder, ReadWriteHolder {
    @Serial
    private static final long serialVersionUID = -3480539403374331932L;

    protected T value;
    private final ReentrantLock lock;

    /**
     * Returns a mutable, thread-safe {@code Holder} implementation.
     * <p>
     * The holder container contains a reference that can be overwritten with the
     * {@code set} method.
     * @param <T> type encapsulated in the container.
     * @return an mutable, thread-safe implementation.
     * @deprecated Factory method replaced by {@code safeHolder}
     */
    @Deprecated
    public static <T> Holder<T> synchronizedHolder(final Holder<T> holder)  {
        return readWrite(holder);
    }

    /**
     * Returns a mutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that can
     * be overwritten with the {@code set} method.
     * <p>
     * This {@code Holder} is thread-safe.
     * @param value holder object to be assigned to this {@code holder}
     * @param <T> type of {@code value} encapsulated in the container.
     * @return an mutable implementation.
     * @throws NullPointerException when holder object is null
     */
    public static <T> Holder<T> of(final T value) {
        return new ReadWriteHolder<>(value);
    }

    /**
     * Returns a mutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that can
     * be overwritten with the {@code set} method.
     * <p>
     * This {@code Holder} is thread-safe.
     * @param <T> type of {@code value} encapsulated in the container.
     * @return an mutable implementation.
     * @throws NullPointerException when holder object is null
     */
    public static <T> Holder<T> empty() {
        return new ReadWriteHolder<>(null);
    }

    /**
     * Returns a mutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that can
     * be overwritten with the {@code set} method.
     * <p>
     * This {@code Holder} is thread-safe.
     * @param holder holder object to be assigned to this {@code holder}
     * @param <T> type of {@code value} encapsulated in the container.
     * @return an mutable implementation.
     * @throws NullPointerException when holder object is null
     */
    public static <T> Holder<T> readWrite(final Holder<T> holder) {
        return Holder.of(Objects.requireNonNull(holder).get());
    }

    /**
     * Returns an immutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that cannot
     * be overwritten with the {@code set} method. If the value to be held is mutable,
     * it is recommended to provide a copy of it with the {@code Supplier}.
     * Note that immutability refers to the holder object, not necessarily the value
     * it contains.
     * <p>
     * @param holder holder object.
     * @param <T> type of the {@code value} encapsulated in the container.
     * @return an immutable implementation.
     * @throws NullPointerException when holder is a null reference.
     */
    public static <T> Holder<T> readOnly(final Holder<T> holder) {
        return new ReadOnlyHolder<>(Objects.requireNonNull(holder).get());
    }

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
     * Returns an immutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that cannot
     * be overwritten with the {@code set} method. If the value to be held is mutable,
     * it is recommended to provide a copy of it with the {@code Supplier}.
     * Note that immutability refers to the holder object, not necessarily the value
     * it contains.
     * <p>
     * @return an immutable implementation.
     */
    public Holder<T> readOnly() {
        return readOnly(this);
    }

    /**
     * Returns a mutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that can
     * be overwritten with the {@code set} method.
     * <p>
     * This {@code Holder} is thread-safe.
     * @return an mutable implementation.
     */
    public Holder<T> readWrite() {
        return readWrite(this);
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
     * Returns {@code true} when this value is not empty and predicate returns
     * {@code true} when applied to this {@code Holder}
     *
     * @param predicate function to be applied to this {@code Holder} object
     * @return {@code true} when this value is not empty and predicate returns
     * {@code true}.
     */
    public boolean exists(Predicate<? super T> predicate) {
        lock.lock();
        try {
            return this.value != null && Objects.requireNonNull(predicate).test(value);
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
            return value == null ? other : get();
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
     * Returns {@code this} {@link Holder} object that satisfies the {@code
     * predicate} function.
     *
     * @param predicate function to apply test.
     * @return {@code Holder} object that agrees/or meets the {@code predicate's}
     * test.
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    public Holder<T> filter(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate,"Expected predicate function");
        return predicate.test(get()) ? this : Holder.empty();
    }

    /**
     * Returns {@code this} {@link Holder} object that satisfies the negative
     * {@code predicate} function.
     *
     * @param predicate function to apply the negative test.
     * @return {@code Holder} object that agrees/or meets the {@code predicate's}
     * test.
     * @throws NullPointerException if {@code predicate} function is {@code null}.
     */
    public Holder<T> filterNot(final Predicate<? super T> predicate) {
        return filter(Objects.requireNonNull(predicate).negate());
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
     *
     * @param value value of the contained object
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
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return Collections.singletonList(get()).iterator();
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
}
