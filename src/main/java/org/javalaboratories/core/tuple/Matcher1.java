package org.javalaboratories.core.tuple;

public final class Matcher1<T1> extends AbstractMatcher {
    private final T1 t1;

    Matcher1(T1 t1) {
        super(t1);
        this.t1 = t1;
    }

    public T1 value1() {
        return t1;
    }
}
