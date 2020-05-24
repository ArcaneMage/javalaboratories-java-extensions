package org.javalaboratories.core.tuple;

public final class Matcher4<T1,T2,T3,T4> extends AbstractMatcher {
    private final T1 t1;
    private final T2 t2;
    private final T3 t3;
    private final T4 t4;

    Matcher4(T1 t1, T2 t2, T3 t3, T4 t4) {
        super(t1,t2,t3,t4);
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
        this.t4 = t4;
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
}
