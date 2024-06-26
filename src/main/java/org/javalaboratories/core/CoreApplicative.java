package org.javalaboratories.core;

import java.io.Serial;
import java.util.Objects;

/**
 * Inherit from this class to ensure applicative maintains natural order in
 * collections as well as having basic/core behaviour.
 * <p>
 * Applicatives value type must implement the {@link Comparable} interface, but
 * failing to do so will result in a {@link ClassCastException} object type being
 * thrown when natural sorting is applied.
 *
 * @param <T> type of contained value.
 */
public abstract class CoreApplicative<T> extends Applicative<T> implements Comparable<CoreApplicative<T>> {

    @Serial
    private static final long serialVersionUID = 7425864655492255203L;

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException when other is a {@code null} reference.
     * @throws ClassCastException if {@code value type} does not implement the
     * {@link Comparable} interface.
     */
    @Override
    public int compareTo(final CoreApplicative<T> other) {
        try {
            // This is okay because the type casting failure is handled.
            // It is possible to enforce type checking by extending T from
            // Comparable interface, but this conflicts with function
            // signatures like map and apply functions.
            @SuppressWarnings("unchecked")
            Comparable<T> a = (Comparable<T>) this.get();
            return a.compareTo(Objects.requireNonNull(other).get());
        } catch (ClassCastException e) {
            throw new ClassCastException(STR."Applicative \{this.get()} is not a comparable type");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
       return STR."Applicative[value=\{this.get()}]";
    }
}
