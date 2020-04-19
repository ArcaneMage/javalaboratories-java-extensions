package com.excelsior.core.tuple;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Tuple {

    static <T1> Tuple1 of(T1 t1) {
        return new Tuple1<>(t1);
    }

    static <T1,T2> Tuple2 of(T1 t1, T2 t2) {
        return new Tuple2<>(t1,t2);
    }

    static <T1,T2,T3> Tuple3 of(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>(t1,t2,t3);
    }

    static <T1,T2,T3,T4> Tuple4 of(T1 t1, T2 t2, T3 t3, T4 t4) {
        return new Tuple4<>(t1,t2,t3,t4);
    }

    static <T1,T2,T3,T4,T5> Tuple5 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return new Tuple5<>(t1,t2,t3,t4,t5);
    }

    static <T1,T2,T3,T4,T5,T6> Tuple6 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        return new Tuple6<>(t1,t2,t3,t4,t5,t6);
    }

    static <T1,T2,T3,T4,T5,T6,T7> Tuple7 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
        return new Tuple7<>(t1,t2,t3,t4,t5,t6,t7);
    }

    static <T1,T2,T3,T4,T5,T6,T7,T8> Tuple8 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8) {
        return new Tuple8<>(t1,t2,t3,t4,t5,t6,t7,t8);
    }

    static <T1,T2,T3,T4,T5,T6,T7,T8,T9> Tuple9 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) {
        return new Tuple9<>(t1,t2,t3,t4,t5,t6,t7,t8,t9);
    }

    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10> Tuple10 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10) {
        return new Tuple10<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10);
    }

    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> Tuple11 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return new Tuple11<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);
    }

    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12> Tuple12 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12) {
        return new Tuple12<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12);
    }

    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13> Tuple13 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13) {
        return new Tuple13<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13);
    }

    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14> Tuple14 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14) {
        return new Tuple14<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14);
    }

    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15> Tuple15 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15) {
        return new Tuple15<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15);
    }

    static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11,T12,T13,T14,T15,T16> Tuple16 of(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11, T12 t12, T13 t13, T14 t14, T15 t15, T16 t16) {
        return new Tuple16<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11,t12,t13,t14,t15,t16);
    }

    int depth();

    int indexOf(Object object);

    Object[] toArray();

    <K> Map<K, ?> toMap(Function<? super Integer, ? extends K> keyMapper);

    List<?> toList();

}