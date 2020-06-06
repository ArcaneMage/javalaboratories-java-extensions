package org.javalaboratories.core.tuple;

import static org.javalaboratories.core.tuple.Matcher.Strategy.*;

public final class Matcher2<T1,T2> extends AbstractMatcher {
    private final T1 t1;
    private final T2 t2;

    public static <T1,T2> Matcher2<T1,T2> allOf(T1 t1, T2 t2) { return new Matcher2<>(t1,t2); }
    public static <T1,T2> Matcher2<T1,T2> anyOf(T1 t1, T2 t2) { return new Matcher2<>(MATCH_ANY,t1,t2); }
    public static <T1,T2> Matcher2<T1,T2> setOf(T1 t1, T2 t2) { return new Matcher2<>(MATCH_SET,t1,t2); }

    private Matcher2(T1 t1, T2 t2) {
        this(MATCH_ALL,t1,t2);
    }

    private Matcher2(Strategy strategy, T1 t1, T2 t2) {
        super(strategy,t1,t2);
        this.t1 = t1;
        this.t2 = t2;
    }
    
    public T1 value1() {
        return t1;
    }
    public T2 value2() {
        return t2;
    }
}
