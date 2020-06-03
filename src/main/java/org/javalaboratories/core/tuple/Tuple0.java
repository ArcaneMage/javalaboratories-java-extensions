package org.javalaboratories.core.tuple;

import org.javalaboratories.core.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * This tuple is a special tuple that is a container of zero elements.
 * <p>
 * Some operations on tuples may result in instantiation of this tuple to signify
 * that computation has resulted in an empty Tuple.
 * <p>
 * However, it is possible to perform joins on this tuple.
 */
public final class Tuple0 implements Tuple {

    public static final long serialVersionUID = -1202062103023587399L;

    public Tuple0() { }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object object) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int depth() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int positionOf(Object object) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R extends Tuple> R join(final Tuple that) {
        Objects.requireNonNull(that);
        Nullable<R> result = Tuples.fromIterable(that.toList(),that.depth());
        return result.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<?> toList() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <K> Map<K, ?> toMap(Function<? super Integer, ? extends K> keyMapper) {
        return Collections.emptyMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Tuple0=[]";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(TupleContainer o) {
        if ( o == null )
            throw new NullPointerException();

        // Compare depth (sort by depth first)
        int result = this.depth() - o.depth();
        if ( result != 0 )
            return result;

        // Comparing with another Tuple0?
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple objects = (Tuple) o;

        return this.depth() == objects.depth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return 13;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<TupleElement> iterator() {
        return new Iterator<TupleElement>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public TupleElement next() {
                throw new NoSuchElementException();
            }
        };
    }

    @Override
    public <T> T value(int position) {
        throw new IllegalArgumentException();
    }
}
