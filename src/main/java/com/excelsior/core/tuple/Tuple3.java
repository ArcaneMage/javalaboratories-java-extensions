package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;

import java.util.function.Function;

/**
 * A tuple with depth of 3
 *
 * @param <T1> type of 1st element
 * @param <T2> type of 2nd element
 * @param <T3> type of 3rd element
 *
 * @author Kevin Henry
 */
public final class Tuple3<T1,T2,T3> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;

    public Tuple3(T1 t1, T2 t2, T3 t3) {
        super(t1,t2,t3);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    /**
     * Converts iterable into a tuple, if possible.
     * <p>
     * Creates a tuple to a depth of 3 from iterable object. If there is
     * insufficient elements, then {@link Nullable} will be empty.
     *
     * @param iterable Iterable object
     * @param <T> iterable type.
     * @return A tuple in {@link Nullable} object container.
     */
    public static <T> Nullable<Tuple3<T,T,T>> fromIterable(Iterable<T> iterable) {
        return Tuples.fromIterable(iterable, 3);
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

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple3<R,T2,T3> transform1(Function<? super T1,? extends R> function) {
        return new Tuple3<>(function.apply(t1),t2,t3);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple3<T1,R,T3> transform2(Function<? super T2,? extends R> function) {
        return new Tuple3<>(t1,function.apply(t2),t3);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple3<T1,T2,R> transform3(Function<? super T3,? extends R> function) {
        return new Tuple3<>(t1,t2,function.apply(t3));
    }
}
