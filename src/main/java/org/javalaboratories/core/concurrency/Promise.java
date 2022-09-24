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

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The {@code Promise} object is a lightweight abstraction of the
 * {@link CompletableFuture} object, the inspiration of which came from the
 * JavaScript's Promise object behaviour. This implementation provides an
 * easily understood API for asynchronous submission of tasks encapsulated
 * as {@link Action} objects with comprehensive exception handling.
 * <p>
 * For example, the following promises to execute the {@code doLongRunningTask()}
 * task asynchronously immediately after the construction of the
 * {@link PrimaryAction} object:
 * <pre>
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
 * a {@code Maybe} object because it is possible that the process may
 * have resulted in an error or was terminated prematurely. Note: it will block
 * the current thread until the process has completed successfully or
 * otherwise.
 * <p>
 * The pre-requisite of the {@link PrimaryAction} object is the task expressed in
 * lambda must always return a value. After task completion, this value will
 * then be available for retrieval or optionally forwarded to the next action.
 * For example:
 * <pre>
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
 * underlying thread pool {@link ManagedPromisePoolExecutor} will manage the processes.
 * The {@link TaskAction} object is useful for scenarios where the value is not
 * expected from the task, but what about situations where the value has to
 * undergo some kind of transformation? Perhaps from one type to another or
 * the value is required for a calculation resulting in a new value.
 * This is the role of the {@link TransmuteAction} action object. Example of
 * usage is shown below:
 * <pre>
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
 *          if (e != null)
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
public interface Promise<T> {

    Consumer<Throwable> DEFAULT_EXCEPTION_HANDLER = e -> {throw new RuntimeException(e);};

    /**
     * Returns the current state of this promise object.
     * <p>
     * The transition of states is as follows:
     * <pre>
     *     PENDING --&gt; FULFILLED | REJECTED
     * </pre>
     * Promise objects always start with {@link States#PENDING}. Essentially
     * means no action has been undertaken by this object yet, but the moment an
     * action object is processed, the state will either be {@link States#FULFILLED}
     * or {@link States#REJECTED}, depending on the outcome of the action.
     */
    enum States { PENDING, FULFILLED, REJECTED }

    /**
     * Awaits for {@link Promise} thread to complete.
     * <p>
     * Like {@link Promise#getResult()} and {@link Promise#handle(Consumer)}, this
     * command will block in the main/current thread.
     *
     * @return new instance of this object.
     */
    Promise<T> await();

    /**
     * Having completed the previous {@code promise}, now execute {@link Consumer}
     * action asynchronously, and return a new {@link Promise} object to manage the
     * asynchronous task and the underlying {@link CompletableFuture} future.
     *
     * @param action the action being processed asynchronously.
     * @return a new {@link Promise} object to manage the {@link TaskAction} action
     * object.
     */
    default Promise<T> thenAccept(final Consumer<? super T> action) {
        Objects.requireNonNull(action);
        return then(TaskAction.of(action));
    }

    /**
     * Having completed the previous {@code promise}, now execute {@link Function}
     * action asynchronously, and return a new {@link Promise} object to manage the
     * asynchronous task and the underlying {@link CompletableFuture} future.
     *
     * @param function the action being processed asynchronously.
     * @param <R> resultant type of applying the {@code function}
     * @return a new {@link Promise} object to manage the {@link TaskAction} action
     * object.
     */
    default <R> Promise<R> thenApply(final Function<? super T, ? extends R> function) {
        Objects.requireNonNull(function);
        return then(TransmuteAction.of(function));
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
     Promise<T> then(final TaskAction<T> action);

    /**
     * Having completed the previous action, now execute {@link TransmuteAction}
     * action asynchronously, and return a new {@link Promise} object to manage
     * the {@link TaskAction} object and the underlying {@link CompletableFuture}
     * future.
     *
     * @param action the action being processed asynchronously.
     * @param <R> resultant type of transmute action.
     * @return a new {@link Promise} object to manage the {@link TransmuteAction}
     * action object.
     */
    <R> Promise<R> then(final TransmuteAction<T,R> action);

    /**
     * @return the current {@link AbstractAction} object being managed by this
     * {@link Promise} object.
     */
    Action<T> getAction();

    /**
     * Returns the current state of this promise object.
     * <p>
     * The transition of states is as follows:
     * <pre>
     *     PENDING -&gt; FULFILLED | REJECTED
     * </pre>
     * Promise objects always start with {@link States#PENDING}. Essentially, no
     * action has been undertaken by this object yet, but the moment an action
     * object is processed, the state will either be {@link States#FULFILLED}
     * or {@link States#REJECTED} depending on the outcome of the action.
     *
     * @return enum {@code States}, the current state of this {@link Promise}
     * object.
     */
    States getState();

    /**
     * Returns a unique identity of this {@link Promise} object.
     * <p>
     * This is particularly useful for {@code HashMap} or other collection
     * objects that require a mechanism to uniquely identify {@link Promise}
     * objects. The {@code Promise.toString()} method also reports {@code identity}
     * making it useful for debugging purposes.
     *
     * @return the unique {@code identity} of the {@link Promise} object.
     */
    String getIdentity();

    /**
     * Returns the result of the this {@link Promise} action processing.
     * <p>
     * This is a blocking call. It will wait for all of the asynchronous
     * processes to complete that relate to this {@link Promise} object, the
     * result of which is described in the returned {@link Maybe} object.
     * <p>
     * If an exception is thrown in any of the processes, {@link Maybe} object
     * will be empty.
     *
     * @return a {@link Maybe} object the describes the result.
     */
    Maybe<T> getResult();

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
    Promise<T> handle(final Consumer<Throwable> handler);
}
