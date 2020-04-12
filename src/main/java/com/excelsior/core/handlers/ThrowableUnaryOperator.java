package com.excelsior.core.handlers;

@FunctionalInterface
public interface ThrowableUnaryOperator<T,E extends Throwable> extends ThrowableFunction<T,T,E> {

}
