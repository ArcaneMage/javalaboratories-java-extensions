package org.javalaboratories.core.tuple;

import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherStrategies.MATCH_ALL;
import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherStrategies.MATCH_ANY;

public final class Matcher6<T1,T2,T3,T4,T5,T6> extends AbstractMatcher {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;
    private final T5 t5;
    private final T6 t6;

    public static <T1,T2,T3,T4,T5,T6> Matcher6<T1,T2,T3,T4,T5,T6> all(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) { return new Matcher6<>(t1,t2,t3,t4,t5,t6); }
    public static <T1,T2,T3,T4,T5,T6> Matcher6<T1,T2,T3,T4,T5,T6> any(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) { return new Matcher6<>(MATCH_ANY,t1,t2,t3,t4,t5,t6); }

    private Matcher6(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        this(MATCH_ALL,t1,t2,t3,t4,t5,t6);
    }

    private Matcher6(MatcherStrategies strategy, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        super(strategy,t1,t2,t3,t4,t5,t6);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
        this.t6 = t6;
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
}
