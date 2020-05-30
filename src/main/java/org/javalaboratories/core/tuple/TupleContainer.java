package org.javalaboratories.core.tuple;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Tuples are considered containers and therefore implement this interface.
 * <p>
 * They are sortable, iterable and serializable.
 * @see Tuple
 * @see AbstractTupleContainer
 * @see AbstractTuple
 */
public interface TupleContainer extends TupleBase, Comparable<TupleContainer>, Iterable<Object>, Serializable {

    /**
     * Determines whether this tuple contains an {@code object}.
     * <p>
     * A tuple is a container of objects of different types. Use this method
     * to determine whether the {@code object} exists within the container;
     * {@code true} is returned to indicate existence.
     *
     * @param element element with which to search.
     * @return true all the object exists in the container.
     */
    boolean contains(Object element);

    /**
     * Returns an {@link Map<K,?>} of elements within this tuple.
     * <p>
     * Implement the function that returns a key value for a given index of the
     * element in the tuple.
     *
     * @param keyMapper Function that returns a key value for a tuple element.
     * @param <K> type of returned value.
     * @return a {@link Map<K,?>} object.
     */
    <K> Map<K,?> toMap(Function<? super Integer, ? extends K> keyMapper);

    /**
     * Returns an object array of elements contained in this tuple.
     * <p>
     * If a tuple is considered empty, an array with zero elements is returned.
     * @return object array.
     */
    Object[] toArray();

    /**
     * Returns a {@link List<?>} object representing the contents of this tuple.
     * @return a {@link List<?>} object.
     */
    List<?> toList();


}
