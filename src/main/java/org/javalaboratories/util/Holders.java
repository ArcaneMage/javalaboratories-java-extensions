package org.javalaboratories.util;

import org.javalaboratories.core.Eval;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Holder utility class
 * <p>
 * Generally used in lambda expressions to mutate objects declared as
 * effectively final.
 * <p>
 * Use this class to create a variety of {@code Holder} objects, which can be
 * thread-safe, immutable as well as mutable. Each factory method describes
 * the type writableHolder implementation it creates.
 *
 * @deprecated class is replaced with a pure alternative, namely {@link Eval},
 * which does not have to rely on side-effects. Refer to the {@link Eval
 * #cpeek(Consumer)} and {@link Eval#cpeek(Predicate, Consumer)} methods for
 * details.
 */
@SuppressWarnings("WeakerAccess")
@Deprecated
public final class Holders {

    /**
     * Returns a mutable, thread-safe {@code Holder} implementation.
     * <p>
     * The holder container contains a reference that can be overwritten with the
     * {@code set} method.
     * @param <T> type encapsulated in the container.
     * @return an mutable, thread-safe implementation.
     */
    public static <T> Holder<T> synchronizedHolder(final Holder<? extends T> holder)  {
        Objects.requireNonNull(holder);
        return new SynchronizedHolder<>(holder);
    }

    /**
     * Returns a mutable {@code Holder} implementation.
     * <p>
     * The holder container contains a {@code null} reference that can be overwritten with the
     * {@code set} method.
     * @param <T> type encapsulated in the container.
     * @return an mutable implementation.
     */
    public static <T> Holder<T> writableHolder() {
        return writableHolder(null);
    }

    /**
     * Returns a mutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that can
     * be overwritten with the {@code set} method.
     * @param <T> type of {@code value} encapsulated in the container.
     * @return an mutable implementation.
     */
    public static <T> Holder<T> writableHolder(final T value) {
        return new WritableHolder<>(value);
    }

    /**
     * Returns an immutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that cannot
     * be overwritten with the {@code set} method. If the value to be held is mutable,
     * it is recommended to provide a copy of it with the {@code Supplier}.
     * Note that immutability refers to the holder object, not necessarily the value
     * it contains.
     * <pre>
     *     {@code
     *      Holder<Person> p = Holders.readableHolder(() -> new Person());
     *     }
     * </pre>
     * @param copy of the object for the holder.
     * @param <T> type writableHolder the {@code value} encapsulated in the container.
     * @return an immutable implementation.
     */
    public static <T> Holder<T> readableHolder(final Supplier<T> copy) {
        Objects.requireNonNull(copy);
        return new ReadableHolder<>(copy.get());
    }

    private static class WritableHolder<T> implements Holder<T>, Serializable {
        private T value;

        public WritableHolder(final T value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Holder<?> holder = (Holder<?>) o;
            return Objects.equals(value, holder.get());
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        public T get() {
            return value;
        }

        public void set(T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Holder[" +
                    "value=" + value +
                    ']';
        }
    }

    private final static class ReadableHolder<T> extends WritableHolder<T> {
        public ReadableHolder(final T value) {
            super(value);
        }

        public void set(T value) {
            throw new UnsupportedOperationException();
        }
    }

    private final static class SynchronizedHolder<T> extends WritableHolder<T> {

        public SynchronizedHolder(final Holder<? extends T> holder) {
            super(holder.get());
        }

        @Override
        public boolean equals(Object o) {
            synchronized (this) {
                return super.equals(o);
            }
        }

        @Override
        public int hashCode() {
            synchronized(this) {
                return super.hashCode();
            }
        }

        public T get() {
            synchronized(this) {
                return super.get();
            }
        }

        public void set(T value) {
            synchronized(this) {
                super.set(value);
            }
        }

        @Override
        public String toString() {
            synchronized(this) {
                return super.toString();
            }
        }
    }
}
