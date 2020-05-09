package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * This tuple is a special tuple that is a container of zero elements.
 * <p>
 * Some operations on tuples may result in instantiation of this tuple to signify
 * that computation has resulted in an empty Tuple.
 * <p>
 * However, it is possible to perform joins on this tuple.
 * @see Tuple#add(int, Object)
 * @see Tuple#splice(int)
 * @see Tuple#truncate(int)
 */
public final class Tuple0 implements Tuple {

    public static final long serialVersionUID = -1202062103023587399L;

    private static final String UNSUPPORTED_OPERATION_MESSAGE = "This is not supported in this tuple";

    public Tuple0() { }

    /**
     * {@inheritDoc}
     */
    @Override
    public <Q, R extends Tuple> R add(int position, final Q value) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

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
    public <T extends Tuple> T hop(int poistion) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <Q extends Tuple, R extends Tuple> R join(final Q that) {
        Objects.requireNonNull(that);
        Nullable<R> result = Tuples.fromIterable(that,that.depth());
        return result.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Tuple> T truncate(int position) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
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
    public <T extends Tuple> T remove(Object object) {
        throw new UnsupportedOperationException(UNSUPPORTED_OPERATION_MESSAGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Tuple o) {
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
    public Iterator<Object> iterator() {
        return new Iterator<Object>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                throw new NoSuchElementException();
            }
        };
    }

    @Override
    public <T> T value(int position) {
        throw new IllegalArgumentException();
    }
}
