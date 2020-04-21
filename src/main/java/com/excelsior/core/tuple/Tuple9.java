package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;

import java.util.function.Function;

public final class Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> extends AbstractTuple {
    private T1 t1;
    private T2 t2;
    private T3 t3;
    private T4 t4;
    private T5 t5;
    private T6 t6;
    private T7 t7;
    private T8 t8;
    private T9 t9;

    public Tuple9(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) {
        super(t1,t2,t3,t4,t5,t6,t7,t8,t9);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
        this.t7 = t7;
        this.t8 = t8;
        this.t9 = t9;
    }

    public static <T> Nullable<Tuple9<T,T,T,T,T,T,T,T,T>> toTuple(Iterable<T> iterable) {
        @SuppressWarnings("unchecked")
        Nullable<Tuple9<T,T,T,T,T,T,T,T,T>> result = (Nullable<Tuple9<T,T,T,T,T,T,T,T,T>>) Tuple.toTuple(9,iterable);
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

    public <T10> Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> join(Tuple1<T10> tuple) {
        return new Tuple10<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,tuple.value1());
    }

    public <T10,T11> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> join(Tuple2<T10,T11> tuple) {
        return new Tuple11<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,tuple.value1(),tuple.value2());
    }

    public <T10,T11,T12> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> join(Tuple3<T10,T11,T12> tuple) {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,tuple.value1(),tuple.value2(),tuple.value3());
    }

    public <T10,T11,T12,T13> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> join(Tuple4<T10,T11,T12,T13> tuple) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4());
    }

    public <T10,T11,T12,T13,T14> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> join(Tuple5<T10,T11,T12,T13,T14> tuple) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5());
    }

    public <T10,T11,T12,T13,T14,T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> join(Tuple6<T10,T11,T12,T13,T14,T15> tuple) {
        return new Tuple15<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6());
    }

    public <T10,T11,T12,T13,T14,T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> join(Tuple7<T10,T11,T12,T13,T14,T15,T16> tuple) {
        return new Tuple16<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7());
    }

    public Tuple1<T1> truncate1() {
        return new Tuple1<>(t1);
    }

    public Tuple2<T1,T2> truncate2() {
        return new Tuple2<>(t1,t2);
    }

    public Tuple3<T1,T2,T3> truncate3() {
        return new Tuple3<>(t1,t2,t3);
    }

    public Tuple4<T1,T2,T3,T4> truncate4() {
        return new Tuple4<>(t1,t2,t3,t4);
    }

    public Tuple5<T1,T2,T3,T4,T5> truncate5() {
        return new Tuple5<>(t1,t2,t3,t4,t5);
    }

    public Tuple6<T1,T2,T3,T4,T5,T6> truncate6() {
        return new Tuple6<>(t1,t2,t3,t4,t5,t6);
    }

    public Tuple7<T1,T2,T3,T4,T5,T6,T7> truncate7() {
        return new Tuple7<>(t1,t2,t3,t4,t5,t6,t7);
    }

    public Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> truncate8() {
        return new Tuple8<>(t1,t2,t3,t4,t5,t6,t7,t8);
    }

    public <R> Tuple9<R,T2,T3,T4,T5,T6,T7,T8,T9> transform1(Function<? super T1,? extends R> function) {
        return new Tuple9<>(function.apply(t1),t2,t3,t4,t5,t6,t7,t8,t9);
    }

    public <R> Tuple9<T1,R,T3,T4,T5,T6,T7,T8,T9> transform2(Function<? super T2,? extends R> function) {
        return new Tuple9<>(t1,function.apply(t2),t3,t4,t5,t6,t7,t8,t9);
    }

    public <R> Tuple9<T1,T2,R,T4,T5,T6,T7,T8,T9> transform3(Function<? super T3,? extends R> function) {
        return new Tuple9<>(t1,t2,function.apply(t3),t4,t5,t6,t7,t8,t9);
    }
    public <R> Tuple9<T1,T2,T3,R,T5,T6,T7,T8,T9> transform4(Function<? super T4,? extends R> function) {
        return new Tuple9<>(t1,t2,t3,function.apply(t4),t5,t6,t7,t8,t9);
    }

    public <R> Tuple9<T1,T2,T3,T4,R,T6,T7,T8,T9> transform5(Function<? super T5,? extends R> function) {
        return new Tuple9<>(t1,t2,t3,t4,function.apply(t5),t6,t7,t8,t9);
    }

    public <R> Tuple9<T1,T2,T3,T4,T5,R,T7,T8,T9> transform6(Function<? super T6,? extends R> function) {
        return new Tuple9<>(t1,t2,t3,t4,t5,function.apply(t6),t7,t8,t9);
    }

    public <R> Tuple9<T1,T2,T3,T4,T5,T6,R,T8,T9> transform7(Function<? super T7,? extends R> function) {
        return new Tuple9<>(t1,t2,t3,t4,t5,t6,function.apply(t7),t8,t9);
    }

    public <R> Tuple9<T1,T2,T3,T4,T5,T6,T7,R,T9> transform8(Function<? super T8,? extends R> function) {
        return new Tuple9<>(t1,t2,t3,t4,t5,t6,t7,function.apply(t8),t9);
    }

    public <R> Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,R> transform9(Function<? super T9,? extends R> function) {
        return new Tuple9<>(t1,t2,t3,t4,t5,t6,t7,t8,function.apply(t9));
    }
}
