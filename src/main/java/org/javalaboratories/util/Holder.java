package org.javalaboratories.util;

/**
 * This is a container object that holds a value.
 * <p>
 * Generally used in lambda expressions to mutate objects declared as effectively final.
 * <p>
 * @param <T> type writableHolder variable to hold.
 */
public interface Holder<T> {

    T get();

    default void set(final T value) {
        throw new UnsupportedOperationException();
    }
}
