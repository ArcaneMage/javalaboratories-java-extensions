package org.javalaboratories.core.tuple;

import static org.javalaboratories.core.tuple.Matcher.MatcherStrategies.MATCH_ALL;
import static org.javalaboratories.core.tuple.Matcher.MatcherStrategies.MATCH_SET;

public final class Matcher1<T1> extends AbstractMatcher {
    private final T1 t1;

    public static <T1> Matcher1<T1> all(T1 t1) { return new Matcher1<>(t1); }
    public static <T1> Matcher1<T1> set(T1 t1) { return new Matcher1<>(MATCH_SET,t1); }

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
