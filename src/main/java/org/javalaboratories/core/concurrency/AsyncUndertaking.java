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
import org.javalaboratories.core.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.javalaboratories.core.concurrency.Promise.States.FULFILLED;
import static org.javalaboratories.core.concurrency.Promise.States.PENDING;
import static org.javalaboratories.core.concurrency.Promise.States.REJECTED;

/**
 * Class implements the {@link Promise} interface.
 * <p>
 * {@code AsyncUndertaking} class only has package visibility, but its API is
 * fully exposed via the (@link Promise} interface for client use. There is
 * consideration of additional implementations of the {@link Promise} interface
 * where composition backed by this class is likely.
 *
 * @param <T> Type of object returned from asynchronous tasks.
 * @see Promise for full contract details and usage.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
class AsyncUndertaking<T> implements Promise<T> {

    private final Logger logger = LoggerFactory.getLogger(Promise.class);

    private final Action<T> action;
    private final PromisePoolService service;
    @EqualsAndHashCode.Include
    private final String identity;
    private CompletableFuture<T> future;

    /**
     * This {@link AsyncUndertaking} constructor is not designed to be called
     * explicitly.
     * <p>
     * Initialises the object with a thread pool and a {@link PrimaryAction}
     * action object to be processed asynchronously. Instantiate an instance
     * with the factory methods of the {@link Promises} class.
     *
     * @param service the thread pool service.
     * @param action the primary action of this object to be processed
     *              asynchronously.
     * @throws NullPointerException if service or action is null.
     */
    AsyncUndertaking(final PromisePoolService service, final PrimaryAction<T> action) {
        this(service,action,null);
    }

    /**
     * This copy constructor is only used internally by this promise object.
     * <p>
     * Initialises the object with a thread pool and a {@link PrimaryAction}
     * action object to be processed asynchronously. Instantiate an instance
     * with the factory methods of the {@link Promises} class.
     *
     * @param service the thread pool service.
     * @param action the primary action of this object to be processed
     *              asynchronously.
     * @param future underlying {@link CompletableFuture} object, initially
     *               set to {@code null} until this object is ready to perform the
     *               action asynchronously.
     * @throws NullPointerException if service or action is null.
     */
    private AsyncUndertaking(final PromisePoolService service, final Action<T> action, final CompletableFuture<T> future) {
        this.service = Objects.requireNonNull(service,"No service?");
        this.action = Objects.requireNonNull(action,"No action object?");
        this.future = future;
        this.identity = String.format("promise-{%s}", UUID.randomUUID());
    }

    @Override
    public final Promise<T> then(final TaskAction<T> action) {
        Consumer<T> actionable = doMakeActionable(action);
        CompletableFuture<Void> future = this.future.thenAcceptAsync(actionable,service)
                .whenComplete((value,exception) -> action.getCompletionHandler()
                        .ifPresent(result -> result.accept(null, exception)));

        return new AsyncUndertaking<>(service,action, unchecked(future));
    }

    @Override
    public final <R> Promise<R> then(final TransmuteAction<T, R> action) {
        Function<T,R> transmutable = doMakeTransmutable(action);
        CompletableFuture<R> future = this.future.thenApplyAsync(transmutable,service)
                .whenComplete((newValue,exception) -> action.getCompletionHandler()
                        .ifPresent(result -> result.accept(newValue, exception)));

        return new AsyncUndertaking<>(service,action,future);
    }

    @Override
    public Action<T> getAction() {
        return this.action;
    }

    @Override
    public States getState() {
        return getState(future);
    }

    @Override
    public final String getIdentity() {
        return identity;
    }

    @Override
    public final Nullable<T> getResult()  {
        if ( getState() == Promise.States.PENDING )
            throw new IllegalStateException();

        T value = null;
        try {
            value = future.get();
        } catch (InterruptedException | ExecutionException e) {
            // Ignore, return optional object instead.
        }
        return Nullable.ofNullable(value);
    }

    @Override
    public Promise<T> handle(Consumer<Throwable> handler) {
        Objects.requireNonNull(handler,"No handle object?");
        try {
            future.join();
        } catch (CompletionException e) {
            handler.accept(e.getCause());
        }
        return new AsyncUndertaking<>(service,action,future);
    }

    /**
     * @return a {@link String} object describing this {@link AsyncUndertaking} object.
     */
    @Override
    public String toString() {
        return String.format("[identity=%s,state=%s,service=%s]",identity,getState(),service);
    }

    /**
     * Invokes this promise's primary action asynchronously. The method is
     * part of the life-cycle of this object, and therefore must not be called
     * in any other context. This is why the access level is set to package
     * default, and must remain so.
     *
     * @return true is returned if action is executed asynchronously.
     */
    final boolean invokePrimaryAction(final PrimaryAction<T> action) {
        future = invokePrimaryActionAsync(Objects.requireNonNull(action,"No action?"));
        logger.debug("Promise [{}] invoked action asynchronously successfully",getIdentity());
        return true;
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

    private Consumer<T> doMakeActionable(final TaskAction<T> action) {
        Objects.requireNonNull(action);
        return (value) -> {
            Consumer<T> result = action.getTask().orElseThrow();
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

    private <R> Function<T,R> doMakeTransmutable(final TransmuteAction<T,R> action) {
        Objects.requireNonNull(action);
        return (value) -> {
            Function<T,R> result = action.getTask().orElseThrow();
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

    private States getState(final CompletableFuture<?super T> future) {
        return future == null ? PENDING : future.isCompletedExceptionally() ? REJECTED : FULFILLED;
    }

    private CompletableFuture<T> invokePrimaryActionAsync(final PrimaryAction<T> action) {
        Supplier<T> actionable = doMakePrimaryActionable(action);
        return CompletableFuture.supplyAsync(actionable,service)
                .whenComplete((value,exception) -> action.getCompletionHandler()
                        .ifPresent(consumer -> consumer.accept(value, exception)));
    }

    private static <T> CompletableFuture<T> unchecked(CompletableFuture<?> future) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T> result = (CompletableFuture<T>) future;
        return result;
    }
}
