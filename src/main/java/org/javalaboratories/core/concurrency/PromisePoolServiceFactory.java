package org.javalaboratories.core.concurrency;

@FunctionalInterface
public interface PromisePoolServiceFactory {
    PromisePoolService newPoolService(int capacity);
}
