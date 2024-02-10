/*
 * Copyright 2020 Kevin Henry
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.javalaboratories.core.concurrency;

import org.javalaboratories.core.Maybe;
import org.javalaboratories.core.util.Generics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is a factory for creating {@link Promise} objects.
 * <p>
 * Its role is to ensure {@link PromisePoolServiceFactory},
 * {@link ManagedPromiseService} and other objects are properly initialised and
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
 *
 * Here is another example using an alternative implementation of {@link Promise}
 * object which supports event-driven notification in several easy steps:
 * <pre>
 *     {@code
 *          // First, implement listener classes
 *          public static class PromiseEventListener implements PromiseEventSubscriber {
 *              ...
 *              ...
 *              public void notify(final PromiseEvent<T> event) {
 *                  if (event.isAny(PRIMARY_ACTION,TASK_ACTION,TRANSMUTE_ACTION)) {
 *                      logger.info("Listener {} received event={}, state={}",name,event.getEventId(),event.getValue());
 *                      events++;
 *                  }
 *              }
 *          }
 *          ...
 *          ...
 *          List<PromiseEventListener> listeners = Arrays.toList(new PromiseEventListener(),...);
 *
 *          // Then instantiate Promise object with the factory method
 *          Promise<String> promise = Promises
 *              .newPromise(PrimaryAction.of(() -> doLongRunningTask("Reading integer value from database")),listeners)
 *              .then(TransmuteAction.of(value -> "Value read from the database: "+value));
 *     }
 * </pre>
 * The above example illustrates the ability to not only define your {@link
 * Action} handlers but also notify {@code listeners/subscribers} of {@link
 * PromiseEvent} -- there is no limit to the number of listeners, and to avoid
 * blocking, they are notified asynchronously.
 */
@SuppressWarnings("WeakerAccess")
public final class Promises {

    private final static ManagedPromiseService managedPoolService;

    /*
     * Instantiate and configure ManagedPromisePool object for Promise objects.
     */
    static {
        PromisePoolServiceFactory<ManagedPromiseService> factory = new PromisePoolServiceFactory<>(new PromiseConfiguration());
        managedPoolService = factory.newManagedPromiseService();
    }

    /**
     * Queues all {@link PrimaryAction} objects for processing with a specified
     * implementation of {@link Promise}.
     * <p>
     * Internal worker threads process all {@link PrimaryAction} objects, the
     * number of simultaneous tasks could reach total {@code capacity}
     * of the worker threads in {@link ManagedPromiseService}. If this is the
     * case, an {@code action} task object will remain in the queue until a worker
     * becomes available.
     * <p>
     * A {@link Promise} object is immediately returned, providing a reference
     * to an asynchronous process that is currently waiting completion of the all
     * the {@code actions} objects. However, if any of the {@code Promise} objects
     * fail, the first rejection encountered will be returned encapsulated as a
     * {@link Promise} object. Use the {@link Promise#handle(Consumer)} to catch
     * or handle the exception thrown asynchronously.
     * <p>
     *
     * @param actions a {@link List} of {@link PrimaryAction} objects to be queued
     * @param <T> Type of value returned from asynchronous task.
     * @return a {@link Promise} object that promises to wait for the conclusion of
     * all aforementioned {@code actions} objects.
     * @throws NullPointerException if {@code action} is null
     */
    public static <T> Promise<List<Promise<T>>> all(final List<PrimaryAction<T>> actions) {
        return all(actions,false);
    }

    /**
     * Queues all {@link PrimaryAction} objects for processing with a specified
     * implementation of {@link Promise}.
     * <p>
     * Internal worker threads process all {@link PrimaryAction} objects, the
     * number of simultaneous tasks could reach total {@code capacity}
     * of the worker threads in {@link ManagedPromiseService}. If this is the
     * case, an {@code action} task object will remain in the queue until a worker
     * becomes available.
     * <p>
     * A {@link Promise} object is immediately returned, providing a reference
     * to an asynchronous process that is currently waiting completion of the all
     * the {@code actions} objects.
     * <p>
     *
     * @param actions a {@link List} of {@link PrimaryAction} objects to be queued
     * @param settle {@code true} all promises will either resolve or reject, but
     *                              exception is not handled; {@code false} means to
     *                              return the first {@link Promise} object that
     *                              encountered an error asynchronously.
     * @param <T> Type of value returned from asynchronous task.
     * @return a {@link Promise} object that promises to wait for the conclusion of
     * all aforementioned {@code actions} objects.
     * @throws NullPointerException if {@code action} is null
     */
    public static <T> Promise<List<Promise<T>>> all(final List<PrimaryAction<T>> actions, boolean settle) {

        List<Promise<T>> promises = all(actions,(action) -> () -> new AsyncPromiseTask<>(managedPoolService,action));

        // Start new thread process that will wait on aforementioned asynchronous
        // processes
        PrimaryAction<List<Promise<T>>> action = PrimaryAction.of(() -> {
            promises.forEach(p -> {
                if (settle) p.await();
                else p.handle(Promise.DEFAULT_EXCEPTION_HANDLER);
            });
            return promises;
        });

        return newPromise(action,() -> new AsyncPromiseTask<>(managedPoolService,action));
    }

    /**
     * Factory method to create instances of {@link Promise} objects.
     * <p>
     * Not only is the {@link Promise} object created, but post creation, the
     * the {@link Supplier} function is executed asynchronously and the
     * {@link Promise} returned to the client.
     *
     * @param supplier a {@link Supplier} the task to be executed
     *                 asynchronously.
     * @param <T> Type of value returned from asynchronous task.
     * @return a new {@link Promise} object.
     * @throws NullPointerException if {@code action} is null
     * @see AsyncPromiseTask
     */
    public static <T> Promise<T> newPromise(final Supplier<? extends T> supplier) {
        return newPromise(PrimaryAction.of(Objects.requireNonNull(supplier,"No supplier")));
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
     * @see AsyncPromiseTask
     */
    public static <T> Promise<T> newPromise(final PrimaryAction<T> action) {
        return newPromise(action, () -> new AsyncPromiseTask<>(managedPoolService,action));
    }
    /**
     * Factory method to create instances of event-driven {@link Promise}
     * objects.
     * <p>
     * Not only is the {@link Promise} object created, but post creation, the
     * the {@link Supplier} task is executed asynchronously and the {@link Promise}
     * returned to the client.
     * <p>
     * This implementation of a {@link Promise} object has the ability to publish
     * events to its {@code subscribers}.
     *
     * There is no limit to the number of {@code subscribers}, but if a
     * {@code subscriber} is considered "toxic" (unhandled exception raised),
     * the {@code subscriber} will be banned from event notification.
     * Notification of events are performed asynchronously to avoid blocking
     * in the main/current thread.
     *
     * @param supplier a {@link PrimaryAction} encapsulating the task to be
     *               executed asynchronously.
     * @param subscribers a collection of {@link PromiseEventSubscriber}
     *                    objects.
     * @param <T> Type of value returned from asynchronous task.
     * @return a new {@link Promise} object.
     * @throws NullPointerException if {@code action} is null
     * @see AsyncPromiseTaskPublisher
     */
    public static <T> Promise<T> newPromise(final Supplier<? extends T> supplier,
                                            final List<? extends PromiseEventSubscriber<T>> subscribers) {
        return newPromise(PrimaryAction.of(supplier),subscribers);
    }

    /**
     * Factory method to create instances of event-driven {@link Promise}
     * objects.
     * <p>
     * Not only is the {@link Promise} object created, but post creation, the
     * the {@link PrimaryAction} task is executed asynchronously and the
     * {@link Promise} returned to the client.
     * <p>
     * This implementation of a {@link Promise} object has the ability to publish
     * events to its {@code subscribers}.
     *
     * There is no limit to the number of {@code subscribers}, but if a
     * {@code subscriber} is considered "toxic" (unhandled exception raised),
     * the {@code subscriber} will be banned from event notification.
     * Notification of events are performed asynchronously to avoid blocking
     * in the main/current thread.
     *
     * @param action a {@link PrimaryAction} encapsulating the task to be
     *               executed asynchronously.
     * @param subscribers a collection of {@link PromiseEventSubscriber}
     *                    objects.
     * @param <T> Type of value returned from asynchronous task.
     * @return a new {@link Promise} object.
     * @throws NullPointerException if {@code action} is null
     * @see AsyncPromiseTaskPublisher
     */
    public static <T> Promise<T> newPromise(final PrimaryAction<T> action,
                                            final List<? extends PromiseEventSubscriber<T>> subscribers) {
        return newPromise(action, () -> new AsyncPromiseTaskPublisher<>(managedPoolService,action,subscribers));
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
     * A {@link List} collection of {@link Promise} objects is immediately returned,
     * providing a reference to an asynchronous process that is currently waiting
     * completion of the all the {@code actions} objects.
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
     * @param <U> Type promise implementation returned.
     * @return a {@link List} collection of {@link Promise} objects.
     *
     * @throws NullPointerException if {@code action} or {@code function}is null
     */
    private static <T,U extends Promise<T>> List<Promise<T>> all(final List<PrimaryAction<T>> actions,
                                                                 final Function<PrimaryAction<T>,Supplier<U>> function) {
        List<PrimaryAction<T>> list = Objects.requireNonNull(actions);
        Function<PrimaryAction<T>,Supplier<U>> factory = Objects.requireNonNull(function);

        // Start asynchronous processes with custom promise implementation
        List<Promise<T>> promises = new ArrayList<>();
        list.forEach(action -> {
            Promise<T> promise = newPromise(action,factory.apply(action));
            promises.add(promise);
        });
        return Collections.unmodifiableList(promises);
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
     * {@link Promises#newPromise(PrimaryAction)} instead, as this method
     * provides the default implementation.
     *
     * @param action a {@link PrimaryAction} encapsulating the task to be
     *        executed asynchronously.
     * @param supplier supplies an implementation of {@link Promise}
     * @param <T> Type of value returned from asynchronous task.
     * @return a new {@link Promise} object.
     * @throws NullPointerException if {@code action} is null
     */
    private static <T, U extends Promise<T>> Promise<T> newPromise(final PrimaryAction<T> action,
                                                                   final Supplier<U> supplier) {
        PrimaryAction<T> a = Objects.requireNonNull(action,"Cannot keep promise -- no action?");
        U result = Objects.requireNonNull(supplier).get();

        Invocable<T> invocable = asInvocable(result)
                .orElseThrow(() -> new IllegalArgumentException("Promise object is not invocable -- promise unkept"));
        invocable.invokeAction(a);
        return result;
    }

    /**
     * Returns the {@link Promise} object as an {@link Invocable}, if possible.
     * <p>
     * @param promise the {@link Promise} object that implements {@link Invocable}
     * @param <T> The type of the resultant value returned from the asynchronous
     *           task.
     * @return {@link Maybe} encapsulates {@link Invocable} implementation.
     */
    private static <T> Maybe<Invocable<T>> asInvocable(final Promise<T> promise) {
        Maybe<Invocable<T>> result;
        try {
            result = Generics.unchecked(Maybe.of(Objects.requireNonNull(promise)));
        } catch (ClassCastException e) {
            result = Maybe.empty();
        }
        return result;
    }

    private Promises() {}
}
