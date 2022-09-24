package org.javalaboratories.core.handlers;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.*;

/**
 * Handlers class provides a broad set of wrapper methods to handle checked
 * exceptions within lambda expressions.
 * <p>
 * Lambdas are generally short and concise, but checked exceptions can
 * sometimes cause the lambda expression to look unwieldy. This class has many
 * useful methods that compliment common functional interfaces. Each method wraps the
 * function object into a function that transforms the checked exception to a
 * {@link RuntimeException} object.
 * <p>
 * For example, here is an example of a method performing file input/output:
 * <pre>
 *     {@code
 *      public void writeFile(String file) throws IOException {
 *          ...
 *      }
 *
 *      Consumer<String> consumer = s -> {
 *          try {
 *              writeFile(s)
 *          } catch (IOException e) {
 *              ...
 *          }
 *      }
 *
 *      ...becomes...
 *
 *      Consumer<String> consumer = Handlers.consumer(s -> writeFile(s));
 * }
 * </pre>
 */
@SuppressWarnings("WeakerAccess")
public final class Handlers {

    /**
     * Wraps consumer {@link ThrowableConsumer} into a {@link Consumer} function,
     * which handles checked exceptions.
     *
     * @param consumer function that throws a checked exception
     * @param <T> type of input into the operation.
     * @param <E> type of exception.
     * @return consumer object that handles the exception.
     */
    public static <T,E extends Throwable> Consumer<T> consumer(final ThrowableConsumer<T,E> consumer) {
        Objects.requireNonNull(consumer);
        return t -> {
            try {
                consumer.accept(t);
            } catch (Throwable throwable) {
                handle(throwable);
            }
        };
    }

    /**
     * Wraps biConsumer {@link ThrowableBiConsumer} into a {@link BiConsumer} function,
     * which handles checked exceptions.
     *
     * @param biConsumer function that throws a checked exception.
     * @param <T> type of input into the operation.
     * @param <U> type of input into the operation.
     * @param <E> type of exception.
     * @return consumer object that handles the exception.
     */
    public static <T,U,E extends Throwable> BiConsumer<T,U> biConsumer(final ThrowableBiConsumer<T,U,E> biConsumer) {
        Objects.requireNonNull(biConsumer);
        return (t,u) -> {
            try {
                biConsumer.accept(t,u);
            } catch (Throwable throwable) {
                handle(throwable);
            }
        };
    }

    /**
     * Wraps function {@link ThrowableFunction} into a {@link Function} function,
     * which handles checked exceptions.
     *
     * @param function function that throws a checked exception.
     * @param <T> type of input into the operation
     * @param <R> type of result from the operation
     * @param <E> type of exception.
     * @return function object that handles the exception
     */
    public static <T,R,E extends Throwable> Function<T,R> function(final ThrowableFunction<T,R,E> function) {
        Objects.requireNonNull(function);
        return t -> {
            R result = null;
            try {
                result = function.apply(t);
            } catch (Throwable throwable) {
                handle(throwable);
            }
            return result;
        };
    }

    /**
     * Wraps function {@link ThrowableBiFunction} into a {@link BiFunction} function,
     * which handles checked exceptions.
     *
     * @param function function that throws a checked exception.
     * @param <T> type of input into the operation
     * @param <U> type of second input into the operation
     * @param <R> type of result from the operation
     * @param <E> type of exception.
     * @return function object that handles the exception
     */
    public static <T,U,R,E extends Throwable> BiFunction<T,U,R> biFunction(final ThrowableBiFunction<T,U,R,E> function) {
        Objects.requireNonNull(function);
        return (t,u) -> {
            R result = null;
            try {
                result = function.apply(t,u);
            } catch (Throwable throwable) {
                handle(throwable);
            }
            return result;
        };
    }

    /**
     * Wraps function {@link ThrowablePredicate} into a {@link Predicate} function,
     * which handles checked exceptions.
     *
     * @param predicate function that throws a checked exception.
     * @param <T> type of input into the operation
     * @param <E> type of exception.
     * @return predicate object that handles the exception
     */
    public static <T,E extends Throwable> Predicate<T> predicate(final ThrowablePredicate<T,E> predicate) {
        Objects.requireNonNull(predicate);
        return t -> {
            boolean result = false;
            try {
                result = predicate.test(t);
            } catch (Throwable throwable) {
                handle(throwable);
            }
            return result;
        };
    }

    /**
     * Wraps function {@link ThrowableBiPredicate} into a {@link BiPredicate} function,
     * which handles checked exceptions.
     *
     * @param predicate function that throws a checked exception.
     * @param <T> type of input into the operation
     * @param <U> type of second inout into the operation
     * @param <E> type of exception.
     * @return predicate object that handles the exception
     */
    public static <T,U,E extends Throwable> BiPredicate<T,U> biPredicate(final ThrowableBiPredicate<T,U,E> predicate) {
        Objects.requireNonNull(predicate);
        return (t,u) -> {
            boolean result = false;
            try {
                result = predicate.test(t,u);
            } catch (Throwable throwable) {
                handle(throwable);
            }
            return result;
        };
    }

    /**
     * Wraps unaryOperator {@link ThrowableUnaryOperator} into a {@link UnaryOperator} function,
     * which handles the checked exceptions.
     *
     * @param unaryOperator function that throws a checked exception.
     * @param <T> type of input into the operation.
     * @param <E> type of exception.
     * @return unary operator object that handles the exception.
     */
    public static <T,E extends Throwable> UnaryOperator<T> unaryOperator(final ThrowableUnaryOperator<T,E> unaryOperator) {
        Objects.requireNonNull(unaryOperator);
        return t -> {
            T result = null;
            try {
                result = unaryOperator.apply(t);
            } catch (Throwable throwable) {
                handle(throwable);
            }
            return result;
        };
    }

    /**
     * Wraps binaryOperator {@link ThrowableBinaryOperator} into a {@link BinaryOperator} function,
     * which handles the checked exceptions.
     *
     * @param binaryOperator function that throws a checked exception.
     * @param <T> type of input into the operation.
     * @param <E> type of exception.
     * @return unary operator object that handles the exception.
     */
    public static <T,E extends Throwable> BinaryOperator<T> binaryOperator(final ThrowableBinaryOperator<T,E> binaryOperator) {
        Objects.requireNonNull(binaryOperator);
        return (t,u) -> {
            T result = null;
            try {
                result = binaryOperator.apply(t,u);
            } catch (Throwable throwable) {
                handle(throwable);
            }
            return result;
        };
    }

    /**
     * Wraps operator {@link ThrowableCallable} into a {@link Callable} function,
     * which handles the checked exceptions.
     *
     * @param callable function that throws a checked exception.
     * @param <T> type of input into the operation.
     * @param <E> type of exception.
     * @return callable object that handles the exception.
     */
    public static <T, E extends Throwable> Callable<T> callable(final ThrowableCallable<T,E> callable) {
        Objects.requireNonNull(callable);
        return () -> {
            T result = null;
            try {
                result = callable.call();
            } catch (Throwable throwable) {
                handle(throwable);
            }
            return result;
        };
    }

    /**
     * Wraps runnable {@link ThrowableRunnable} into a {@link Runnable} function,
     * which handles the checked exceptions.
     * @param <E> type of exception.
     *
     * @param runnable function that throws a checked exception.
     * @return runnable object that handles the exception.
     */
    public static <E extends Throwable> Runnable runnable(final ThrowableRunnable<E> runnable) {
        Objects.requireNonNull(runnable);
        return () -> {
            try {
                runnable.run();
            } catch(Throwable throwable) {
                handle(throwable);
            }
        };
    }

    /**
     * Wraps supplier {@link ThrowableSupplier} into {@link Supplier} function,
     * which handles the checked exception.
     *
     * @param supplier function that throws a checked exception.
     * @param <T> type of input into the operation
     * @param <E> type of exception.
     * @return supplier function that handles the exception.
     */
    public static <T,E extends Throwable> Supplier<T> supplier(final ThrowableSupplier<T,E> supplier) {
        Objects.requireNonNull(supplier);
        return () -> {
            T result = null;
            try {
                result = supplier.get();
            } catch (Throwable throwable) {
                handle(throwable);
            }
            return result;
        };
    }

    private static void handle (Throwable t) {
        if (t instanceof RuntimeException)
            throw (RuntimeException) t;

        if (t instanceof IOException)
            throw new UncheckedIOException((IOException) t);


        if (t instanceof Exception)
            throw new RuntimeException(t);

        if (t instanceof Error)
            throw new RuntimeException(t);
    }

    private Handlers() {}
}