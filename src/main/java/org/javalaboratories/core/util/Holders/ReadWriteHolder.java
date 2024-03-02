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

    public ReadWriteHolder(final T value) {
        super(value);
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
     * {@inheritDoc}
     */
    @Override
    protected <U> Holder<U> pure(U value) {
        return new ReadWriteHolder<>(value);
    }
}
