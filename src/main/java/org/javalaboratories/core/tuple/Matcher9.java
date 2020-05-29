package org.javalaboratories.core.tuple;

import java.util.EnumSet;
import java.util.Set;

import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherProperties.MATCH_ALL;
import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherProperties.MATCH_ANY;

public final class Matcher9<T1,T2,T3,T4,T5,T6,T7,T8,T9> extends AbstractMatcher {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;
    private final T5 t5;
    private final T6 t6;
    private final T7 t7;
    private final T8 t8;
    private final T9 t9;

    public static <T1,T2,T3,T4,T5,T6,T7,T8,T9> Matcher9<T1,T2,T3,T4,T5,T6,T7,T8,T9> all(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) { return new Matcher9<>(t1,t2,t3,t4,t5,t6,t7,t8,t9); }
    public static <T1,T2,T3,T4,T5,T6,T7,T8,T9> Matcher9<T1,T2,T3,T4,T5,T6,T7,T8,T9> any(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) { return new Matcher9<>(EnumSet.of(MATCH_ANY),t1,t2,t3,t4,t5,t6,t7,t8,t9); }

    private Matcher9(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) {
        this(EnumSet.of(MATCH_ALL),t1,t2,t3,t4,t5,t6,t7,t8,t9);
    }

    private Matcher9(Set<MatcherProperties> properties, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9) {
        super(properties,t1,t2,t3,t4,t5,t6,t7,t8,t9);
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
}
