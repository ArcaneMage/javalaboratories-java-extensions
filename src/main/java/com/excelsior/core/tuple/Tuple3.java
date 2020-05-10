package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;
import com.excelsior.core.function.Consumer3;
import com.excelsior.core.function.Function3;

import java.util.Objects;
import java.util.function.Function;

import static com.excelsior.core.tuple.Tuple.of;

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
     * Add value at position 1
     */
    public <T> Tuple4<T,T1,T2,T3> addAt1(T value) {
        return add(1,value);
    }

    /**
     * Add value at position 2
     */
    public <T> Tuple4<T1,T,T2,T3> addAt2(T value) {
        return add(2,value);
    }

    /**
     * Add value at position 3
     */
    public <T> Tuple4<T1,T2,T,T3> addAt3(T value) {
        return add(3,value);
    }

    /**
     * Hop to position/value 1
     */
    public Tuple3<T1,T2,T3> hopTo1() {
        return hop(1);
    }

    /**
     * Hop to position/value 2
     */
    public Tuple2<T2,T3> hopTo2() {
        return hop(2);
    }

    /**
     * Hop to position/value 3
     */
    public Tuple1<T3> hopTo3() {
        return hop(3);
    }

    /**
     * Joins a tuple to this tuple.
     * @param value a tuple object.
     */
    public <T> Tuple4<T1,T2,T3,T> join(T value) {
        return join(of(value));
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public Tuple3<T1,T2,T3> join(Tuple0 tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4> Tuple4<T1,T2,T3,T4> join(Tuple1<T4> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5> Tuple5<T1,T2,T3,T4,T5> join(Tuple2<T4,T5> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6> Tuple6<T1,T2,T3,T4,T5,T6> join(Tuple3<T4,T5,T6> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6,T7> Tuple7<T1,T2,T3,T4,T5,T6,T7> join(Tuple4<T4,T5,T6,T7> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6,T7,T8> Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> join(Tuple5<T4,T5,T6,T7,T8> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6,T7,T8,T9> Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> join(Tuple6<T4,T5,T6,T7,T8,T9> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6,T7,T8,T9,T10> Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> join(Tuple7<T4,T5,T6,T7,T8,T9,T10> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6,T7,T8,T9,T10,T11> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> join(Tuple8<T4,T5,T6,T7,T8,T9,T10,T11> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6,T7,T8,T9,T10,T11,T12> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> join(Tuple9<T4,T5,T6,T7,T8,T9,T10,T11,T12> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> join(Tuple10<T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> join(Tuple11<T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> join(Tuple12<T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> join(Tuple13<T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> tuple) {
        return super.join(tuple);
    }

    /**
     * Splices at positions 1
     */
    public Tuple2<Tuple0,Tuple3<T1,T2,T3>> spliceAt1() {
        return splice(1);
    }

    /**
     * Splices at positions 2
     */
    public Tuple2<Tuple1<T1>,Tuple2<T2,T3>> spliceAt2() {
        return splice(2);
    }

    /**
     * Splices at positions 3
     */
    public Tuple2<Tuple2<T1,T2>,Tuple1<T3>> spliceAt3() {
        return splice(3);
    }

    /**
     * Transform this tuple into another object
     * @param function function that performs the transformation
     * @param <R> return type of transformation
     * @return transformed object.
     */
    public <R> R map(Function3<? super T1,? super T2,? super T3,? extends R> function) {
        return function.apply(t1,t2,t3);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple3<R,T2,T3> mapAt1(Function<? super T1,? extends R> function) {
        return new Tuple3<>(function.apply(t1),t2,t3);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple3<T1,R,T3> mapAt2(Function<? super T2,? extends R> function) {
        return new Tuple3<>(t1,function.apply(t2),t3);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple3<T1,T2,R> mapAt3(Function<? super T3,? extends R> function) {
        return new Tuple3<>(t1,t2,function.apply(t3));
    }

    /**
     * Tests whether given {@code tuple} is equal to this {@code tuple}, and if
     * true, the {@code consumer} function is executed.
     * <p>
     * This is particularly useful if the tuple contents are unknown and when
     * discovered, the {@code consumer} function is performed.
     * <pre>
     * {@code
     *      tuple
     *        .match(when("John","Wellington"), (a,b,c) -> System.out.println(a))
     *        .match(when("Alex","Wall",23), (a,b,c) -> System.out.println(b))
     * }
     * </pre>
     * @param matcher object to use with this tuple.
     * @param consumer function to execute if match is found.
     * @param <Q> type of matcher.
     * @return this tuple -- useful for multiple matches.
     */
    public <Q extends Matcher> Tuple3<T1,T2,T3> match(final Q matcher, final Consumer3<? super T1,? super T2,? super T3> consumer) {
        Objects.requireNonNull(consumer);
        Tuple3<T1,T2,T3> result = this;
        if (matcher.match(this))
            consumer.accept(t1,t2,t3);

        return result;
    }

    /**
     * Truncates tuples at position 1
     */
    public Tuple0 truncateAt1() {
        return truncate(1);
    }

    /**
     * Truncates tuples at position 2
     */
    public Tuple1<T1> truncateAt2() {
        return truncate(2);
    }

    /**
     * Truncates tuples at position 3
     */
    public Tuple2<T1,T2> truncateAt3() {
        return truncate(3);
    }
}
