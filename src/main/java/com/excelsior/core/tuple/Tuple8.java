package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;

import java.util.function.Function;

/**
 * A tuple with depth of 8
 *
 * @param <T1> type of 1st element
 * @param <T2> type of 2nd element
 * @param <T3> type of 3rd element
 * @param <T4> type of 4th element
 * @param <T5> type of 5th element
 * @param <T6> type of 6th element
 * @param <T7> type of 7th element
 * @param <T8> type of 8th element
 *
 * @author Kevin Henry
 */
public final class Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;
    private final T5 t5;
    private final T6 t6;
    private final T7 t7;
    private final T8 t8;

    public Tuple8(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) {
        super(t1,t2,t3,t4,t5,t6,t7,t8);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
        this.t7 = t7;
        this.t8 = t8;
    }

    /**
     * Converts iterable into a tuple, if possible.
     * <p>
     * Creates a tuple to a depth of 8 from iterable object. If there is
     * insufficient elements, then {@link Nullable} will be empty.
     *
     * @param iterable Iterable object
     * @param <T> iterable type.
     * @return A tuple in {@link Nullable} object container.
     */
    public static <T> Nullable<Tuple8<T,T,T,T,T,T,T,T>> fromIterable(Iterable<T> iterable) {
        return Tuples.fromIterable(iterable, 8);
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

    public T7 value7() {
        return t7;
    }

    public T8 value8() {
        return t8;
    }

    /**
     * Add value at position 1
     */
    public <T> Tuple9<T,T1,T2,T3,T4,T5,T6,T7,T8> addAt1(T value) {
        return add(1,value);
    }

    /**
     * Add value at position 2
     */
    public <T> Tuple9<T1,T,T2,T3,T4,T5,T6,T7,T8> addAt2(T value) {
        return add(2,value);
    }

    /**
     * Add value at position 3
     */
    public <T> Tuple9<T1,T2,T,T3,T4,T5,T6,T7,T8> addAt3(T value) {
        return add(3,value);
    }

    /**
     * Add value at position 4
     */
    public <T> Tuple9<T1,T2,T3,T,T4,T5,T6,T7,T8> addAt4(T value) {
        return add(4,value);
    }

    /**
     * Add value at position 5
     */
    public <T> Tuple9<T1,T2,T3,T4,T,T5,T6,T7,T8> addAt5(T value) {
        return add(5,value);
    }

    /**
     * Add value at position 6
     */
    public <T> Tuple9<T1,T2,T3,T4,T5,T,T6,T7,T8> addAt6(T value) {
        return add(6,value);
    }

    /**
     * Add value at position 7
     */
    public <T> Tuple9<T1,T2,T3,T4,T5,T6,T,T7,T8> addAt7(T value) {
        return add(7,value);
    }

    /**
     * Add value at position 8
     */
    public <T> Tuple9<T1,T2,T3,T4,T5,T6,T7,T,T8> addAt8(T value) {
        return add(8,value);
    }

    /**
     * Hop to position/value 1
     */
    public Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> hopTo1() {
        return hop(1);
    }

    /**
     * Hop to position/value 2
     */
    public Tuple7<T2,T3,T4,T5,T6,T7,T8> hopTo2() {
        return hop(2);
    }

    /**
     * Hop to position/value 3
     */
    public Tuple6<T3,T4,T5,T6,T7,T8> hopTo3() {
        return hop(3);
    }

    /**
     * Hop to position/value 4
     */
    public Tuple5<T4,T5,T6,T7,T8> hopTo4() {
        return hop(4);
    }

    /**
     * Hop to position/value 5
     */
    public Tuple4<T5,T6,T7,T8> hopTo5() {
        return hop(5);
    }

    /**
     * Hop to position/value 6
     */
    public Tuple3<T6,T7,T8> hopTo6() {
        return hop(6);
    }

    /**
     * Hop to position/value 7
     */
    public Tuple2<T7,T8> hopTo7() {
        return hop(7);
    }

    /**
     * Hop to position/value 8
     */
    public Tuple1<T8> hopTo8() {
        return hop(8);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> join(Tuple0 tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T9> Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> join(Tuple1<T9> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T9,T10> Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> join(Tuple2<T9,T10> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T9,T10,T11> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> join(Tuple3<T9,T10,T11> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T9,T10,T11,T12> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> join(Tuple4<T9,T10,T11,T12> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T9,T10,T11,T12,T13> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> join(Tuple5<T9,T10,T11,T12,T13> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T9,T10,T11,T12,T13,T14> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> join(Tuple6<T9,T10,T11,T12,T13,T14> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T9,T10,T11,T12,T13,T14,T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> join(Tuple7<T9,T10,T11,T12,T13,T14,T15> tuple) {
        return super.join(tuple);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T9,T10,T11,T12,T13,T14,T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> join(Tuple8<T9,T10,T11,T12,T13,T14,T15,T16> tuple) {
        return super.join(tuple);
    }

    /**
     * Splices at positions 1
     */
    public Tuple2<Tuple0,Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>> spliceAt1() {
        return splice(1);
    }

    /**
     * Splices at positions 2
     */
    public Tuple2<Tuple1<T1>,Tuple7<T2,T3,T4,T5,T6,T7,T8>> spliceAt2() {
        return splice(2);
    }

    /**
     * Splices at positions 3
     */
    public Tuple2<Tuple2<T1,T2>,Tuple6<T3,T4,T5,T6,T7,T8>> spliceAt3() {
        return splice(3);
    }

    /**
     * Splices at positions 4
     */
    public Tuple2<Tuple3<T1,T2,T3>,Tuple5<T4,T5,T6,T7,T8>> spliceAt4() {
        return splice(4);
    }

    /**
     * Splices at positions 5
     */
    public Tuple2<Tuple4<T1,T2,T3,T4>,Tuple4<T5,T6,T7,T8>> spliceAt5() {
        return splice(5);
    }

    /**
     * Splices at positions 6
     */
    public Tuple2<Tuple5<T1,T2,T3,T4,T5>,Tuple3<T6,T7,T8>> spliceAt6() {
        return splice(6);
    }

    /**
     * Splices at positions 7
     */
    public Tuple2<Tuple6<T1,T2,T3,T4,T5,T6>,Tuple2<T7,T8>> spliceAt7() {
        return splice(7);
    }

    /**
     * Splices at positions 8
     */
    public Tuple2<Tuple7<T1,T2,T3,T4,T5,T6,T7>,Tuple1<T8>> spliceAt8() {
        return splice(8);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple8<R,T2,T3,T4,T5,T6,T7,T8> transform1(Function<? super T1,? extends R> function) {
        return new Tuple8<>(function.apply(t1),t2,t3,t4,t5,t6,t7,t8);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple8<T1,R,T3,T4,T5,T6,T7,T8> transform2(Function<? super T2,? extends R> function) {
        return new Tuple8<>(t1,function.apply(t2),t3,t4,t5,t6,t7,t8);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple8<T1,T2,R,T4,T5,T6,T7,T8> transform3(Function<? super T3,? extends R> function) {
        return new Tuple8<>(t1,t2,function.apply(t3),t4,t5,t6,t7,t8);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple8<T1,T2,T3,R,T5,T6,T7,T8> transform4(Function<? super T4,? extends R> function) {
        return new Tuple8<>(t1,t2,t3,function.apply(t4),t5,t6,t7,t8);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple8<T1,T2,T3,T4,R,T6,T7,T8> transform5(Function<? super T5,? extends R> function) {
        return new Tuple8<>(t1,t2,t3,t4,function.apply(t5),t6,t7,t8);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple8<T1,T2,T3,T4,T5,R,T7,T8> transform6(Function<? super T6,? extends R> function) {
        return new Tuple8<>(t1,t2,t3,t4,t5,function.apply(t6),t7,t8);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple8<T1,T2,T3,T4,T5,T6,R,T8> transform7(Function<? super T7,? extends R> function) {
        return new Tuple8<>(t1,t2,t3,t4,t5,t6,function.apply(t7),t8);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple8<T1,T2,T3,T4,T5,T6,T7,R> transform8(Function<? super T8,? extends R> function) {
        return new Tuple8<>(t1,t2,t3,t4,t5,t6,t7,function.apply(t8));
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

    /**
     * Truncates tuples at position 4
     */
    public Tuple3<T1,T2,T3> truncateAt4() {
        return truncate(4);
    }

    /**
     * Truncates tuples at position 5
     */
    public Tuple4<T1,T2,T3,T4> truncateAt5() {
        return truncate(5);
    }

    /**
     * Truncates tuples at position 6
     */
    public Tuple5<T1,T2,T3,T4,T5> truncateAt6() {
        return truncate(6);
    }

    /**
     * Truncates tuples at position 7
     */
    public Tuple6<T1,T2,T3,T4,T5,T6> truncateAt7() {
        return truncate(7);
    }

    /**
     * Truncates tuples at position 8
     */
    public Tuple7<T1,T2,T3,T4,T5,T6,T7> truncateAt8() {
        return truncate(8);
    }
}
