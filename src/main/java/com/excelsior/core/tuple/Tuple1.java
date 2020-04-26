package com.excelsior.core.tuple;

import com.excelsior.core.Nullable;

import java.util.function.Function;

/**
 * A tuple with depth of 1
 *
 * @param <T1> type of 1st element
 *
 * @author Kevin Henry
 */
public final class Tuple1<T1> extends AbstractTuple {
    private final T1 t1;

    public Tuple1(T1 t1) {
        super(t1);
        this.t1 = t1;
    }

    /**
     * Converts iterable into a tuple, if possible.
     * <p>
     * Creates a tuple to a depth of 1 from iterable object. If there is
     * insufficient elements, then {@link Nullable} will be empty.
     *
     * @param iterable Iterable object
     * @param <T> iterable type.
     * @return A tuple in {@link Nullable} object container.
     */
    public static <T> Nullable<Tuple1<T>> fromIterable(Iterable<T> iterable) {
        return Tuples.fromIterable(iterable, 1);
    }

    public T1 value1() {
        return t1;
    }

    /**
     * Transform this tuple into another object
     * @param function function that performs the transformation
     * @param <R> return type of transformation
     * @return transformed object.
     */
    public <R> R transform(Function<? super T1,? extends R> function) {
        return function.apply(t1);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple1<R> transform1(Function<? super T1,? extends R> function) {
        return new Tuple1<>(function.apply(t1));
    }
}
