package com.excelsior.core.function;

/**
 * Represents a function that accepts 4 parameters and returns a result.
 * <p>
 * This is a functional interface whose functional method is
 * {@link Function4#apply(Object, Object, Object, Object)}.
 * @param <T1> type of first parameter
 * @param <T2> type of second parameter
 * @param <T3> type of third parameter
 * @param <T4> type of fourth parameter
 * @param <R> type of returned value
 */
@FunctionalInterface
public interface Function4<T1,T2,T3,T4,R> {

    /**
     * Applies this method to the given parameters.
     * @param t1 first parameter
     * @param t2 second parameter
     * @param t3 third parameter
     * @param t4 fourth parameter
     * @return the result of the function
     */
    R apply(T1 t1, T2 t2, T3 t3, T4 t4);
}
