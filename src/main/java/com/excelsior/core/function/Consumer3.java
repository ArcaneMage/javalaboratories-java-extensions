package com.excelsior.core.function;

/**
 * Represents a consumer function that accepts 3 parameters.
 * <p>
 * This is a functional interface whose functional method is
 * {@link Consumer3#accept(Object, Object, Object)}.
 * @param <T1> type of first parameter
 * @param <T2> type of second parameter
 * @param <T3> type of third parameter
 */
@FunctionalInterface
public interface Consumer3<T1,T2,T3> {

    /**
     * Performs the operation with the given parameters.
     * @param t1 first parameter
     * @param t2 second parameter
     * @param t3 third parameter
     */
    void accept(T1 t1, T2 t2, T3 t3);
}
