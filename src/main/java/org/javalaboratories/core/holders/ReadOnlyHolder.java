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
package org.javalaboratories.core.holders;

import java.io.Serial;
import java.util.Objects;

/**
 * Returns an immutable {@code Holder} implementation.
 * <p>
 * The holder container contains a reference to the {@code value} that cannot
 * be overwritten with the {@code set} method.
 * Note that immutability refers to the reference of the object in this
 * container, not necessarily the state of that object.
 *
 * @param <T> type of the {@code value} encapsulated in the container.
 */
final class ReadOnlyHolder<T> extends Holder<T> {
    @Serial
    private static final long serialVersionUID = 3906482600158622341L;

    public static final Holder<?> EMPTY = new ReadOnlyHolder<>(null);

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
    static <T> Holder<T> readWrite(final Holder<T> holder) {
        return new ReadWriteHolder<>(Objects.requireNonNull(holder).get());
    }

    public ReadOnlyHolder(final T value) {
        super(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <U> Holder<U> pure(U value) {
        return new ReadOnlyHolder<>(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Holder<T> readWrite() {
        return readWrite(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Holder<T> readOnly() {
        throw new IllegalStateException("Already in a read-only state");
    }

    @Override
    public String toString() {
        String s = super.toString();
        return STR."\{s} (Read-Only)";
    }
}
