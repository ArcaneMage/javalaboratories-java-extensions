package org.javalaboratories.core.tuple;

public final class Matcher2<T1,T2> extends AbstractMatcher {
    private final T1 t1;
    private final T2 t2;

    Matcher2(T1 t1, T2 t2) {
        super(t1,t2);
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
