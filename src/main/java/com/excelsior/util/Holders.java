package com.excelsior.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * Holder utility class
 * <p>
 * Generally used in lambda expressions to mutate objects declared as effectively final.
 * <p>
 * Use this class to create a variety of {@code Holder} objects, which can be
 * thread-safe, immutable as well as mutable. Each factory method describes
 * the type holder it creates.
 */
@SuppressWarnings("WeakerAccess")
public final class Holders {

    public static <T> Holder<T> synchronizedHolder(final T value) {
        return new SynchronizedHolder<>(value);
    }

    public static <T> Holder<T> synchronizedHolder() {
        return new SynchronizedHolder<>(null);
    }

    public static <T> Holder<T> mutableHolder() {
        return mutableHolder(null);
    }

    public static <T> Holder<T> mutableHolder(final T value) {
        return new MutableHolder<>(value);
    }

    public static <T> Holder<T> immutableHolder(final T value) {
        return new ImmutableHolder<>(value);
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

        public SynchronizedHolder(final T value) {
            super(value);
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
