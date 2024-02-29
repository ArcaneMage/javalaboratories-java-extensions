package org.javalaboratories.core.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.javalaboratories.core.Eval;
import org.javalaboratories.core.Functor;

/**
 * Holder utility class
 * <p>
 * Generally used in lambda expressions to mutate objects declared as
 * effectively final.
 * <p>
 * Use this class to create a variety of {@code Holder} objects, which can be
 * thread-safe, immutable as well as mutable. Each factory method describes
 * the type writableHolder implementation it creates.
 * <p>
 * Class is replaced with a pure alternative, namely {@link Eval},
 * which does not have to rely on side effects. Refer to the {@link Eval
 * #cpeek(Consumer)} and {@link Eval#cpeek(Predicate, Consumer)} methods for
 * details.
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
    public static <T> Holder<T> synchronizedHolder(final Holder<? extends T> holder)  {
        Objects.requireNonNull(holder);
        return new SynchronizedHolder<>(holder);
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
    public static <T> Holder<T> mutable() {
        return mutable((T)null);
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
    public static <T> Holder<T> mutable(final T value) {
        return new MutableHolder<>(value);
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
    public static <T> Holder<T> mutable(final Holder<T> holder) {
        return Holders.mutable(Objects.requireNonNull(holder).get());
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
     * @param <T> type writableHolder the {@code value} encapsulated in the container.
     * @return an immutable implementation.
     */
    public static <T> Holder<T> readOnly() {
        return readOnly((T)null);
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

    private static class MutableHolder<T> implements Holder<T>, Serializable {
        @Serial
        private static final long serialVersionUID = -3480539403374331932L;

        private T value;
        private final ReentrantLock lock;

        public MutableHolder(final T value) {
            this.value = value;
            this.lock = new ReentrantLock();
        }

        public Holder<T> assign(final Holder<T> holder) {
            return new MutableHolder<>(Objects.requireNonNull(holder).get());
        }

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

        @Override
        public int hashCode() {
            lock.lock();
            try {
                return Objects.hash(value);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public T getOrElse(T other) {
            lock.lock();
            try {
                return value == null ? get() : other;
            } finally {
                lock.unlock();
            }
        }

        public T get() {
            lock.lock();
            try {
                return value;
            } finally {
                lock.unlock();
            }
        }

        public void set(T value) {
            lock.lock();
            try {
                this.value = value;
            } finally {
                lock.unlock();
            }
        }

        @Override
        public String toString() {
            lock.lock();
            try {
                return STR."Holder[value=\{value}\{']'}";
            } finally {
                lock.unlock();
            }
        }
    }

    private final static class ReadOnlyHolder<T> extends MutableHolder<T> {
        @Serial
        private static final long serialVersionUID = 3906482600158622341L;

        public ReadOnlyHolder(final T value) {
            super(value);
        }

        public Holder<T> assign (final Holder<T> holder) {
            return new ReadOnlyHolder<>(Objects.requireNonNull(holder).get());
        }

        public void set(T value) {
            throw new UnsupportedOperationException();
        }
    }

    @Deprecated
    private final static class SynchronizedHolder<T> extends MutableHolder<T> {
        @Serial
        private static final long serialVersionUID = 7172407096739536828L;

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

    private Holders() {}
}
