package org.javalaboratories.core.handlers;

@FunctionalInterface
public interface ThrowableConsumer<T, E extends Throwable> {

    void accept(T t) throws E;
}
