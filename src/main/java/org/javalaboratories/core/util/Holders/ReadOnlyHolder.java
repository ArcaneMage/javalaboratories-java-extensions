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

    @Override
    public String toString() {
        String s = super.toString();
        return STR."\{s} (Read-Only)";
    }

}
