package org.javalaboratories.core.handlers;

@FunctionalInterface
public interface ThrowableRunnable<E extends Throwable> {

   void run() throws E;
}
