package org.javalaboratories.core.tuple;

import static org.javalaboratories.core.tuple.Matcher.MatcherStrategies.*;

public final class Matcher11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> extends AbstractMatcher {
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

    public static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> Matcher11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> allOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) { return new Matcher11<>(t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11); }
    public static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> Matcher11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> anyOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) { return new Matcher11<>(MATCH_ANY,t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11); }
    public static <T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> Matcher11<T1,T2,T3,T4,T5,T6,T7,T8,T9,T10,T11> setOf(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) { return new Matcher11<>(MATCH_SET,t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11); }

    private Matcher11(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        this(MATCH_ALL,t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);
    }

    private Matcher11(MatcherStrategies strategy, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        super(strategy,t1,t2,t3,t4,t5,t6,t7,t8,t9,t10,t11);
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

    public T9 value9() {
        return t9;
    }

    public T10 value10() {
        return t10;
    }

    public T11 value11() {
        return t11;
    }
}
