package com.excelsior.core.handlers;

@FunctionalInterface
public interface ThrowablePredicate<T,E extends Throwable> {

    boolean test(T t) throws E;
}
