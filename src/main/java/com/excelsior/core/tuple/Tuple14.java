package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;

import java.util.function.Function;

public final class Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> extends AbstractTuple {
    private T1 t1;
    private T2 t2;
    private T3 t3;
    private T4 t4;
    private T5 t5;
    private T6 t6;
    private T7 t7;
    private T8 t8;
    private T9 t9;
    private T10 t10;
    private T11 t11;
    private T12 t12;
    private T13 t13;
    private T14 t14;

    public Tuple14(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14) {
        super(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14);
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
        this.t14 = t14;
    }
    
    public static <T> Nullable<Tuple14<T,T,T,T,T,T,T,T,T,T,T,T,T,T>> toTuple(Iterable<T> iterable) {
        @SuppressWarnings("unchecked")
        Nullable<Tuple14<T,T,T,T,T,T,T,T,T,T,T,T,T,T>> result = (Nullable<Tuple14<T,T,T,T,T,T,T,T,T,T,T,T,T,T>>) Tuple.toTuple(14,iterable);
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

    public T14 value14() { return t14; }

    public <T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> join(Tuple1<T15> tuple) {
        return new Tuple15<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,tuple.value1());
    }

    public <T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> join(Tuple2<T15,T16> tuple) {
        return new Tuple16<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,tuple.value1(),tuple.value2());
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

    public Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> truncate9() {
        return new Tuple9<>(t1,t2,t3,t4,t5,t6,t7,t8,t9);
    }

    public Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> truncate10() {
        return new Tuple10<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10);
    }

    public Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> truncate11() {
        return new Tuple11<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);
    }

    public Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> truncate12() {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12);
    }

    public Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> truncate13() {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13);
    }

    public <R> Tuple14<R,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> transform1(Function<? super T1,? extends R> function) {
        return new Tuple14<>(function.apply(t1),t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14);
    }

    public <R> Tuple14<T1,R,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> transform2(Function<? super T2,? extends R> function) {
        return new Tuple14<>(t1,function.apply(t2),t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14);
    }

    public <R> Tuple14<T1,T2,R,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> transform3(Function<? super T3,? extends R> function) {
        return new Tuple14<>(t1,t2,function.apply(t3),t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14);
    }
    public <R> Tuple14<T1,T2,T3,R,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> transform4(Function<? super T4,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,function.apply(t4),t5,t6,t7,t8,t9,t10,t11,t12,t13,t14);
    }

    public <R> Tuple14<T1,T2,T3,T4,R,T6,T7,T8,T9,T10,T11,T12,T13,T14> transform5(Function<? super T5,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,t4,function.apply(t5),t6,t7,t8,t9,t10,t11,t12,t13,t14);
    }

    public <R> Tuple14<T1,T2,T3,T4,T5,R,T7,T8,T9,T10,T11,T12,T13,T14> transform6(Function<? super T6,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,t4,t5,function.apply(t6),t7,t8,t9,t10,t11,t12,t13,t14);
    }

    public <R> Tuple14<T1,T2,T3,T4,T5,T6,R,T8,T9,T10,T11,T12,T13,T14> transform7(Function<? super T7,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,function.apply(t7),t8,t9,t10,t11,t12,t13,t14);
    }

    public <R> Tuple14<T1,T2,T3,T4,T5,T6,T7,R,T9,T10,T11,T12,T13,T14> transform8(Function<? super T8,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,function.apply(t8),t9,t10,t11,t12,t13,t14);
    }

    public <R> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,R,T10,T11,T12,T13,T14> transform9(Function<? super T9,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,function.apply(t9),t10,t11,t12,t13,t14);
    }

    public <R> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,R,T11,T12,T13,T14> transform10(Function<? super T10,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,function.apply(t10),t11,t12,t13,t14);
    }

    public <R> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,R,T12,T13,T14> transform11(Function<? super T11,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,function.apply(t11),t12,t13,t14);
    }

    public <R> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,R,T13,T14> transform12(Function<? super T12,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,function.apply(t12),t13,t14);
    }

    public <R> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,R,T14> transform13(Function<? super T13,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,function.apply(t13),t14);
    }

    public <R> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,R> transform14(Function<? super T14,? extends R> function) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,function.apply(t14));
    }
}
