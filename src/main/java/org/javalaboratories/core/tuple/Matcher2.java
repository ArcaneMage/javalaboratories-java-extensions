package org.javalaboratories.core.tuple;

import java.util.EnumSet;
import java.util.Set;

import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherProperties.MATCH_ALL;
import static org.javalaboratories.core.tuple.AbstractMatcher.MatcherProperties.MATCH_ANY;

public final class Matcher2<T1,T2> extends AbstractMatcher {
    private final T1 t1;
    private final T2 t2;

    public static <T1,T2> Matcher2<T1,T2> all(T1 t1, T2 t2) { return new Matcher2<>(t1,t2); }
    public static <T1,T2> Matcher2<T1,T2> any(T1 t1, T2 t2) { return new Matcher2<>(EnumSet.of(MATCH_ANY),t1,t2); }

    private Matcher2(T1 t1, T2 t2) {
        this(EnumSet.of(MATCH_ALL),t1,t2);
    }

    private Matcher2(Set<MatcherProperties> properties, T1 t1, T2 t2) {
        super(properties,t1,t2);
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
