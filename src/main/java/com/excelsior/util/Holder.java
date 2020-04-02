package com.excelsior.util;

import java.io.Serializable;
import java.util.Objects;

/**
 * This is a container object that holds a value.
 * <p>
 * Generally used in lambda expressions to mutate objects declared as effectively final.
 * @param <T> type of variable to hold.
 */
public final class Holder<T> implements Serializable {
    private T value;

    public Holder() {}

    public Holder(final T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Holder<?> holder = (Holder<?>) o;
        return Objects.equals(value, holder.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Holder[" +
                "value=" + value +
                ']';
    }
}
