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

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.javalaboratories.core.Eval;

/**
 * Holder utility class
 * <p>
 * Generally used in lambda expressions to mutate objects declared as
 * effectively final.
 * <p>
 * Use this class to create a variety of {@code Holder} objects, which are
 * thread-safe, immutable as well as mutable. Each factory method describes
 * the type {@code Holder} implementation it creates.
 * <p>
 * Class is replaced with a pure alternative, namely {@link Eval},
 * which does not have to rely on side effects. Refer to the {@link Eval
 * #cpeek(Consumer)} and {@link Eval#cpeek(Predicate, Consumer)} methods for
 * details.
 *
 * @see Holder
 */
@SuppressWarnings("WeakerAccess")
public final class Holders {

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
     * The holder container contains a {@code null} reference that can be overwritten with the
     * {@code set} method.
     * <p>
     * This {@code Holder} is thread-safe.
     * @param <T> type encapsulated in the container.
     * @return an mutable implementation.
     */
    public static <T> Holder<T> readWrite() {
        return readWrite((T)null);
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
     */
    public static <T> Holder<T> readWrite(final T value) {
        return new ReadWriteHolder<>(value);
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
        return Holders.readWrite(Objects.requireNonNull(holder).get());
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
     * @param <T> type writableHolder the {@code value} encapsulated in the container.
     * @return an immutable implementation.
     * @throws NullPointerException when holder is a null reference.
     */
    public static <T> Holder<T> readOnly(final Holder<T> holder) {
        return Holders.readOnly(Objects.requireNonNull(holder).get());
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
     * @param value of the object for the holder.
     * @param <T> type writableHolder the {@code value} encapsulated in the container.
     * @return an immutable implementation.
     */
    public static <T> Holder<T> readOnly(final T value) {
        return new ReadOnlyHolder<>(value);
    }

    private Holders() {}
}
