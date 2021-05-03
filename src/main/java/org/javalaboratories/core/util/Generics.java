package org.javalaboratories.util;

import java.util.Objects;

/**
 * Utility class for generics.
 * <p>
 * Introduces methods to handle unchecked casting of objects. It is recommended
 * to ensure that all type-checking cases are carefully considered before using
 * these methods.
 */
public final class Generics {
    public static <T> T unchecked(Object object) {
        Objects.requireNonNull(object, "No object?");
        @SuppressWarnings("unchecked")
        T result = (T) object;
        return result;
    }

    public static <T> T unchecked(Object object, Class<T> clazz) {
        Objects.requireNonNull(object, "No object?");
        Objects.requireNonNull(clazz, "Class type expected?");

        return clazz.cast(object);
    }
}
