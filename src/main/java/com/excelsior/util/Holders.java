package com.excelsior.util;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Holder utility class
 * <p>
 * Generally used in lambda expressions to mutate objects declared as effectively final.
 * <p>
 * Use this class to create a variety of {@code Holder} objects, which can be
 * thread-safe, immutable as well as mutable. Each factory method describes
 * the type of implementation it creates.
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
     */
    public static <T> Holder<T> synchronizedHolder(final Holder<? extends T> holder )  {
        Objects.requireNonNull(holder);
        return new SynchronizedHolder<>(holder);
    }

    /**
     * Returns a mutable {@code Holder} implementation.
     * <p>
     * The holder container contains a null reference that can be overwritten with the
     * {@code set} method.
     * @param <T> type encapsulated in the container.
     * @return an mutable implementation.
     */
    public static <T> Holder<T> mutableHolder() {
        return mutableHolder(null);
    }

    /**
     * Returns a mutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that can
     * be overwritten with the {@code set} method.
     * @param <T> type of the {@code value} encapsulated in the container.
     * @return an mutable implementation.
     */
    public static <T> Holder<T> mutableHolder(final T value) {
        return new MutableHolder<>(value);
    }

    /**
     * Returns an immutable {@code Holder} implementation.
     * <p>
     * The holder container contains a reference to the {@code value} that cannot
     * be overwritten with the {@code set} method. Provide a copy of the value to be
     * held with the {@code Supplier}
     * <pre>
     *     {@code
     *      Holder<Person> p = Holders.immutableHolder(() -> new Person());
     *     }
     * </pre>
     * @param copy a of the object for the holder.
     * @param <T> type of the {@code value} encapsulated in the container.
     * @return an immutable implementation.
     */
    @Deprecated
    public static <T> Holder<T> immutableHolder(final Supplier<T> copy) {
        Objects.requireNonNull(copy);
        return new ImmutableHolder<>(copy.get());
    }

    private static class MutableHolder<T> implements Holder<T>, Serializable {
        private T value;

        public MutableHolder(final T value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Holder<?> holder = (Holder<?>) o;
            synchronized(this) {
                return Objects.equals(value, holder.get());
            }
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

    private final static class ImmutableHolder<T> extends MutableHolder<T> {
        public ImmutableHolder(final T value) {
            super(value);
        }

        public void set(T value) {
            throw new UnsupportedOperationException();
        }
    }

    private final static class SynchronizedHolder<T> extends MutableHolder<T> {

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
