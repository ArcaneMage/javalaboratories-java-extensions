package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;

import java.util.function.Function;

/**
 * A tuple with depth of 13
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
 * @param <T13> type of 13th element
 *
 * @author Kevin Henry
 */
public final class Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> extends AbstractTuple {
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
    private final T13 t13;

    public Tuple13(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        super(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13);
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
        this.t13 = t13;
    }

    /**
     * Converts iterable into a tuple, if possible.
     * <p>
     * Creates a tuple to a depth of 13 from iterable object. If there is
     * insufficient elements, then {@link Nullable} will be empty.
     *
     * @param iterable Iterable object
     * @param <T> iterable type.
     * @return A tuple in {@link Nullable} object container.
     */
    public static <T> Nullable<Tuple13<T,T,T,T,T,T,T,T,T,T,T,T,T>> fromIterable(Iterable<T> iterable) {
        @SuppressWarnings("unchecked")
        Nullable<Tuple13<T,T,T,T,T,T,T,T,T,T,T,T,T>> result = (Nullable<Tuple13<T,T,T,T,T,T,T,T,T,T,T,T,T>>) Tuple.fromIterable(iterable, 13);
        return result;
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

    public T13 value13() { return t13; }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T14> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> join(Tuple1<T14> tuple) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,tuple.value1());
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T14,T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> join(Tuple2<T14,T15> tuple) {
        return new Tuple15<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,tuple.value1(),tuple.value2());
    }

    /**
     * Joins a tuple to this tuple.
     * @param tuple a tuple object.
     */
    public <T14,T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> join(Tuple3<T14,T15,T16> tuple) {
        return new Tuple16<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,tuple.value1(),tuple.value2(),tuple.value3());
    }

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
     * Truncates remaining tuples to a depth of 12
     */
    public Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> truncate12() {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<R,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> transform1(Function<? super T1,? extends R> function) {
        return new Tuple13<>(function.apply(t1),t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,R,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> transform2(Function<? super T2,? extends R> function) {
        return new Tuple13<>(t1,function.apply(t2),t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,R,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> transform3(Function<? super T3,? extends R> function) {
        return new Tuple13<>(t1,t2,function.apply(t3),t4,t5,t6,t7,t8,t9,t10,t11,t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,T3,R,T5,T6,T7,T8,T9,T10,T11,T12,T13> transform4(Function<? super T4,? extends R> function) {
        return new Tuple13<>(t1,t2,t3,function.apply(t4),t5,t6,t7,t8,t9,t10,t11,t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,T3,T4,R,T6,T7,T8,T9,T10,T11,T12,T13> transform5(Function<? super T5,? extends R> function) {
        return new Tuple13<>(t1,t2,t3,t4,function.apply(t5),t6,t7,t8,t9,t10,t11,t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,T3,T4,T5,R,T7,T8,T9,T10,T11,T12,T13> transform6(Function<? super T6,? extends R> function) {
        return new Tuple13<>(t1,t2,t3,t4,t5,function.apply(t6),t7,t8,t9,t10,t11,t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,T3,T4,T5,T6,R,T8,T9,T10,T11,T12,T13> transform7(Function<? super T7,? extends R> function) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,function.apply(t7),t8,t9,t10,t11,t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,T3,T4,T5,T6,T7,R,T9,T10,T11,T12,T13> transform8(Function<? super T8,? extends R> function) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,function.apply(t8),t9,t10,t11,t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,R,T10,T11,T12,T13> transform9(Function<? super T9,? extends R> function) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,function.apply(t9),t10,t11,t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,R,T11,T12,T13> transform10(Function<? super T10,? extends R> function) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,function.apply(t10),t11,t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R,T12,T13> transform11(Function<? super T11,? extends R> function) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,function.apply(t11),t12,t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R,T13> transform12(Function<? super T12,? extends R> function) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,function.apply(t12),t13);
    }

    /**
     * Transform an element in this tuple into another object.
     * @param function function that performs the transformation
     * @param <R> return type of transformed element
     * @return a tuple with transformed element.
     */
    public <R> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R> transform13(Function<? super T13,? extends R> function) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,function.apply(t13));
    }
}
