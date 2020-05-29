package org.javalaboratories.core.tuple;

import java.util.EnumSet;
import java.util.Set;

import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherProperties.MATCH_ALL;
import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherProperties.MATCH_ANY;

public final class Matcher5<T1,T2,T3,T4,T5> extends AbstractMatcher {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;
    private final T5 t5;

    public static <T1,T2,T3,T4,T5> Matcher5<T1,T2,T3,T4,T5> all(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) { return new Matcher5<>(t1,t2,t3,t4,t5); }
    public static <T1,T2,T3,T4,T5> Matcher5<T1,T2,T3,T4,T5> any(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) { return new Matcher5<>(EnumSet.of(MATCH_ANY),t1,t2,t3,t4,t5); }

    private Matcher5(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        this(EnumSet.of(MATCH_ALL),t1,t2,t3,t4,t5);
    }

    private Matcher5(Set<MatcherProperties> properties, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        super(properties,t1,t2,t3,t4,t5);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
        this.t5 = t5;
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
}
