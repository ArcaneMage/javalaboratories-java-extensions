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
 * {@link ManagedPoolService} and other objects are properly initialised and
 * ready for the production of {@link Promise} objects. For flexibility, the
 * thread pool service can be swapped out for an alternative executor -- configure
 * the concrete implementation in the "{@code promise-configuration.properties}"
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

    private final static ManagedPoolService managedPoolService;

    /*
     * Instantiate and configure ManagedPromisePool object for Promise objects.
     */
    static {
        PromisePoolServiceFactory<ManagedPoolService> factory = new PromisePoolServiceFactory<>(new PromiseConfiguration());
        managedPoolService = factory.newPoolService();
    }

    /**
     * Queues all {@link PrimaryAction} objects for processing with a specified
     * implementation of {@link Promise}.
     * <p>
     * Internal worker threads process all {@link PrimaryAction} objects, the
     * number of simultaneous tasks could reach total {@code capacity}
     * of the worker threads in {@link ManagedPromisePoolExecutor}. If this is the
     * case, an {@code action} object will remain in the queue until a worker
     * becomes available.
     * <p>
     * A {@link Promise} object is immediately returned, providing a reference
     * to an asynchronous process that is currently waiting completion of the all
     * the {@code actions} objects.
     * <p>
     *
     * @param actions a {@link List} of {@link PrimaryAction} objects to be queued
     * @param <T> Type of value returned from asynchronous task.
     * @return a {@link Promise} object that promises to wait for the conclusion of
     * all aforementioned {@code actions} objects.
     * @throws NullPointerException if {@code action} is null
     */
    public static <T> Promise<List<Promise<T>>> all(final List<PrimaryAction<T>> actions) {
        return all(actions,(action) -> () -> new AsyncUndertaking<>(managedPoolService,action));
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
        return newPromise(action, () -> new AsyncUndertaking<>(managedPoolService,action));
    }

    /**
     * Queues all {@link PrimaryAction} objects for processing with a specified
     * implementation of {@link Promise}.
     * <p>
     * Internal worker threads process all {@link PrimaryAction} objects, the
     * number of simultaneous tasks could reach total {@code capacity}
     * of the worker threads in {@link ManagedPromisePoolExecutor}. If this is the
     * case, an {@code action} object will remain in the queue until a worker
     * becomes available.
     * <p>
     * A {@link Promise} object is immediately returned, providing a reference
     * to an asynchronous process that is currently waiting completion of the all
     * the {@code actions} objects.
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
     * @return a {@link Promise} object that promises to wait for the conclusion of
     * all aforementioned {@code actions} objects.
     * @throws NullPointerException if {@code action} is null
     * @throws NullPointerException if {@code function} is null
     */
    private static <T,U extends Promise<T>> Promise<List<Promise<T>>> all(final List<PrimaryAction<T>> actions,
                                                                final Function<PrimaryAction<T>,Supplier<U>> function) {
        List<PrimaryAction<T>> list = Objects.requireNonNull(actions);
        Function<PrimaryAction<T>,Supplier<U>> factory = Objects.requireNonNull(function);
        List<Promise<T>> results = new ArrayList<>();
        // Start asynchronous processes with custom promise implementation
        list.forEach(action -> {
            Promise<T> promise = newPromise(action,factory.apply(action));
            results.add(promise);
        });
        // Start new thread process that will wait on aforementioned asynchronous processes
        PrimaryAction<List<Promise<T>>> action = PrimaryAction.of(() -> {
                    results.forEach(Promise::await);
                    return Collections.unmodifiableList(results);});
        return newPromise(action);
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
