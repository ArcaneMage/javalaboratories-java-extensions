package org.javalaboratories.core.util;

import org.javalaboratories.core.Eval;
import org.javalaboratories.core.Functor;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
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
 */
public interface Holder<T> extends Functor<T>, Iterable<T> {

    /**
     * Sets {@code value} for this {@code holder}. Default implementation
     * is {@link UnsupportedOperationException}. Not all {@code holder}
     * implementations implement this method.
     * @param value value
     */
    default void set(final T value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException when mapper function is a {@code null}
     * reference.
     */
    @Override
    default <R> Holder<R> map(final Function<? super T, ? extends R> mapper) {
        return Holders.readOnly(Objects.requireNonNull(mapper).apply(get()));
    }

    /**
     * {@inheritDoc}
     */
    default Holder<T> peek(Consumer<? super T> consumer) {
        return (Holder<T>) Functor.super.peek(consumer);
    }

    /**
     * {@inheritDoc}
     */
    default Iterator<T> iterator() {
        return Collections.singletonList(get()).iterator();
    }
}
