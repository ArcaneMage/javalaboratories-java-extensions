package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;

import java.util.function.Function;

/**
 * A tuple with depth of 6
 *
 * @param <T1> type of 1st element
 * @param <T2> type of 2nd element
 * @param <T3> type of 3rd element
 * @param <T4> type of 4th element
 * @param <T5> type of 5th element
 * @param <T6> type of 6th element
 *
 * @author Kevin Henry
 */
public final class Tuple6<T1,T2,T3,T4,T5,T6> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;
    private final T5 t5;
    private final T6 t6;

    public Tuple6(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        super(t1,t2,t3,t4,t5,t6);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
    }

    /**
     * Converts iterable into a tuple, if possible.
     * <p>
     * Creates a tuple to a depth of 6 from iterable object. If there is
     * insufficient elements, then {@link Nullable} will be empty.
     *
     * @param iterable Iterable object
     * @param <T> iterable type.
     * @return A tuple in {@link Nullable} object container.
     */
    public static <T> Nullable<Tuple6<T,T,T,T,T,T>> fromIterable(Iterable<T> iterable) {
        return Tuples.fromIterable(iterable, 6);
    }

    public T1 value1() {
        return t1;
    }

    public T2 value2() {
        return t2;
    }

    public T3 value3() {
        return t3;
    }

    public T4 value4() {
        return t4;
    }

    public T5 value5() {
        return t5;
    }

    public T6 value6() {
        return t6;
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple6<R,T2,T3,T4,T5,T6> transform1(Function<? super T1,? extends R> function) {
        return new Tuple6<>(function.apply(t1),t2,t3,t4,t5,t6);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple6<T1,R,T3,T4,T5,T6> transform2(Function<? super T2,? extends R> function) {
        return new Tuple6<>(t1,function.apply(t2),t3,t4,t5,t6);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple6<T1,T2,R,T4,T5,T6> transform3(Function<? super T3,? extends R> function) {
        return new Tuple6<>(t1,t2,function.apply(t3),t4,t5,t6);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple6<T1,T2,T3,R,T5,T6> transform4(Function<? super T4,? extends R> function) {
        return new Tuple6<>(t1,t2,t3,function.apply(t4),t5,t6);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple6<T1,T2,T3,T4,R,T6> transform5(Function<? super T5,? extends R> function) {
        return new Tuple6<>(t1,t2,t3,t4,function.apply(t5),t6);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple6<T1,T2,T3,T4,T5,R> transform6(Function<? super T6,? extends R> function) {
        return new Tuple6<>(t1,t2,t3,t4,t5,function.apply(t6));
    }
}

