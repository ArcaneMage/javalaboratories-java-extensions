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

import static org.javalaboratories.core.concurrency.Promise.States.*;

/**
 * The {@code Promise} object is a lightweight abstraction of the
 * {@link CompletableFuture} object, the inspiration of which came from the
 * JavaScript's Promise object behaviour. This implementation provides an
 * easily understood API for asynchronous submission of tasks encapsulated
 * as {@link AbstractAction} objects with comprehensive exception handling.
 * <p>
 * For example, the following promises to execute the {@code doLongRunningTask()}
 * task asynchronously immediately after the construction of the
 * {@link PrimaryAction} object:
 * <pre
 * {@code
 *      Promise<Integer> promise = Promises
 *          .newPromise(PrimaryAction.of(() -> doLongRunningTask("Hello World")));
 *
 *      int result = promise.getResult().orElse(-1);
 * }
 * </pre>
 * {@code Promise} objects first have to be created with the {@code newPromise}
 * factory method of the {@link Promises} class with a {@link PrimaryAction}
 * action object, but then what about the results of the process? Use the
 * {@link Promise#getResult()} to retrieve the results. This method returns
 * a {@code Nullable} object because it is possible that the process may
 * have resulted in an error or was terminated prematurely. Note: it will block
 * the current thread until the process has completed successfully or
 * otherwise.
 * <p>
 * The pre-requisite of the {@link PrimaryAction} object is the task expressed in
 * lambda must always return a value. After task completion, this value will
 * then be available for retrieval or optionally forwarded to the next action.
 * For example:
 * <pre
 * {@code
 *      Promise<Integer> promise = Promises
 *          .newPromise(PrimaryAction.of(() -> doLongRunningTask("Hello World")))
 *          .then(TaskAction.of(value -> System.out.println("Result: "+value)))
 *
 *      int result = promise.getResult().orElse(-1);
 * }
 * </pre>
 * In the above example, both the {@link Promise#then} method and the
 * {@link TaskAction} object are introduced to illustrate subsequent asynchronous
 * processing of action objects. The {@code then} method executes the action
 * in a separate thread as soon as the previous action completes. There is
 * no restriction on the number of subsequent {@code then} methods -- the
 * underlying thread pool {@link PromisePoolService} will manage the processes.
 * The {@link TaskAction} object is useful for scenarios where the value is not
 * expected from the task, but what about situations where the value has to
 * undergo some kind of transformation? Perhaps from one type to another or
 * the value is required for a calculation resulting in a new value.
 * This is the role of the {@link TransmuteAction} action object. Example of
 * usage is shown below:
 * <pre
 * {@code
 *      Promise<String> promise = Promises
 *          .newPromise(PrimaryAction.of(() -> doLongRunningTask("Reading integer value from database")))
 *          .then(TransmuteAction.of(value -> "Value read from the database: "+value));
 *
 *      String result = promise.getResult().orElse("No result");
 *      System.out.println(result);
 * }
 * </pre>
 * In the above use case, the first action is to read the integer value from
 * a database, then the second action transforms the value into a {@link String},
 * which is then retrieved by {@link Promise#getResult()} method call.
 * <p>
 * <b>Handling Completion Asynchronously</b>
 * <p>
 * <b>All</b> action objects have the ability to handle results and exceptions
 * within their own thread context. When the action task completes, the promise
 * object then invokes the action's result handler (if it is available), passing
 * to it the resultant value of the task or the exception object for processing.
 * Review the example below which illustrates this behaviour.
 * <pre>
 * {@code
 *
 *      public void handleResult(int value, Throwable e) {
 *          if ( e != null )
 *              logger.error("Exception thrown: ",e);
 *          else
 *              logger.info("Completed successfully with result: "+value);
 *      }
 *
 *      ...
 *      ...
 *
 *      Promise<Integer> promise = Promises
 *          .newPromise(PrimaryAction.of(() -> doLongRunningTask("Reading integer value from database")))
 *          .then(TransmuteAction.of(value -> value / 0,this::handleResult));
 * }
 * </pre>
 * Alternatively, it is also possible to handle any exception thrown by the action
 * objects in the main thread context with the help of the
 * {@link Promise#handle(Consumer)} method. The difference here is this method
 * blocks and waits for threads to complete. For example:
 * <pre>
 * {@code
 *      Promise<Integer> promise = Promises
 *          .newPromise(PrimaryAction.of(() -> doLongRunningTask("Reading integer value from database")))
 *          .then(TransmuteAction.of(value -> value / 0))
 *          .handle(e -> logger.error("Houston we have a problem!",e));
 * }
 * </pre>
 * This covers the main behavior fo the {@code Promise} object. Explore the method
 * documentation for more information.
 *
 * @param <T> Type of object returned from asynchronous tasks.
 *
 * @see Promises
 * @see PrimaryAction
 * @see TaskAction
 * @see TransmuteAction
 * @see Promise#getState()
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuppressWarnings("WeakerAccess")
public class Promise<T> {
    /**
     * Returns the current state of this promise object.
     * <p>
     * The transition of states is as follows:
     * <pre>
     *     PENDING --> FULFILLED | REJECTED
     * </pre>
     * Promise objects always start with {@link States#PENDING}. Essentially
     * means no action has been undertaken by this object yet, but the moment an
     * action object is processed, the state will either be {@link States#FULFILLED}
     * or {@link States#REJECTED}, depending on the outcome of the action.
     */
    public enum States { PENDING, FULFILLED, REJECTED }

    private final Logger logger = LoggerFactory.getLogger(Promise.class);

    private final Action action;
    private final PromisePoolService service;
    @EqualsAndHashCode.Include
    private final String identity;
    private CompletableFuture<T> future;

    /**
     * This {@link Promise} constructor is not designed to be called explicitly.
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
    Promise(final PromisePoolService service, final PrimaryAction<T> action) {
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
    private Promise(final PromisePoolService service, final Action<?> action, final CompletableFuture<T> future) {
        Objects.requireNonNull(action,"No service object?");
        Objects.requireNonNull(action,"No action object?");
        this.service = service;
        this.future = future;
        this.action = action;
        this.identity = String.format("promise-{%s}", UUID.randomUUID());
    }

    /**
     * Having completed the previous action, now execute {@link TaskAction} action
     * asynchronously, and return a new {@link Promise} object to manage the
     * {@link TaskAction} object and the underlying {@link CompletableFuture} future.
     *
     * @param action the action being processed asynchronously.
     * @return a new {@link Promise} object to manage the {@link TaskAction} action
     * object.
     */
    public final Promise<T> then(final TaskAction<T> action) {
        Consumer<T> actionable = doMakeActionable(action);
        CompletableFuture<Void> future = this.future.thenAcceptAsync(actionable,service)
                .whenComplete((value,exception) -> action.getCompletionHandler()
                        .ifPresent(result -> result.accept(value, exception)));

        return new Promise<>(service,action,toFuture(future));
    }

    /**
     * Having completed the previous action, now execute {@link TransmuteAction}
     * action asynchronously, and return a new {@link Promise} object to manage
     * the {@link TaskAction} object and the underlying {@link CompletableFuture}
     * future.
     *
     * @param action the action being processed asynchronously.
     * @return a new {@link Promise} object to manage the {@link TransmuteAction}
     * action object.
     */
    public final <R> Promise<R> then(final TransmuteAction<T,R> action) {
        Function<T,R> transmutable = doMakeTransmutable(action);
        CompletableFuture<R> future = this.future.thenApplyAsync(transmutable,service)
                .whenComplete((newValue,exception) -> action.getCompletionHandler()
                        .ifPresent(result -> result.accept(newValue, exception)));

        return new Promise<>(service,action,future);
    }

    /**
     * Returns the current state of this promise object.
     * <p>
     * The transition of states is as follows:
     * <pre>
     *     PENDING -> FULFILLED | REJECTED
     * </pre>
     * Promise objects always start with {@link States#PENDING}. Essentially, no
     * action has been undertaken by this object yet, but the moment an action
     * object is processed, the state will either be {@link States#FULFILLED}
     * or {@link States#REJECTED} depending on the outcome of the action.
     *
     * @return enum {@code States}, the current state of this {@link Promise}
     * object.
     */
    public States getState() {
        return getState(future);
    }

    /**
     * Returns a unique identity of this {@link Promise} object.
     * <p>
     * This is particularly useful for {@code HashMap} or other collection
     * objects that require a mechanism to uniquely identify {@link Promise}
     * objects. The {@link Promise#toString()} method also reports {@code identity}
     * making it useful for debugging purposes.
     *
     * @return the unique {@code identity} of the {@link Promise} object.
     */
    public final String getIdentity() {
        return identity;
    }

    /**
     * Returns the result of the this {@link Promise} action processing.
     * <p>
     * This is a blocking call. It will wait for all of the asynchronous
     * processes to complete that relate to this {@link Promise} object, the
     * result of which is described in the returned {@link Nullable} object.
     * <p>
     * If an exception is thrown in any of the processes, {@link Nullable} object
     * will be empty.
     *
     * @return a {@link Nullable} object the describes the result.
     */
    public final Nullable<T> getResult()  {
        if ( getState() == States.PENDING )
            throw new IllegalStateException();

        T value = null;
        try {
            value = future.get();
        } catch (InterruptedException | ExecutionException e) {
            // Ignore, return optional object instead.
        }
        return Nullable.ofNullable(value);
    }

    /**
     * Handles any exception thrown asynchronously in the current/main thread.
     * <p>
     * This is a blocking call. It will wait for all of the asynchronous processes
     * to complete that relate to this {@link Promise} object. Any of the
     * processes that complete exceptionally, will result in the {@code handler}
     * function being executed.
     *
     * @param handler Function to handle any exception thrown.
     * @return a new {@link Promise} object.
     */
    public Promise<T> handle(Consumer<Throwable> handler) {
        Objects.requireNonNull(handler,"No handle object?");
        try {
            future.join();
        } catch (CompletionException e) {
            handler.accept(e.getCause());
        }
        return new Promise<>(service,action,future);
    }

    /**
     * @return a {@link String} object describing this {@link Promise} object.
     */
    @Override
    public String toString() {
        return String.format("[identity=%s,state=%s,service=%s]",identity,getState(),service);
    }

    /**
     * @return the current {@link AbstractAction} object being managed by this
     * {@link Promise} object.
     */
    protected Action<T> getAction() {
        @SuppressWarnings("unchecked")
        Action<T> action = this.action;
        return action;
    }

    /**
     * Invokes this promise's primary action asynchronously. The method is
     * part of the life-cycle of this object, and therefore must not be called
     * in any other context. This is why the access level is set to package
     * default, and must remain so.
     *
     * @return true is returned if action is executed asynchronously.
     */
    final boolean invokePrimaryAction(PrimaryAction<T> action) {
        future = invokePrimaryActionAsync(action);
        logger.debug("Promise [{}] invoked action asynchronously successfully",getIdentity());
        return true;
    }

    private Supplier<T> doMakePrimaryActionable(final PrimaryAction<T> action) {
        Objects.requireNonNull(action);
        return () -> {
            Supplier<T> result = action.getTask().orElseThrow();
            try {
                return result.get();
            } finally {
                if (logger.isTraceEnabled()) {
                    logger.trace("Promise [{}] processed task of PrimaryAction object",getIdentity());
                }
            }
        };
    }

    private Consumer<T> doMakeActionable(final TaskAction<T> action) {
        Objects.requireNonNull(action);
        return (value) -> {
            Consumer<T> result = action.getTask().orElseThrow();
            try {
                result.accept(value);
            } finally {
                if (logger.isTraceEnabled()) {
                    logger.trace("Promise [{}] processed task of TaskAction object",getIdentity());
                }
            }
        };
    }

    private <R> Function<T,R> doMakeTransmutable(final TransmuteAction<T,R> action) {
        Objects.requireNonNull(action);
        return (value) -> {
            Function<T,R> result = action.getTransmute().orElseThrow();
            try {
                return result.apply(value);
            } finally {
                if (logger.isTraceEnabled()) {
                    logger.trace("Promise [{}] processed transmute task of TransmuteAction object",getIdentity());
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

    private static <T> CompletableFuture<T> toFuture(CompletableFuture<?> future) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T> result = (CompletableFuture<T>) future;
        return result;
    }
}
