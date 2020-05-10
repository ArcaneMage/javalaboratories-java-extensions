package com.excelsior.core.tuple;

/**
 * Tuples, which implement this interface, with the exception of {@link Matcher}
 * objects, are considered to be "joinable".
 * <p>
 * That is to mean they have the ability to be concatenated to form a new
 * tuple.
 */
public interface JoinableTuple<T extends Tuple> {
    /**
     * Joins this tuple with {@code that} tuple object.
     * <p>
     * @param that tuple to join with this object.
     * @param <R> type of this tuple.
     * @return instance of newly joined tuple.
     */
    <R extends Tuple> R join(final T that);
}
