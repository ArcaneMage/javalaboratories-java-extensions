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
     * Joins a {@code that} tuple to the tuple, resulting in a new tuple.
     * <p>
     * The (@code that) tuple contents are appended to the end the tuple, thus
     * creating a new tuple with the appropriate depth to accommodate the
     * merged contents.
     * <p>
     * If teh combined depth of both this and {@code that} tuple is greater
     * that {@code MAX_DEPTH}, the exception {@link TupleOverflowException}
     * is thrown.
     * @param that is a tuple whose contents are to be merged with the tuple's
     *             contents.
     * @param <Q> the type of the tuple.
     * @param <R> the type of the new tuple returned.
     * @return a new tuple with the appropriate depth to house the value.
     * @throws NullPointerException if {@code that} is null.
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
     * Adds a value to the tuple at element position.
     * <p>
     * The parameter {@code position} is a positive value that is greater than
     * 0 and less than the depth of the tuple. If the addition of the value
     * results in potentially creating a tuple that has a depth that is greater
     * than {@code MAX_DEPTH}, the method will throw {@link TupleOverflowException}
     * exception.
     *
     * @param position non-zero, positive value that indicates physical
     *                 position to place the value. This parameter must be less than
     *                 or equal to {@link this#depth()}
     * @param value the value to add to the tuple.
     * @param <Q> type of value.
     * @param <R> type of tuple returned.
     * @return a new tuple with the appropriate type and depth to accommodate
     * the added value.
     */
    final <Q, R extends Tuple> R add(int position, final Q value) {
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
     * Returns a tuple with elements from {@code position} to the end of the tuple.
     * <p>
     * This method is similar to {@link AbstractTuple#truncate(int)}, but rather truncating
     * from the right, truncation in this method is performed from the left. An
     * example would be the following: given the tuple {@code [1,2,3,4,5]}, hopping
     * to {@code position 3}, will result in a new tuple {@code [3,4,5]}.
     *
     * @param position non-zero, positive value to which to hop. This parameter must
     *                 be less than or equal to {@link this#depth()}
     * @param <T> type of tuple returned
     * @return new resultant tuple
     */
    final <T extends Tuple> T hop(int position) {
        verify(position);
        Nullable<T> hopped = Tuples.fromIterable(this, position, depth() - position + 1);
        return hopped.get();
    }

    /**
     * Splices or cuts the tuple into two smaller tuples at {@code position}.
     * <p>
     * This method is almost the direct opposite to the join, except that the
     * resultant tuple returned is a container of two smaller tuples. For example,
     * given the tuple {@code [1,2,3,4,5]}. If the tuple is spliced at position 3,
     * the method will return the following {@code [[1,2],[3,4,5]]} in the
     * {@link Tuple2} container. Use the methods of {@link Tuple2} to access the
     * sliced the tuples.
     *
     * @param position is the location in the tuple at which to perform the
     *                 slicing. This parameter must be less than or equal to
     *                 {@link this#depth()}
     * @param <Q>      type of the first tuple in the returned {@link Tuple2}
     *           container.
     * @param <R>      type of the second tuple in the returned {@link Tuple2}
     *           container.
     * @return a new {@link Tuple2} containing tuple slices.
     */
    final <Q extends Tuple, R extends Tuple> Tuple2<Q,R> splice(int position) {
        return Tuple.of(truncate(position),hop(position));
    }


    /**
     * Truncates the tuple at {@code position}
     * <p>
     * Method returns a new truncated tuple, a tuple that has had the remaining
     * elements discarded. For example the tuple {@code [1,2,3,4,5]} truncated at
     * {@code position} 3 would result in a new tuple {@code [1,2]}
     *
     * @param position is a non-zero, positive value at which truncation is
     *                 performed. This parameter must be less than or equal to
     *                 {@link this#depth()}
     * @param <T> type of tuple returned.
     * @return a new truncated tuple.
     */
    final <T extends Tuple> T truncate(int position) {
        verify(position);
        Nullable<T> result = Tuples.fromIterable(this, position - 1);
        return result.get();
    }

    /**
     * Removes an element/object from the tuple.
     * <p>
     * The first occurrence of the object is removed from the tuple and a new
     * resultant tuple is returned without the specified element. A match is
     * determined with the {@link Object#equals(Object)} method.
     * <p>
     * Initially, the position of the object is calculated, so if the object
     * does not exist, then this will result in an exception being thrown.
     *
     * @param object Object to be removed.
     * @param <T> type of tuple returned.
     * @return tuple without the specified {@code element}
     */
    final <T extends Tuple> T remove(Object object) {
        int position = positionOf(object);
        Tuple2<Tuple, Tuple> spliced = splice(position);
        return spliced.value1().join(position < depth() ? ((AbstractTuple) spliced.value2()).hop(2) : Tuples.emptyTuple());
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
