package com.excelsior.core.tuple;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

/**
 * A tuple implements this interface.
 * <p>
 * A tuple is a container whose elements are NOT necessarily of the same type.
 * However, they are powerful in that they provide a mechanism for functions
 * to return multiple values and/or pass multiple values as single tuple
 * method argument. They do not always relate to each other but collectively
 * they have some meaning.
 * <p>
 * Up to 16 tuple types are currently supported and are created with the aid of
 * factory methods, {@link Tuple#of}, defined in this interface. There are many
 * operations available on tuples including the ability to convert elements to
 * to a {@link List}, {@link Map} and to an array.
 * <p>
 * All tuples are immutable, comparable, iterable and serializable.
 * @see Tuple0
 * @see Tuple1
 * @see Tuple2
 * @see Tuple3
 * @see Tuple4
 * @see Tuple5
 * @see Tuple6
 * @see Tuple7
 * @see Tuple8
 * @see Tuple9
 * @see Tuple10
 * @see Tuple11
 * @see Tuple12
 * @see Tuple13
 * @see Tuple14
 * @see Tuple15
 * @see Tuple16
 *
 * @author Kevin Henry
 */
public interface Tuple extends Comparable<Tuple>, Iterable<Object>, Serializable {

    int MAX_DEPTH = 16;

    /**
     * Creates a tuple with a depth of 0
     * @return a tuple of encapsulating the element(s)
     */
    static Tuple0 of() { return new Tuple0(); }

    /**
     * Creates a tuple with a depth of 1
     * @return a tuple of encapsulating the element(s)
     */
    static <T1> Tuple1<T1> of(T1 t1) {
        return new Tuple1<>(t1);
    }

    /**
     * Creates a tuple with a depth of 2
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2> Tuple2<T1,T2> of(T1 t1, T2 t2) {
        return new Tuple2<>(t1,t2);
    }

    /**
     * Creates a tuple with a depth of 3
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3> Tuple3<T1,T2,T3> of(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>(t1,t2,t3);
    }

    /**
     * Creates a tuple with a depth of 4
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4> Tuple4<T1,T2,T3,T4> of(T1 t1, T2 t2, T3 t3, T4 t4) {
        return new Tuple4<>(t1,t2,t3,t4);
    }

    /**
     * Creates a tuple with a depth of 5
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5> Tuple5<T1,T2,T3,T4,T5> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return new Tuple5<>(t1,t2,t3,t4,t5);
    }

    /**
     * Creates a tuple with a depth of 6
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6> Tuple6<T1,T2,T3,T4,T5,T6> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        return new Tuple6<>(t1,t2,t3,t4,t5,t6);
    }

    /**
     * Creates a tuple with a depth of 7
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6,T7> Tuple7<T1,T2,T3,T4,T5,T6,T7> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
        return new Tuple7<>(t1,t2,t3,t4,t5,t6,t7);
    }

    /**
     * Creates a tuple with a depth of 8
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8> Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) {
        return new Tuple8<>(t1,t2,t3,t4,t5,t6,t7,t8);
    }

    /**
     * Creates a tuple with a depth of 9
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9> Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) {
        return new Tuple9<>(t1,t2,t3,t4,t5,t6,t7,t8,t9);
    }

    /**
     * Creates a tuple with a depth of 10
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10) {
        return new Tuple10<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10);
    }

    /**
     * Creates a tuple with a depth of 11
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return new Tuple11<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);
    }

    /**
     * Creates a tuple with a depth of 12
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12) {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12);
    }

    /**
     * Creates a tuple with a depth of 13
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13);
    }

    /**
     * Creates a tuple with a depth of 14
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14);
    }

    /**
     * Creates a tuple with a depth of 15
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15) {
        return new Tuple15<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15);
    }

    /**
     * Creates a tuple with a depth of 16
     * @return a tuple of encapsulating the element(s)
     */
    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16) {
        return new Tuple16<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15,t16);
    }

    /**
     * Adds a value to this tuple at element position.
     * <p>
     * The parameter {@code position} is a positive value that is greater than
     * 0 and less than the depth of this tuple. If the addition of the value
     * results in potentially creating a tuple that has a depth that is greater
     * than {@code MAX_DEPTH}, the method will throw {@link TupleOverflowException}
     * exception.
     * @param position non-zero, positive value that indicates physical
     *                 position to place the value.
     * @param value the value to add to this tuple.
     * @param <Q> type of value.
     * @param <R> type of tuple returned.
     * @return a new tuple with the appropriate type and depth to accommodate
     * the added value.
     */
    <Q,R extends Tuple> R add(int position, Q value);

    /**
     * Determines whether this tuple contains an {@code object}.
     * <p>
     * A tuple is a container of objects of different types. Use this method
     * to determine whether the {@code object} exists within the container;
     * {@code true} is returned to indicate existence.
     * @param object element with which to search.
     * @return true when the object exists in the container.
     */
    boolean contains(Object object);

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
     * Joins a {@code value} to a tuple.
     * <p>
     * The (@code value) is appended to the end the tuple, thus creating a new
     * tuple with the appropriate depth to accommodate the value.
     * <p>
     * @param value the value to append to the end of the new tuple.
     * @param <Q> the type of the value.
     * @param <R> the type of the new tuple returned.
     * @return a new tuple with the appropriate depth to house the value.
     */
    default <Q, R extends Tuple> R join(Q value) {
        return join(Tuple.of(value));
    }

    /**
     * Joins a {@code that} tuple to this tuple, resulting in a new tuple.
     * <p>
     * The (@code that) tuple contents are appended to the end this tuple, thus
     * creating a new tuple with the appropriate depth to accommodate the
     * merged contents.
     * <p>
     * If teh combined depth of both this and {@code that} tuple is greater
     * that {@ode MAX_DEPTH}, the exception {@link TupleOverflowException}
     * is thrown.
     * @param that is a tuple whose contents are to be merged with this tuple's
     *             contents.
     * @param <Q> the type of the tuple.
     * @param <R> the type of the new tuple returned.
     * @return a new tuple with the appropriate depth to house the value.
     */
    <Q extends Tuple, R extends Tuple> R join(Q that);

    /**
     * Splices or cuts this tuple into two smaller tuples at {@code position}.
     * <p>
     * This method is almost the direct opposite to the join, except that the
     * resultant tuple returned is a container of two smaller tuples. For example,
     * given the tuple {@code [1,2,3,4,5]}. If the tuple is spliced at position 3,
     * the method will return the following {@code [[1,2],[3,4,5]]} in the
     * {@link Tuple2} container. Use the methods of {@link Tuple2} to access the
     * sliced the tuples.
     *
     * @param position is the location in this tuple with which to perform the
     *                 slicing.
     * @param <Q>      type of the first tuple in the returned {@link Tuple2}
     *           container.
     * @param <R>      type of the second tuple in the returned {@link Tuple2}
     *           container.
     * @return a new {@link Tuple2} containing tuple slices.
     */
    <Q extends Tuple, R extends Tuple> Tuple2<Q,R> splice(int position);

    /**
     * Truncates this tuple at {@code position}
     * <p>
     * Method returns a new truncated tuple, a tuple that has had the remaining
     * elements discarded. For example the tuple {@code [1,2,3,4,5]} truncated at
     * {@code position} 3 would result in a new tuple {@code [1,2]}
     *
     * @param position the truncation position
     * @param <T> type of tuple returned.
     * @return a new truncated tuple.
     */
    <T extends Tuple> T truncate(int position);

    /**
     * Returns an object array of elements contained in this tuple.
     * <p>
     * If a tuple is considered empty, an array with zero elements is returned.
     * @return object array.
     */
    Object[] toArray();

    /**
     * Returns an {@link Map<K,?>} of elements within this tuple.
     * <p>
     * Implement the function that returns a key value for a given index of the
     * element in the tuple.
     * @param keyMapper Function that returns a key value for a tuple element.
     * @param <K> type of returned value.
     * @return a {@link Map<K,?>} object.
     */
    <K> Map<K, ?> toMap(Function<? super Integer, ? extends K> keyMapper);

    /**
     * Returns a {@link List<?>} object representing the contents of this tuple.
     * @return a {@link List<?>} object.
     */
    List<?> toList();

}