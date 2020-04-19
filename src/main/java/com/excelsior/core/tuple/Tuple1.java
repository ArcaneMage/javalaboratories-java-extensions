package com.excelsior.core.tuple;

public final class Tuple1<T1> extends TupleContainer implements Tuple  {
    private final T1 t1;

    public Tuple1(T1 t1) {
        super(t1);
        this.t1 = t1;
    }

    public T1 value1() {
        return t1;
    }

    public <T2> Tuple2<T1,T2> join(Tuple1<T2> tuple) {
        return new Tuple2<>(t1,tuple.value1());
    }

    public <T2,T3> Tuple3<T1,T2,T3> join(Tuple2<T2,T3> tuple) {
        return new Tuple3<>(t1,tuple.value1(),tuple.value2());
    }

    public <T2,T3,T4> Tuple4<T1,T2,T3,T4> join(Tuple3<T2,T3,T4> tuple) {
        return new Tuple4<>(t1,tuple.value1(),tuple.value2(),tuple.value3());
    }

    public <T2,T3,T4,T5> Tuple5<T1,T2,T3,T4,T5> join(Tuple4<T2,T3,T4,T5> tuple) {
        return new Tuple5<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4());
    }

    public <T2,T3,T4,T5,T6> Tuple6<T1,T2,T3,T4,T5,T6> join(Tuple5<T2,T3,T4,T5,T6> tuple) {
        return new Tuple6<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5());
    }

    public <T2,T3,T4,T5,T6,T7> Tuple7<T1,T2,T3,T4,T5,T6,T7> join(Tuple6<T2,T3,T4,T5,T6,T7> tuple) {
        return new Tuple7<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6());
    }

    public <T2,T3,T4,T5,T6,T7,T8> Tuple8<T1,T2,T3,T4,T5,T6,T7,T8> join(Tuple7<T2,T3,T4,T5,T6,T7,T8> tuple) {
        return new Tuple8<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7());
    }

    public <T2,T3,T4,T5,T6,T7,T8,T9> Tuple9<T1,T2,T3,T4,T5,T6,T7,T8,T9> join(Tuple8<T2,T3,T4,T5,T6,T7,T8,T9> tuple) {
        return new Tuple9<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8());
    }

    public <T2,T3,T4,T5,T6,T7,T8,T9,T10> Tuple10<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> join(Tuple9<T2,T3,T4,T5,T6,T7,T8,T9,T10> tuple) {
        return new Tuple10<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9());
    }

    public <T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> Tuple11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> join(Tuple10<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> tuple) {
        return new Tuple11<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9(),tuple.value10());
    }

    public <T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> Tuple12<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> join(Tuple11<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> tuple) {
        return new Tuple12<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9(),tuple.value10(),tuple.value11());
    }

    public <T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> Tuple13<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> join(Tuple12<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> tuple) {
        return new Tuple13<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9(),tuple.value10(),tuple.value11(),tuple.value12());
    }

    public <T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> Tuple14<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> join(Tuple13<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> tuple) {
        return new Tuple14<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9(),tuple.value10(),tuple.value11(),tuple.value12(),tuple.value13());
    }

    public <T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> Tuple15<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> join(Tuple14<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> tuple) {
        return new Tuple15<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9(),tuple.value10(),tuple.value11(),tuple.value12(),tuple.value13(),tuple.value14());
    }

    public <T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> Tuple16<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> join(Tuple15<T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> tuple) {
        return new Tuple16<>(t1,tuple.value1(),tuple.value2(),tuple.value3(),tuple.value4(),tuple.value5(),tuple.value6(),tuple.value7(),tuple.value8(),tuple.value9(),tuple.value10(),tuple.value11(),tuple.value12(),tuple.value13(),tuple.value14(),tuple.value15());
    }
}

