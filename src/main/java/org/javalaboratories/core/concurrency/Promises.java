package org.javalaboratories.core.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is a factory for creating {@link Promise} objects.
 * <p>
 * Its role is to ensure {@link PromisePoolServiceFactory},
 * {@link PromisePoolService} and other objects are properly initialised and
 * ready for the production of {@link Promise} objects. For flexibility, the
 * thread pool service can swapped out for an alternative executor -- configure
 * the concrete implementation in the {@code promise-configuration.properties}
 * file; factory methods also provide a means to create custom {@link Promise}
 * objects.
 * <p>
 * A typical example for creating promise objects is shown:
 *
 * <pre>
 *     {@code
 *          Promise<String> promise = Promises
 *              .newPromise(PrimaryAction.of(() -> doLongRunningTask("Reading integer value from database")))
 *              .then(TransmuteAction.of(value -> "Value read from the database: "+value));
 *
 *          String result = promise.getResult()
 *             .IfPresent(result -> System.out::println(result));
 *     }
 * </pre>
 */
@SuppressWarnings("WeakerAccess")
public final class Promises {

    private final static PromisePoolService promisePoolService;

    /*
     * Instantiate and configure PromisePoolService object for Promise objects.
     */
    static {
        PromisePoolServiceFactory factory = new PromisePoolServiceFactory(new PromiseConfiguration());
        promisePoolService = factory.newPoolService();
    }

    /**
     * Queues all {@link PrimaryAction} objects for processing.
     * <p>
     * Internal worker threads process all {@link PrimaryAction} objects, the
     * number of simultaneous tasks could reach total {@code capacity}
     * of the worker threads in {@link PromisePoolService}. If this is the case,
     * an {@code action} object will remain in the queue until a worker becomes
     * available.
     * <p>
     * A {@link List} of {@link Promise} objects is immediately returned,
     * providing access to processing states of each process.
     * <p>
     * @param actions a {@link List} of {@link PrimaryAction} objects to be queued
     * @param <T> Type of value returned from asynchronous task.
     * @return a {@link List} of {@link Promise} objects for tracking tasks and/or
     * further asynchronous tasks.
     * @throws NullPointerException if {@code actions} is null
     */
    public static <T> List<Promise<T>> all(final List<PrimaryAction<T>> actions) {
        return all(actions,(action) -> () -> new AsyncUndertaking<>(promisePoolService,action));
    }

    /**
     * Factory method to create instances of {@link Promise} objects.
     * <p>
     * Not only is the {@link Promise} object created, but post creation, the
     * the {@link PrimaryAction} task is executed asynchronously and the
     * {@link Promise} returned to the client.
     *
     * @param action a {@link PrimaryAction} encapsulating the task to be
     *               executed asynchronously.
     * @param <T> Type of value returned from asynchronous task.
     * @return a new {@link Promise} object.
     * @throws NullPointerException if {@code action} is null
     */
    public static <T> Promise<T> newPromise(final PrimaryAction<T> action) {
        return newPromise(action, () -> new AsyncUndertaking<>(promisePoolService,action));
    }

    /**
     * Queues all {@link PrimaryAction} objects for processing with a specified
     * implementation of {@link Promise}.
     * <p>
     * Internal worker threads process all {@link PrimaryAction} objects, the
     * number of simultaneous tasks could reach total {@code capacity}
     * of the worker threads in {@link PromisePoolService}. If this is the case,
     * an {@code action} object will remain in the queue until a worker becomes
     * available.
     * <p>
     * A {@link List} of {@link Promise} objects is immediately returned,
     * providing access to processing states of each process.
     * <p>
     * This factory method is really provided for further development purposes,
     * a mechanism to create alternative implementations of {@link Promise}
     * objects. Therefore, it is recommended to use {@link Promises#all(List)}
     * instead, as this method provides the default
     * implementation.
     *
     * @param actions a {@link List} of {@link PrimaryAction} objects to be queued
     * @param function to present an implementation of the {@link Promise} object.
     * @param <T> Type of value returned from asynchronous task.
     * @return a {@link List} of {@link Promise} objects for tracking tasks and/or
     * further asynchronous tasks.
     * @throws NullPointerException if {@code action} is null
     * @throws NullPointerException if {@code function} is null
     */
    private static <T,U extends Promise<T>> List<Promise<T>> all(final List<PrimaryAction<T>> actions,
                                                                final Function<PrimaryAction<T>,Supplier<U>> function) {
        List<PrimaryAction<T>> list = Objects.requireNonNull(actions);
        Function<PrimaryAction<T>,Supplier<U>> factory = Objects.requireNonNull(function);
        List<Promise<T>> results = new ArrayList<>();
        list.forEach(action -> {
            Promise<T> promise = newPromise(action,factory.apply(action));
            results.add(promise);
        });
        return Collections.unmodifiableList(results);
    }

    /**
     * Factory method to create instances of {@link Promise} objects with
     * a specified implementation of {@link Promise}.
     * <p>
     * Not only is the {@link Promise} object created, but post creation, the
     * the {@link PrimaryAction} task is executed asynchronously and the
     * {@link Promise} returned to the client.
     * <p>
     * This factory method is really provided for further development purposes,
     * a mechanism to create an alternative implementation of {@link Promise}
     * objects. Therefore, it is recommended to use
     * {@link Promises#newPromise(PrimaryAction) instead, as this method
     * provides the default implementation.
     *
     * @param action a {@link PrimaryAction} encapsulating the task to be
     *               executed asynchronously.
     * @param supplier supplies an implementation of {@link Promise}
     * @param <T> Type of value returned from asynchronous task.
     * @return a new {@link Promise} object.
     * @throws NullPointerException if {@code action} is null
     */
    private static <T, U extends Promise<T>> Promise<T> newPromise(final PrimaryAction<T> action, final Supplier<U> supplier) {
        PrimaryAction<T> a = Objects.requireNonNull(action,"Cannot keep promise -- no action?");
        U result = Objects.requireNonNull(supplier).get();
        unchecked(result).invokePrimaryAction(a);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T,U extends Promise<T>,V extends AsyncUndertaking<T>> V unchecked(U promise) {
        // This is okay: AsyncUndertaking is the only implementation for now.
        return (V) promise;
    }

    private Promises() {}
}
