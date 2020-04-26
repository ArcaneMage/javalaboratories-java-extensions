package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * A tuple with depth of 2
 *
 * @param <T1> type of 1st element
 * @param <T2> type of 2nd element
 *
 * @author Kevin Henry
 */
public final class Tuple2<T1,T2> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;

    public Tuple2(T1 t1, T2 t2) {
        super(t1,t2);
        this.t1 = t1;
        this.t2 = t2;
    }

    /**
     * Converts iterable into a tuple, if possible.
     * <p>
     * Creates a tuple to a depth of 2 from iterable object. If there is
     * insufficient elements, then {@link Nullable} will be empty.
     *
     * @param iterable Iterable object
     * @param <T> iterable type.
     * @return A tuple in {@link Nullable} object container.
     */
    public static <T> Nullable<Tuple2<T,T>> fromIterable(Iterable<T> iterable) {
        return Tuples.fromIterable(iterable, 2);
    }

    public T1 value1() {
        return t1;
    }

    public T2 value2() {
        return t2;
    }

    /**
     * Joins a value to the end of this tuple.
     */
  /*  public <T> Tuple3<T1,T2,T> join(T value) {
        return new Tuple3<>(t1,t2,value);
    }
*/
    /**
     * Splices this tuple into two partitions at element position 1
     */
    public Tuple2<Tuple1<T1>,Tuple1<T2>> splice1() { return splice(1); }

    /**
     * Transform this tuple into another object
     * @param function function that performs the transformation
     * @param <R> return type of transformation
     * @return transformed object.
     */
    public <R> R transform(BiFunction<? super T1,? super T2,? extends R> function) {
        return function.apply(t1,t2);
    }

    /**
     * Truncates remaining tuples to a depth of 1
     */
    public Tuple1<T1> truncate1() {
        return new Tuple1<>(t1);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple2<R,T2> transform1(Function<? super T1,? extends R> function) {
        return new Tuple2<>(function.apply(t1),t2);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple2<T1,R> transform2(Function<? super T2,? extends R> function) {
        return new Tuple2<>(t1,function.apply(t2));
    }
}
