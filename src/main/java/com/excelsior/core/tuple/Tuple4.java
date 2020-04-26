package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;

import java.util.function.Function;

/**
 * A tuple with depth of 4
 *
 * @param <T1> type of 1st element
 * @param <T2> type of 2nd element
 * @param <T3> type of 3rd element
 * @param <T4> type of 4th element
 *
 * @author Kevin Henry
 */
public final class Tuple4<T1,T2,T3,T4> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;

    public Tuple4(T1 t1, T2 t2, T3 t3, T4 t4) {
        super(t1,t2,t3,t4);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
    }

    /**
     * Converts iterable into a tuple, if possible.
     * <p>
     * Creates a tuple to a depth of 4 from iterable object. If there is
     * insufficient elements, then {@link Nullable} will be empty.
     *
     * @param iterable Iterable object
     * @param <T> iterable type.
     * @return A tuple in {@link Nullable} object container.
     */
    public static <T> Nullable<Tuple4<T,T,T,T>> fromIterable(Iterable<T> iterable) {
        return Tuples.fromIterable(iterable, 4);
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

    /**
     * Splices this tuple into two partitions at element position 1
     */
    public Tuple2<Tuple1<T1>,Tuple3<T2,T3,T4>> splice1() { return splice(1); }

    /**
     * Splices this tuple into two partitions at element position 2
     */
    public Tuple2<Tuple2<T1,T2>,Tuple2<T3,T4>> splice2() { return splice(2); }

    /**
     * Splices this tuple into two partitions at element position 3
     */
    public Tuple2<Tuple3<T1,T2,T3>,Tuple1<T4>> splice3() { return splice(3); }

    /**
     * Truncates remaining tuples to a depth of 1
     */
    public Tuple1<T1> truncate1() {
        return new Tuple1<>(t1);
    }

    /**
     * Truncates remaining tuples to a depth of 2
     */
    public Tuple2<T1,T2> truncate2() {
        return new Tuple2<>(t1,t2);
    }

    /**
     * Truncates remaining tuples to a depth of 3
     */
    public Tuple3<T1,T2,T3> truncate3() {
        return new Tuple3<>(t1,t2,t3);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple4<R,T2,T3,T4> transform1(Function<? super T1,? extends R> function) {
        return new Tuple4<>(function.apply(t1),t2,t3,t4);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple4<T1,R,T3,T4> transform2(Function<? super T2,? extends R> function) {
        return new Tuple4<>(t1,function.apply(t2),t3,t4);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple4<T1,T2,R,T4> transform3(Function<? super T3,? extends R> function) {
        return new Tuple4<>(t1,t2,function.apply(t3),t4);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple4<T1,T2,T3,R> transform4(Function<? super T4,? extends R> function) {
        return new Tuple4<>(t1,t2,t3,function.apply(t4));
    }
}
