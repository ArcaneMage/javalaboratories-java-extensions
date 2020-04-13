package com.excelsior.core.handlers;

@FunctionalInterface
public interface ThrowableRunnable<E extends Throwable> {

   void run() throws E;
}
