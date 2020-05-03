package com.excelsior.core.tuple;

/**
 * This exception is thrown when tuple element types are not in the same order
 * when comparing.
 * <p>
 * Tuples element types must be in the same order for the collection sort to
 * function correctly. For example, tuple with types {@code [String,Integer,Date]} will
 * not compare well with tuple with types {@code [String,Date,Integer]} because
 * the types are not in the same order.
 */
public class TupleComparableException extends ClassCastException {
    public TupleComparableException(String s) {
        super(s);
    }
}
