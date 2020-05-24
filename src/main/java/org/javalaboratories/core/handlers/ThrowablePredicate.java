package org.javalaboratories.core.handlers;

@FunctionalInterface
public interface ThrowablePredicate<T,E extends Throwable> {

    boolean test(T t) throws E;
}
