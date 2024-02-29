package org.javalaboratories.core.util;

import org.javalaboratories.core.Eval;
import org.javalaboratories.core.Functor;

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
 * @param <T> type writableHolder variable to hold.
 *
 * Class is replaced with a pure alternative, namely {@link Eval},
 * which does not have to rely on side-effects. Refer to the {@link Eval
 * #cpeek(Consumer)} and {@link Eval#cpeek(Predicate, Consumer)}  methods for
 * details.
 */
public interface Holder<T> extends Functor<T> {

    /**
     * Assigns contents of {@code holder} to this {@code holder}.
     *
     * @param holder a holder object.
     * @return new holder containing value of {@code holder}.
     * @throws NullPointerException if holder object is null.
     */
    Holder<T> assign(final Holder<T> holder);

    /**
     * @return value of this {@code holder}
     */
    T get();

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
}
