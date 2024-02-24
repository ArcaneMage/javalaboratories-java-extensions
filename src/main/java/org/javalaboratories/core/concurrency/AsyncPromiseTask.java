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

import lombok.EqualsAndHashCode;
import org.javalaboratories.core.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.javalaboratories.core.concurrency.Promise.States.FULFILLED;
import static org.javalaboratories.core.concurrency.Promise.States.PENDING;
import static org.javalaboratories.core.concurrency.Promise.States.REJECTED;

/**
 * Class implements the {@link Promise} interface.
 * <p>
 * {@code AsyncPromiseTask} class only has package visibility, but its API is
 * fully exposed via the (@link Promise} interface for client use. There is
 * consideration of additional implementations of the {@link Promise} interface
 * where composition backed by this class is likely.
 *
 * @param <T> Type of object returned from asynchronous tasks.
 * @see Promise for full contract details and usage.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class AsyncPromiseTask<T> implements Promise<T>, Invocable<T> {

    private static final Consumer<Throwable> INERT_HANDLER = e -> {};
    private static final Logger logger = LoggerFactory.getLogger(Promise.class);

    private final Action<T> action;
    private final ManagedPromiseService service;
    @EqualsAndHashCode.Include
    private final String identity;
    private CompletableFuture<T> future;

    /**
     * This {@link AsyncPromiseTask} constructor is not designed to be called
     * explicitly.
     * <p>
     * Initialises the object with a thread pool and a {@link PrimaryAction}
     * action object to be processed asynchronously. Instantiate an instance
     * with the factory methods of the {@link Promises} class.
     *
     * @param service the thread pool service.
     * @param action the primary action of this object to be processed
     *               asynchronously.
     * @throws NullPointerException if service or action is null.
     */
    AsyncPromiseTask(final ManagedPromiseService service, final PrimaryAction<T> action) {
        this(service,action,null);
    }

    /**
     * This constructor is only used internally by this object to create a new
     * {@link Promise} object to represent encapsulated {@code CompletableFuture}.
     * <p>
     * Initialises the object with a thread pool and a {@link Action}
     * action object to be processed asynchronously. Instantiate an instance
     * with the factory methods of the {@link Promises} class.
     *
     * @param service the thread pool service.
     * @param action the action of this object to be processed asynchronously.
     * @param future underlying {@link CompletableFuture} object, initially
     *               set to {@code null} until this object is ready to perform the
     *               action asynchronously.
     * @throws NullPointerException if service or action is null.
     */
    AsyncPromiseTask(final ManagedPromiseService service, final Action<T> action, final CompletableFuture<T> future) {
        this.service = Objects.requireNonNull(service,"No service?");
        this.action = Objects.requireNonNull(action,"No action object?");
        this.future = future;
        this.identity = String.format("{%s}", UUID.randomUUID());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> await() {
        return handle(INERT_HANDLER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> then(final TaskAction<T> action) {
        Consumer<T> actionable = doMakeActionable(action);
        CompletableFuture<Void> future = this.future.thenAcceptAsync(actionable,service)
                .whenComplete((value,exception) -> action.getCompletionHandler()
                        .ifPresent(result -> result.accept(null, exception)));
        // This is okay for now, need to revisit.
        @SuppressWarnings("unchecked")
        CompletableFuture<T> f = (CompletableFuture<T>) future;
        return new AsyncPromiseTask<>(service,action,f);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> Promise<R> then(final TransmuteAction<T,R> action) {
        Function<T,R> transmutable = doMakeTransmutable(action);
        CompletableFuture<R> future = this.future.thenApplyAsync(transmutable,service)
                .whenComplete((newValue,exception) -> action.getCompletionHandler()
                        .ifPresent(result -> result.accept(newValue, exception)));

        return new AsyncPromiseTask<>(service,action,future);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Action<T> getAction() {
        return this.action;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public States getState() {
        return getState(future);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getIdentity() {
        return identity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Maybe<T> getResult()  {
        T value = null;
        try {
            value = future.get();
        } catch (CancellationException | ExecutionException | InterruptedException e) {
            // Ignore, return optional object instead.
        }
        return Maybe.ofNullable(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> handle(final Consumer<Throwable> handler) {
        Objects.requireNonNull(handler,"No handle object?");
        try {
            future.join();
        } catch (CompletionException | CancellationException e) {
            handler.accept(e.getCause());
        }
        return new AsyncPromiseTask<>(service,action,future);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean invoke(final PrimaryAction<T> action) {
        future = invokeAsync(Objects.requireNonNull(action,"No action?"));
        logger.debug("Promise [{}] invoked action asynchronously successfully",getIdentity());
        return true;
    }

    /**
     * @return a {@link String} object describing this {@link AsyncPromiseTask} object.
     */
    @Override
    public String toString() {
        return String.format("[identity=%s,state=%s,service=%s]",identity,getState(),service);
    }

    /**
     * @return current underlying future that is executing current action.
     */
    CompletableFuture<T> getFuture() {
        return future;
    }

    /**
     * @return main thread pool service for all promises.
     */
    ManagedPromiseService getService() {
        return service;
    }

    /**
     * Invokes the {@link PrimaryAction} action asynchronously.
     * <p>
     * This is the initial action to be executed asynchronously. It is only
     * overridable in derived classes for additional behaviour, but it is highly
     * recommended to call this method first.
     * <p>
     * @param action the primary action
     * @return the underlying future that executes the primary action.
     * @throws NullPointerException if action is null
     */
    protected CompletableFuture<T> invokeAsync(final PrimaryAction<T> action) {
        Supplier<T> actionable = doMakePrimaryActionable(action);
        return CompletableFuture.supplyAsync(actionable,service)
                .whenComplete((value,exception) -> action.getCompletionHandler()
                        .ifPresent(consumer -> consumer.accept(value, exception)));
    }

    private Supplier<T> doMakePrimaryActionable(final PrimaryAction<T> action) {
        Objects.requireNonNull(action);
        return () -> {
            Supplier<T> result = action.getTask().orElseThrow();
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("Promise [{}] starting task of PrimaryAction object",getIdentity());
                }
                return result.get();
            } finally {
                if (logger.isTraceEnabled()) {
                    logger.trace("Promise [{}] finished task of PrimaryAction object",getIdentity());
                }
            }
        };
    }

    private Consumer<T> doMakeActionable(final TaskAction<? super T> action) {
        Objects.requireNonNull(action);
        return (value) -> {
            Consumer<? super T> result = action.getTask().orElseThrow();
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("Promise [{}] starting task of TaskAction object",getIdentity());
                }
                result.accept(value);
            } finally {
                if (logger.isTraceEnabled()) {
                    logger.trace("Promise [{}] finished task of TaskAction object",getIdentity());
                }
            }
        };
    }

    private <R> Function<T,R> doMakeTransmutable(final TransmuteAction<? super T,? extends R> action) {
        Objects.requireNonNull(action);
        return (value) -> {
            Function<? super T,? extends R> result = action.getTask().orElseThrow();
            try {
                if (logger.isTraceEnabled()) {
                    logger.trace("Promise [{}] starting transmutation task of TransmuteAction object",getIdentity());
                }
                return result.apply(value);
            } finally {
                if (logger.isTraceEnabled()) {
                    logger.trace("Promise [{}] finished transmutation task of TransmuteAction object",getIdentity());
                }
            }
        };
    }

    private States getState(final CompletableFuture<? super T> future) {
        return future == null ? PENDING : !future.isDone() ? PENDING :
                future.isCompletedExceptionally() ? REJECTED : FULFILLED;
    }
}
