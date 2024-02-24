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
import org.javalaboratories.core.concurrency.PromiseEvent.Actions;
import org.javalaboratories.core.event.*;
import org.javalaboratories.core.util.Arguments;
import org.javalaboratories.core.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.javalaboratories.core.concurrency.Promise.States.FULFILLED;

/**
 * Class implements the {@link Promise} interface.
 * <p>
 * Initialises the object with a thread pool and a {@link PrimaryAction}
 * action object to be processed asynchronously. Instantiate an instance
 * with the factory methods of the {@link Promise} class.
 * <p>
 * This implementation has the ability to notify {@link PromiseEventSubscriber}
 * objects on the completion of a {@link Promise} -- event driven. It implements
 * the {@code Observer} design pattern. To complete the pattern, implement the
 * {@link PromiseEventSubscriber} interface and provide a {@link List}
 * collection of them.
 * <p>
 * <u>Notification Policy</u>
 * <p>
 * There is no limit to the number of subscribers that can be assigned to this
 * {@code Promise} object. However, if the {@code subscriber} raises an
 * exception, it will be considered {@code toxic} and removed from the internal
 * {@code publisher}. Moreover, if the current {@code action} raises an
 * exception, none of {@code subscribers} will be notified. Exceptions are
 * expected to be handled with the usual {@code Promise} handlers: whether in the
 * {@link Action} objects and/or with the {@link Promise#handle(Consumer)}
 * method.
 * <p>
 * Notification of {@code subscribers} is performed asynchronously to avoid
 * blocking the main/current thread. This is also means when this object
 * transitions to the {@link States#FULFILLED} state, it is possible to retrieve
 * the result of the asynchronous computation <b>before all</b> the
 * {@link PromiseEventSubscriber} objects are notified of the resultant result
 * in the {@link Event}. This is a reasonable and deliberate design: it is
 * unreasonable to block the {@link Promise#await}, {@link Promise#handle} and
 * {@link Promise#getResult()} methods until all {@link PromiseEventSubscriber}
 * have been notified. It is not the role of this object to be dependent on
 * {@code subscribers}, but it does guarantee to eventually inform all the
 * {@code subscribers} of the current {@link Event}.
 * <p>
 * Constructor in this class is package-access only, use the factory methods
 * provided in the {@link Promises} class.
 *
 * @param <T> Type of object returned from asynchronous tasks.
 */
class AsyncPromiseTaskPublisher<T> extends AsyncPromiseTask<T> implements EventSource {

    private static final Logger logger = LoggerFactory.getLogger(Promise.class);

    private final EventPublisher<PromiseEvent<?>> publisher;

    /**
     * Constructs this event-driven {@link Promise} object
     * <p>
     * Use the {@link Promises} factory method to construct this object.
     *
     * @param service the thread pool service
     * @param action the action of this object to processed
     *               asynchronously.
     * @param subscribers collection of subscribers/listeners of promise
     *                    objects.
     * @throws NullPointerException if service or action is null.
     */
    AsyncPromiseTaskPublisher(final ManagedPromiseService service, final PrimaryAction<T> action,
                              final List<? extends PromiseEventSubscriber<?>> subscribers) {
        super(service,action);
        Arguments.requireNonNull(() -> new IllegalArgumentException("Arguments null?"),service,action,subscribers);
        this.publisher =  new EventBroadcaster<>(this);

        subscribers.forEach(s ->
            // This is fine because this is really a collection of
            // EventSubscriber types.
            publisher.subscribe(Generics.unchecked(s))
        );
    }

    /**
     * This constructor is only used internally by this object to create a new
     * {@link Promise} object to represent encapsulated {@code CompletableFuture}
     * and {@code publisher}.
     * <p>
     * Preferably use {@link AsyncPromiseTaskPublisher( ManagedPromiseService ,
     * PrimaryAction, List)} constructor or the {@link Promises} factory method.
     *
     * @param service the thread pool service.
     * @param action the action of this object to be processed asynchronously.
     * @param future underlying {@link CompletableFuture} object, initially
     *               set to {@code null} until this object is ready to perform the
     *               action asynchronously.
     * @param publisher underlying event publisher.
     * @throws NullPointerException if service or action or future or promise is null.
     */
     AsyncPromiseTaskPublisher(final ManagedPromiseService service, final Action<T> action,
                               final CompletableFuture<T> future, final EventPublisher<PromiseEvent<?>> publisher) {
        super(service,action,future);
        Objects.requireNonNull(publisher);
        this.publisher = publisher;
    }

    /**
     * Handles the notification completion of subscribers
     * <p>
     * Default implementation is to log completion but can be overridden
     * in derived classes in this package.
     *
     * @param value returned value from the asynchronous event notification.
     * @param throwable an exception thrown during notification.
     */
    void handleNotifyComplete(Void value, Throwable throwable) {
        logger.debug("Promise [{}] notification of subscribers complete",getIdentity());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Asynchronously notifies {@link PromiseEventSubscriber} subscribers of
     * the {@link PromiseEvent} when the promise is complete.
     */
    public Promise<T> then(final TaskAction<T> action) {
        Promise<T> promise = super.then(action);
        notify(() -> notifyEvent(promise,Actions.TASK_ACTION));
        CompletableFuture<T> future = ((AsyncPromiseTask<T>) promise).getFuture();
        return new AsyncPromiseTaskPublisher<>(getService(),action,future,publisher);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Asynchronously notifies {@link PromiseEventSubscriber} subscribers of
     * the {@link PromiseEvent} when the promise is complete.
     */
    public final <R> Promise<R> then(final TransmuteAction<T,R> action) {
        Promise<R> promise = super.then(action);
        notify(() -> notifyTransmuteEvent(promise));
        CompletableFuture<R> future = ((AsyncPromiseTask<R>) promise).getFuture();

        return new AsyncPromiseTaskPublisher<>(getService(),action,future,publisher);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Asynchronously notifies {@link PromiseEventSubscriber} subscribers of
     * the {@link PromiseEvent} when the promise is complete.
     *
     * @throws NullPointerException if action is null
     */
    CompletableFuture<T> invokePrimaryActionAsync(final PrimaryAction<T> action) {
        CompletableFuture<T> future = super.invokePrimaryActionAsync(action);
        notify(() -> notifyEvent(future, Actions.PRIMARY_ACTION));
        return future;
    }

    /**
     * Performs notification of events asynchronously.
     * <p>
     * @param runnable encapsulates underlying task that actually performs
     *                    the notification.
     * @return CompletableFuture object that encapsulates current state of
     * asynchronous process.
     * @throws NullPointerException if broadcaster parameter is null.
     */
    @SuppressWarnings("UnusedReturnValue")
    final CompletableFuture<Void> notify(final Runnable runnable) {
        Objects.requireNonNull(publisher,"Expected publisher?");
        CompletableFuture<Void> result = CompletableFuture
                .runAsync(runnable,getService())
                .whenComplete(this::handleNotifyComplete);
        logger.debug("Promise [{}] notifying subscribers",getIdentity());
        return result;
    }

    @SuppressWarnings("SameParameterValue")
    private void notifyEvent(final Future<T> future, final Actions action) {
        Arguments.requireNonNull(future);
        try {
            PromiseEvent<T> event = new PromiseEvent<>(action,future.get());
            publisher.publish(event);
        } catch (CancellationException | ExecutionException | InterruptedException e) {
            // Ignore, return optional object instead.
        }
    }

    private void notifyEvent(final Promise<T> promise, final Actions action) {
        Arguments.requireNonNull(promise);
        Maybe<T> value = promise.getResult();
        if (promise.getState() == FULFILLED) {
            publisher.publish(new PromiseEvent<>(action,value.orElse(null)));
        }
    }

    private <R> void notifyTransmuteEvent(Promise<R> promise) {
        Arguments.requireNonNull(promise);
        Maybe<R> value = promise.getResult();
        if (promise.getState() == FULFILLED) {
            PromiseEvent<R> event = new PromiseEvent<>(Actions.TRANSMUTE_ACTION,value.orElse(null));
            publisher.publish(event);
        }
    }
}
