package org.javalaboratories.core.tuple;

import static org.javalaboratories.core.tuple.Matcher.Strategy.*;

public final class Matcher3<T1,T2,T3> extends AbstractMatcher {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;

    public static <T1,T2,T3> Matcher3<T1,T2,T3> allOf(T1 t1, T2 t2, T3 t3) { return new Matcher3<>(t1,t2,t3); }
    public static <T1,T2,T3> Matcher3<T1,T2,T3> anyOf(T1 t1, T2 t2, T3 t3) { return new Matcher3<>(MATCH_ANY,t1,t2,t3); }
    public static <T1,T2,T3> Matcher3<T1,T2,T3> setOf(T1 t1, T2 t2, T3 t3) { return new Matcher3<>(MATCH_SET,t1,t2,t3); }

    private Matcher3(T1 t1, T2 t2, T3 t3) {
        this(MATCH_ALL,t1,t2,t3);
    }

    private Matcher3(Strategy strategy, T1 t1, T2 t2, T3 t3) {
        super(strategy,t1,t2,t3);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
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
}
