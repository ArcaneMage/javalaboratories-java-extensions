package org.javalaboratories.core.handlers;

@FunctionalInterface
public interface ThrowableBiPredicate<T,U,E extends Throwable> {

    boolean test(T t, U v) throws E;
}
