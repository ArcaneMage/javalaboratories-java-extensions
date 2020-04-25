package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;

import java.util.function.Function;

/**
 * A tuple with depth of 12
 *
 * @param <T1> type of 1st element
 * @param <T2> type of 2nd element
 * @param <T3> type of 3rd element
 * @param <T4> type of 4th element
 * @param <T5> type of 5th element
 * @param <T6> type of 6th element
 * @param <T7> type of 7th element
 * @param <T8> type of 8th element
 * @param <T9> type of 9th element
 * @param <T10> type of 10th element
 * @param <T11> type of 11th element
 * @param <T12> type of 12th element
 *
 * @author Kevin Henry
 */
public final class Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> extends AbstractTuple {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;
    private final T5 t5;
    private final T6 t6;
    private final T7 t7;
    private final T8 t8;
    private final T9 t9;
    private final T10 t10;
    private final T11 t11;
    private final T12 t12;

    public Tuple12(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12) {
        super(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
        this.t7 = t7;
        this.t8 = t8;
        this.t9 = t9;
        this.t10 = t10;
        this.t11 = t11;
        this.t12 = t12;
    }

    /**
     * Converts iterable into a tuple, if possible.
     * <p>
     * Creates a tuple to a depth of 12 from iterable object. If there is
     * insufficient elements, then {@link Nullable} will be empty.
     *
     * @param iterable Iterable object
     * @param <T> iterable type.
     * @return A tuple in {@link Nullable} object container.
     */
    public static <T> Nullable<Tuple12<T,T,T,T,T,T,T,T,T,T,T,T>> fromIterable(Iterable<T> iterable) {
        return Tuples.fromIterable(iterable, 12);
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

    public T7 value7() { return t7; }

    public T8 value8() { return t8; }

    public T9 value9() { return t9; }

    public T10 value10() { return t10; }

    public T11 value11() { return t11; }

    public T12 value12() { return t12; }

    /**
     * Joins a tuple to this tuple.
     * @param value a tuple object.
     */
    public <T> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T> join(T value) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,value);
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T13> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> join(Tuple1<T13> tuple) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,tuple.value1());
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T13,T14> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> join(Tuple2<T13,T14> tuple) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,tuple.value1(),tuple.value2());
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T13,T14,T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> join(Tuple3<T13,T14,T15> tuple) {
        return new Tuple15<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,tuple.value1(),tuple.value2(),tuple.value3());
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T13,T14,T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> join(Tuple4<T13,T14,T15,T16> tuple) {
        return new Tuple16<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4());
    }
    
    /**
     * Splices this tuple into two partitions at element position 1
     */
    public Tuple2<Tuple1<T1>,Tuple11<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> splice1() { return splice(1); }

    /**
     * Splices this tuple into two partitions at element position 2
     */
    public Tuple2<Tuple2<T1,T2>,Tuple10<T3,T4,T5,T6,T7,T8,T9,T10,T11,T12>> splice2() { return splice(2); }

    /**
     * Splices this tuple into two partitions at element position 3
     */
    public Tuple2<Tuple3<T1,T2,T3>,Tuple9<T4,T5,T6,T7,T8,T9,T10,T11,T12>> splice3() { return splice(3); }

    /**
     * Splices this tuple into two partitions at element position 4
     */
    public Tuple2<Tuple4<T1,T2,T3,T4>,Tuple8<T5,T6,T7,T8,T9,T10,T11,T12>> splice4() { return splice(4); }

    /**
     * Splices this tuple into two partitions at element position 5
     */
    public Tuple2<Tuple5<T1,T2,T3,T4,T5>,Tuple7<T6,T7,T8,T9,T10,T11,T12>> splice5() { return splice(5); }

    /**
     * Splices this tuple into two partitions at element position 6
     */
    public Tuple2<Tuple6<T1,T2,T3,T4,T5,T6>,Tuple6<T7,T8,T9,T10,T11,T12>> splice6() { return splice(6); }

    /**
     * Splices this tuple into two partitions at element position 7
     */
    public Tuple2<Tuple7<T1,T2,T3,T4,T5,T6,T7>,Tuple5<T8,T9,T10,T11,T12>> splice7() { return splice(7); }

    /**
     * Splices this tuple into two partitions at element position 8
     */
    public Tuple2<Tuple8<T1,T2,T3,T4,T5,T6,T7,T8>,Tuple4<T9,T10,T11,T12>> splice8() { return splice(8); }

    /**
     * Splices this tuple into two partitions at element position 9
     */
    public Tuple2<Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9>,Tuple3<T10,T11,T12>> splice9() { return splice(9); }

    /**
     * Splices this tuple into two partitions at element position 10
     */
    public Tuple2<Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10>,Tuple2<T11,T12>> splice10() { return splice(10); }

    /**
     * Splices this tuple into two partitions at element position 11
     */
    public Tuple2<Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11>,Tuple1<T12>> splice11() { return splice(11); }

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
     * Truncates remaining tuples to a depth of 4
     */
    public Tuple4<T1,T2,T3,T4> truncate4() {
        return new Tuple4<>(t1,t2,t3,t4);
    }

    /**
     * Truncates remaining tuples to a depth of 5
     */
    public Tuple5<T1,T2,T3,T4,T5> truncate5() {
        return new Tuple5<>(t1,t2,t3,t4,t5);
    }

    /**
     * Truncates remaining tuples to a depth of 6
     */
    public Tuple6<T1,T2,T3,T4,T5,T6> truncate6() {
        return new Tuple6<>(t1,t2,t3,t4,t5,t6);
    }

    /**
     * Truncates remaining tuples to a depth of 7
     */
    public Tuple7<T1,T2,T3,T4,T5,T6,T7> truncate7() {
        return new Tuple7<>(t1,t2,t3,t4,t5,t6,t7);
    }

    /**
     * Truncates remaining tuples to a depth of 8
     */
    public Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> truncate8() {
        return new Tuple8<>(t1,t2,t3,t4,t5,t6,t7,t8);
    }

    /**
     * Truncates remaining tuples to a depth of 9
     */
    public Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> truncate9() {
        return new Tuple9<>(t1,t2,t3,t4,t5,t6,t7,t8,t9);
    }

    /**
     * Truncates remaining tuples to a depth of 10
     */
    public Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> truncate10() {
        return new Tuple10<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10);
    }

    /**
     * Truncates remaining tuples to a depth of 11
     */
    public Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> truncate11() {
        return new Tuple11<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<R,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> transform1(Function<? super T1,? extends R> function) {
        return new Tuple12<>(function.apply(t1),t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,R,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> transform2(Function<? super T2,? extends R> function) {
        return new Tuple12<>(t1,function.apply(t2),t3,t4,t5,t6,t7,t8,t9,t10,t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,T2,R,T4,T5,T6,T7,T8,T9,T10,T11,T12> transform3(Function<? super T3,? extends R> function) {
        return new Tuple12<>(t1,t2,function.apply(t3),t4,t5,t6,t7,t8,t9,t10,t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,T2,T3,R,T5,T6,T7,T8,T9,T10,T11,T12> transform4(Function<? super T4,? extends R> function) {
        return new Tuple12<>(t1,t2,t3,function.apply(t4),t5,t6,t7,t8,t9,t10,t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,T2,T3,T4,R,T6,T7,T8,T9,T10,T11,T12> transform5(Function<? super T5,? extends R> function) {
        return new Tuple12<>(t1,t2,t3,t4,function.apply(t5),t6,t7,t8,t9,t10,t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,T2,T3,T4,T5,R,T7,T8,T9,T10,T11,T12> transform6(Function<? super T6,? extends R> function) {
        return new Tuple12<>(t1,t2,t3,t4,t5,function.apply(t6),t7,t8,t9,t10,t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,T2,T3,T4,T5,T6,R,T8,T9,T10,T11,T12> transform7(Function<? super T7,? extends R> function) {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,function.apply(t7),t8,t9,t10,t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,T2,T3,T4,T5,T6,T7,R,T9,T10,T11,T12> transform8(Function<? super T8,? extends R> function) {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,t7,function.apply(t8),t9,t10,t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,R,T10,T11,T12> transform9(Function<? super T9,? extends R> function) {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,t7,t8,function.apply(t9),t10,t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,R,T11,T12> transform10(Function<? super T10,? extends R> function) {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,function.apply(t10),t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R,T12> transform11(Function<? super T11,? extends R> function) {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,function.apply(t11),t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R> transform12(Function<? super T12,? extends R> function) {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,function.apply(t12));
    }
}
