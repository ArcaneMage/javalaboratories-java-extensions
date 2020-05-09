package com.excelsior.core.function;

/**
 * Represents a consumer function that accepts 15 parameters.
 * <p>
 * This is a functional interface whose functional method is
 * {@link Consumer15#accept(Object, Object, Object, Object, Object, Object,
 * Object, Object, Object, Object, Object, Object, Object, Object, Object)}.
 * @param <T1> type of first parameter
 * @param <T2> type of second parameter
 * @param <T3> type of third parameter
 * @param <T4> type of fourth parameter
 * @param <T5> type of fifth parameter
 * @param <T6> type of sixth parameter
 * @param <T7> type of seventh parameter
 * @param <T8> type of eighth parameter
 * @param <T9> type of ninth parameter
 * @param <T10> type of tenth parameter
 * @param <T11> type of eleventh parameter
 * @param <T12> type of twelfth parameter
 * @param <T13> type of thirteenth parameter
 * @param <T14> type of fourteenth parameter
 * @param <T15> type of fifteenth parameter
 */
@FunctionalInterface
public interface Consumer15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> {

    /**
     * Performs the operation with the given parameters.
     * @param t1 first parameter
     * @param t2 second parameter
     * @param t3 third parameter
     * @param t4 fourth parameter
     * @param t5 fifth parameter
     * @param t6 sixth parameter
     * @param t7 seventh parameter
     * @param t8 eighth parameter
     * @param t9 ninth parameter
     * @param t10 tenth parameter
     * @param t11 eleventh parameter
     * @param t12 twelfth parameter
     * @param t13 thirteenth parameter
     * @param t14 fourteenth parameter
     * @param t15 fifteenth parameter
     */
    void accept(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15);
}
