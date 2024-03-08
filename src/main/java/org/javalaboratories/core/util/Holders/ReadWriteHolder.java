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

import java.io.Serial;
import java.util.Objects;
import java.util.function.Function;

/**
 * Returns a mutable {@code Holder} implementation.
 * <p>
 * The holder container contains a reference to the {@code value} that can
 * be overwritten with the {@code set} method.
 * <p>
 * This {@code Holder} is thread-safe.
 * @param <T> type of {@code value} encapsulated in the container.
 */
final class ReadWriteHolder<T> extends Holder<T> {
    @Serial
    private static final long serialVersionUID = -7908715729004692956L;

    /**
     * Returns an immutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that cannot
     * be overwritten with the {@code set} method. Note that immutability refers
     * to the holder object, not necessarily the value it contains.
     * <p>
     * @param holder holder object.
     * @param <T> type of the {@code value} encapsulated in the container.
     * @return an immutable implementation.
     * @throws NullPointerException when holder is a null reference.
     */
    static <T> Holder<T> readOnly(final Holder<T> holder) {
        return new ReadOnlyHolder<>(Objects.requireNonNull(holder).get());
    }

    public ReadWriteHolder(final T value) {
        super(value);
    }

    /**
     * {@inheritDoc}
     */
    public T getSet(final Function<? super T,T> function) {
        getLock().lock();
        try {
            T result = value;
            value = Objects.requireNonNull(function).apply(value);
            return result;
        } finally {
            getLock().unlock();
        }
    }

    /**
     * Sets/mutate the current value.
     * <p>
     * The mutation of the value is thread-safe. That is to say the reference
     * of the object is thread-safe, not necessarily the state of the contained
     * object. It is recommended that {@code value} or the contained object
     * is also a thread safe.
     *
     * @param value value
     */
    public void set(T value) {
        getLock().lock();
        try {
            this.value = value;
        } finally {
            getLock().unlock();
        }
    }

    /**
     * Sets {@code value} of this {@code holder} to the resultant value of
     * the function and returns it to the client.
     *
     * @param function to compute the resultant value.
     * @return the resultant value.
     */
    public T setGet(final Function<? super T,T> function) {
        getLock().lock();
        try {
            value = Objects.requireNonNull(function).apply(value);
            return value;
        } finally {
            getLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <U> Holder<U> pure(U value) {
        return new ReadWriteHolder<>(value);
    }

    /**
     * {@inheritDoc}
     */
    public Holder<T> readOnly() {
        return readOnly(this);
    }

    /**
     * {@inheritDoc}
     */
    public Holder<T> readWrite() {
        throw new IllegalStateException("Already in a read-write state");
    }
}
