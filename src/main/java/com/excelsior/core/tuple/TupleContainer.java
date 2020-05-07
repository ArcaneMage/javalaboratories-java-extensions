package com.excelsior.core.tuple;

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
public interface TupleContainer extends Comparable<Tuple>, Iterable<Object>, Serializable {

    int MAX_DEPTH = 16;

    /**
     * Determines whether this tuple contains an {@code object}.
     * <p>
     * A tuple is a container of objects of different types. Use this method
     * to determine whether the {@code object} exists within the container;
     * {@code true} is returned to indicate existence.
     *
     * @param element element with which to search.
     * @return true when the object exists in the container.
     */
    boolean contains(Object element);

    /**
     * Returns the depth of this tuple.
     * <p>
     * The depth refers to the maximum number of objects/elements that can be
     * accommodated in the tuple container. It can be referred to as the size
     * of the tuple.
     * @return depth of this tuple.
     */
    int depth();

    /**
     * Returns non-zero based position of an element within this tuple container.
     * <p>
     * @param object element with which to search.
     * @return zero-based position of the object, 0 to indicate not found.
     */
    int positionOf(Object object);

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

    /**
     * Returns object value at position.
     * <p>
     * All concrete tuple implementations, for example {@link Tuple2}, have direct
     * random access to values via the {@code Tuple2.valueX()}, where X represents
     * the position of the element. Although this method performs the same
     * operation, it provides access to elements when the concrete type of the
     * tuple is unknown. However, it is not as efficient as {@code valueX} methods
     * because it has to traverse the data structure in order locate the value.
     * <p>
     * Where possible use the {@code valueX} method of the tuple, otherwise resort
     * to this method if the concrete tuple type is unknown.
     * @param position of the element in the tuple, the
     *                 {@link IllegalArgumentException} is thrown if
     *                 {@code position} is less than 1 or greater than (@code depth)
     *                 of the tuple.
     * @param <T> type of object returned.
     * @return Element object cast to operate type. Method will attempt to cast the
     * element type.
     */
    <T> T value(int position);
}
