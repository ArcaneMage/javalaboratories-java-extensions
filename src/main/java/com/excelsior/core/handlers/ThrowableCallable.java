package com.excelsior.core.handlers;

@FunctionalInterface
public interface ThrowableCallable<T,E extends Throwable> {

    T call() throws E;
}
