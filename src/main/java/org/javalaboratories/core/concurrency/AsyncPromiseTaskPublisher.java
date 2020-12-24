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

import org.javalaboratories.core.event.EventBroadcaster;
import org.javalaboratories.core.event.EventPublisher;
import org.javalaboratories.core.event.EventSource;
import org.javalaboratories.util.Arguments;
import org.javalaboratories.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.javalaboratories.core.concurrency.PromiseEvents.PRIMARY_ACTION_EVENT;
import static org.javalaboratories.core.concurrency.PromiseEvents.TOKEN_ACTION_EVENT;
import static org.javalaboratories.core.concurrency.PromiseEvents.TRANSMUTE_ACTION_EVENT;

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
 * There are three types of events all subscribers are notified on:
 * <ol>
 *     <li>{@link PromiseEvents#PRIMARY_ACTION_EVENT}</li>
 *     <li>{@link PromiseEvents#TOKEN_ACTION_EVENT}</li>
 *     <li>{@link PromiseEvents#TRANSMUTE_ACTION_EVENT}</li>
 * </ol>
 * There is no limit on the number of subscribers that can be assigned to this
 * {@code Promise} object. However, if the {@code subscriber} raises an
 * exception, it will be considered {@code toxic} and removed from the internal
 * {@code publisher}.
 * <p>
 * Notification of {@code subscribers} is performed asynchronously to avoid
 * blocking the main/current thread.
 * <p>
 * Constructor in this class is package-access only, use the factory methods
 * provided in the {@link Promises} class.
 *
 * @param <T> Type of object returned from asynchronous tasks.
 */
class AsyncPromiseTaskPublisher<T> extends AsyncPromiseTask<T> implements EventSource {

    private static final Logger logger = LoggerFactory.getLogger(Promise.class);

    private final EventPublisher<EventState<?>> publisher;

    /**
     * Constructs this event-driven {@link Promise} object
     * <p><
     * @param service the thread pool service
     * @param action the action of this object to processed
     *               asynchronously.
     * @param subscribers collection of subscribers/listeners of promise
     *                    objects.
     * @throws NullPointerException if service or action is null.
     */
    AsyncPromiseTaskPublisher(final ManagedPoolService service, final PrimaryAction<T> action,
                              final List<? extends PromiseEventSubscriber> subscribers) {
        super(service,action);
        Arguments.requireNonNull(() -> new IllegalArgumentException("Arguments null?"),service,action,
                subscribers);
        this.publisher = new EventBroadcaster<>(this);
        subscribers.forEach(s -> publisher.subscribe(s,PRIMARY_ACTION_EVENT,TOKEN_ACTION_EVENT,
                TRANSMUTE_ACTION_EVENT));
    }

    /**
     * This constructor is only used internally by this object to create a new
     * {@link Promise} object to represent encapsulated {@code CompletableFuture}.
     * <p>
     *
     * @param service the thread pool service.
     * @param action the action of this object to be processed asynchronously.
     * @param future underlying {@link CompletableFuture} object, initially
     *               set to {@code null} until this object is ready to perform the
     *               action asynchronously.
     * @param publisher underlying event publisher.
     * @throws NullPointerException if service or action or future or promise is null.
     */
    AsyncPromiseTaskPublisher(final ManagedPoolService service, final Action<T> action,
                              final CompletableFuture<T> future, final EventPublisher<EventState<?>> publisher) {
        super(service,action,future);
        Objects.requireNonNull(publisher);
        this.publisher = publisher;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Asynchronously notifies {@link PromiseEventSubscriber} subscribers of
     * the {@link PromiseEvents#TOKEN_ACTION_EVENT} when the promise is
     * complete.
     */
    public Promise<T> then(final TaskAction<T> action) {
        Promise<T> result = super.then(action);
        notify(() -> result.getResult()
                .ifPresent(value -> {
                    EventState<Void> state = new EventState<>(null);
                    publisher.publish(TOKEN_ACTION_EVENT,state);
                }));
        return new AsyncPromiseTaskPublisher<>(getService(),action,getFuture(),publisher);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Asynchronously notifies {@link PromiseEventSubscriber} subscribers of
     * the {@link PromiseEvents#TRANSMUTE_ACTION_EVENT} when the promise is
     * complete.
     */
    public final <R> Promise<R> then(final TransmuteAction<T,R> action) {
        Promise<R> result = super.then(action);
        notify(() -> result.getResult()
                .ifPresent(value -> {
                    EventState<R> state = new EventState<>(value);
                    publisher.publish(TRANSMUTE_ACTION_EVENT,state);
                }));
        return new AsyncPromiseTaskPublisher<>(getService(),action,Generics.unchecked(getFuture()),publisher);
    }

    /**
     * Performs notification of events asynchronously.
     * <p>
     * @param publisher encapsulates underlying task that actually performs
     *                    the notification.
     * @return CompletableFuture object that encapsulates current state of
     * asynchronous process.
     * @throws NullPointerException if broadcaster parameter is null.
     */
    @SuppressWarnings("UnusedReturnValue")
    final CompletableFuture<Void> notify(final AsyncPublisher publisher) {
        Objects.requireNonNull(publisher,"Expected publisher?");
        return CompletableFuture
                .runAsync(publisher::publish)
                .whenComplete(this::handleNotifyComplete);
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
     * the {@link PromiseEvents#PRIMARY_ACTION_EVENT} when the promise is
     * complete.
     *
     * @throws NullPointerException if action is null
     */
    CompletableFuture<T> invokePrimaryActionAsync(final PrimaryAction<T> action) {
        CompletableFuture<T> future = super.invokePrimaryActionAsync(action);
        notify(() -> {
            try {
                EventState<T> state = new EventState<>(future.get());
                publisher.publish(PRIMARY_ACTION_EVENT,state);
            } catch (CancellationException | ExecutionException | InterruptedException e) {
                // Ignore, return optional object instead.
            }});
        return future;
    }

    /**
     * Represents object that has the ability to notify events to subscribers
     * asynchronously.
     */
    @FunctionalInterface
    interface AsyncPublisher {
        void publish();
    }
}
