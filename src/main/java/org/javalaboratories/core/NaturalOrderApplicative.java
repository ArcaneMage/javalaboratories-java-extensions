package org.javalaboratories.core;

/**
 * Inherit from this class to ensure applicative maintains natural order in
 * collections.
 * <p>
 * Applicatives value type must implement the {@link Comparable} interface, but
 * failing to do so will result in a {@link ClassCastException} object type being
 * thrown when natural sorting is applied.
 *
 * @param <T> type of contained value.
 */
public abstract class NaturalOrderApplicative<T> extends Applicative<T> implements Comparable<Applicative<T>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Applicative<T> other) {
        try {
            // This is okay because the type casting failure is handled.
            // It is possible to enforce type checking by extending T from
            // Comparable interface, but this conflicts with parent function
            // signatures like flatMap and map functions.
            @SuppressWarnings("unchecked")
            Comparable<T> a = (Comparable<T>) this.get();
            return a.compareTo(other.get());
        } catch (ClassCastException e) {
            throw new ClassCastException(STR."Applicative \{this.get()} is not a comparable type");
        }
    }
}
