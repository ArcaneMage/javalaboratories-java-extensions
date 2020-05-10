package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;

import java.util.*;


/**
 * Tuples inherit from this class.
 * <p>
 * The tuple is considered to be a container. It currently uses a simple doubly
 * linked list data structure. A decision was made to use this approach rather
 * than reusing {@link LinkedList} because it is important to reduce the overhead
 * of the structure of the tuple as well as minimise typing where possible in
 * concrete classes. This class implements {@link Comparable},
 * {@link java.io.Serializable} and {@link Iterable} interfaces.
 * <p>
 * This class and derived classes are immutable.
 */

public abstract class AbstractTuple extends AbstractTupleContainer implements Tuple {

    /**
     * Constructor
     *
     * @param elements to be managed by container.
     */
    AbstractTuple(Object... elements) {
        super(elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <Q, R extends Tuple> R add(int position, final Q value) {
        if (this.depth() + 1 > MAX_DEPTH)
            throw new TupleOverflowException();
        if (position == 1) {
            return Tuple.of(value).join(this);
        } else {
            Tuple2<Tuple, Tuple> spliced = this.splice(position);
            return spliced.value1().join(Tuple.of(value)).join(spliced.value2());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Tuple> T hop(int position) {
        verify(position);
        Nullable<T> hopped = Tuples.fromIterable(this, position, depth() - position + 1);
        return hopped.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <Q extends Tuple, R extends Tuple> R join(final Q that) {
        Objects.requireNonNull(that);
        int depth = this.depth() + that.depth();
        if (depth > MAX_DEPTH)
            throw new TupleOverflowException();
        Object[] objects = getObjects(this, that);
        Nullable<R> result = Tuples.fromIterable(Arrays.asList(objects), objects.length);
        return result.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Tuple> T truncate(int position) {
        verify(position);
        Nullable<T> result = Tuples.fromIterable(this, position - 1);
        return result.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Tuple> T remove(Object object) {
        int position = positionOf(object);
        Tuple2<Tuple, Tuple> spliced = splice(position);
        return spliced.value1().join(position < depth() ? spliced.value2().hop(2) : Tuples.emptyTuple());
    }

    private Object[] getObjects(final Tuple... tuples) {
        // Calculate depth
        int depth = 0;
        for (Tuple tuple : tuples)
            depth += tuple.depth();
        // Convert to objects
        Object[] result = new Object[depth];
        int i = 0;
        for (Tuple tuple : tuples) {
            Iterator<?> it = tuple.iterator();
            while (it.hasNext()) result[i++] = it.next();
        }
        return result;
    }
}
