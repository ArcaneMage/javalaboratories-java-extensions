package org.javalaboratories.core.tuple;

import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherStrategies.MATCH_ALL;

public final class Matcher1<T1> extends AbstractMatcher {
    private final T1 t1;

    public static <T1> Matcher1<T1> all(T1 t1) { return new Matcher1<>(t1); }

    private Matcher1(T1 t1) {
        this(MATCH_ALL,t1);
    }

    private Matcher1(MatcherStrategies strategy, T1 t1) {
        super(strategy,t1);
        this.t1 = t1;
    }

    public T1 value1() {
        return t1;
    }
}
