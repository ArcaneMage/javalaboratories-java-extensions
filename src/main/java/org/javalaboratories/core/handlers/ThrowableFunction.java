package org.javalaboratories.core.handlers;

@FunctionalInterface
public interface ThrowableFunction<T,R,E extends Throwable> {

    R apply(T t) throws E;
}
