package com.excelsior.core.tuple;


import com.excelsior.core.Nullable;

import java.util.function.Function;

public final class Tuple3<T1,T2,T3> extends AbstractTuple {
    private T1 t1;
    private T2 t2;
    private T3 t3;

    public Tuple3(T1 t1, T2 t2, T3 t3) {
        super(t1,t2,t3);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public static <T> Nullable<Tuple3<T,T,T>> toTuple(Iterable<T> iterable) {
        @SuppressWarnings("unchecked")
        Nullable<Tuple3<T,T,T>> result = (Nullable<Tuple3<T,T,T>>) Tuple.toTuple(3,iterable);
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

    public <T4> Tuple4<T1,T2,T3,T4> join(Tuple1<T4> tuple) {
        return new Tuple4<>(t1,t2,t3,tuple.value1());
    }

    public <T4,T5> Tuple5<T1,T2,T3,T4,T5> join(Tuple2<T4,T5> tuple) {
        return new Tuple5<>(t1,t2,t3,tuple.value1(),tuple.value2());
    }

    public <T4,T5,T6> Tuple6<T1,T2,T3,T4,T5,T6> join(Tuple3<T4,T5,T6> tuple) {
        return new Tuple6<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3());
    }

    public <T4,T5,T6,T7> Tuple7<T1,T2,T3,T4,T5,T6,T7> join(Tuple4<T4,T5,T6,T7> tuple) {
        return new Tuple7<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4());
    }

    public <T4,T5,T6,T7,T8> Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> join(Tuple5<T4,T5,T6,T7,T8> tuple) {
        return new Tuple8<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(), tuple.value5());
    }

    public <T4,T5,T6,T7,T8,T9> Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> join(Tuple6<T4,T5,T6,T7,T8,T9> tuple) {
        return new Tuple9<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6());
    }

    public <T4,T5,T6,T7,T8,T9,T10> Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> join(Tuple7<T4,T5,T6,T7,T8,T9,T10> tuple) {
        return new Tuple10<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7());
    }

    public <T4,T5,T6,T7,T8,T9,T10,T11> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> join(Tuple8<T4,T5,T6,T7,T8,T9,T10,T11> tuple) {
        return new Tuple11<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8());
    }

    public <T4,T5,T6,T7,T8,T9,T10,T11,T12> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> join(Tuple9<T4,T5,T6,T7,T8,T9,T10,T11,T12> tuple) {
        return new Tuple12<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9());
    }

    public <T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> join(Tuple10<T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> tuple) {
        return new Tuple13<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9(),tuple.value10());
    }

    public <T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> join(Tuple11<T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> tuple) {
        return new Tuple14<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9(),tuple.value10(),tuple.value11());
    }

    public <T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> join(Tuple12<T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> tuple) {
        return new Tuple15<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9(),tuple.value10(),tuple.value11(),tuple.value12());
    }

    public <T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> join(Tuple13<T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> tuple) {
        return new Tuple16<>(t1,t2,t3,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9(),tuple.value10(),tuple.value11(),tuple.value12(),tuple.value13());
    }

    public Tuple1<T1> truncate1() {
        return new Tuple1<>(t1);
    }

    public Tuple2<T1,T2> truncate2() {
        return new Tuple2<>(t1,t2);
    }

    public <R> Tuple3<R,T2,T3> transform1(Function<? super T1,? extends R> function) {
        return new Tuple3<>(function.apply(t1),t2,t3);
    }

    public <R> Tuple3<T1,R,T3> transform2(Function<? super T2,? extends R> function) {
        return new Tuple3<>(t1,function.apply(t2),t3);
    }

    public <R> Tuple3<T1,T2,R> transform3(Function<? super T3,? extends R> function) {
        return new Tuple3<>(t1,t2,function.apply(t3));
    }
}
