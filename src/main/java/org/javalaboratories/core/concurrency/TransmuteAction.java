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
import org.javalaboratories.core.util.Generics;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This action object consumes the output value from the previous task and
 * transform it, and forwards the transformed value to subsequent tasks.
 * <p>
 * Use the factory methods {@code Transmute#of(...)} to create the instance of the
 * {@link TransmuteAction}.
 * <p>
 * Invoking {@link TransmuteAction} objects requires the use of the
 * {@link Promise#then(TransmuteAction)} method
 * <p>
 * It is not compulsory to provide a {@code completionHandler}, hence the
 * multiple factory methods. However, this action does expect the task to
 * return a resultant object.
 *
 * @param <T> Type of the object to be processed by the task.
 * @param <R> Type of the transmuted object.
 * @see Promise
 * @see PrimaryAction
 * @see TaskAction
 */
@EqualsAndHashCode(callSuper=false)
public final class TransmuteAction<T,R> extends AbstractAction<R> {
    private final Function<? super T,? extends R> task;

    /**
     * Constructor to setup internal handlers.
     * <p>
     * Recommended to use factory methods for creation.
     * @param task main transmute task handler that will be executed
     *             asynchronously.
     * @throws NullPointerException if task parameter is null.
     */
    private TransmuteAction(final Function<? super T,? extends R> task) {
        this(task, null);
    }

    /**
     * Constructor to setup internal handlers.
     * <p>
     * Recommended to use factory methods for creation.
     * @param task main transmute task handler that will be executed
     *             asynchronously.
     * @param completionHandler to handle task completion -- this is optional.
     * @throws NullPointerException if task parameter is null.
     */
    private TransmuteAction(final Function<? super T,? extends R> task,
                            final BiConsumer<? super R,Throwable> completionHandler) {
        super(completionHandler);
        this.task = Objects.requireNonNull(task,"No task?");
    }

    /**
     * Factory method to construct this {@link TaskAction} object,
     * <p>
     * @param task main transmute task handler that will be executed
     *             asynchronously.
     * @param <T> type of object to be transmuted.
     * @param <R> type of object that has undergone transmutation.
     * @return an instance of the {@code TransmuteAction}.
     * @throws NullPointerException if task parameter is null.
     */
    public static <T,R> TransmuteAction<T,R> of(final Function<? super T,? extends R> task) {
        return new TransmuteAction<>(task);
    }

    /**
     * Factory method to construct this {@link TaskAction} object,
     * <p>
     * @param task main transmute task handler that will be executed
     *             asynchronously.
     * @param completionHandler to handle task completion -- this is optional.
     * @param <T> type of object to be transmuted.
     * @param <R> type of object that has undergone transmutation.
     * @return an instance of the {@code TransmuteAction}.
     * @throws NullPointerException if task parameter is null.
     */
    public static <T,R> TransmuteAction<T,R> of(final Function<? super T,? extends R> task,
                                                final BiConsumer<R,Throwable> completionHandler) {
        return new TransmuteAction<>(task,completionHandler);
    }

    /**
     * @return this {@link TaskAction} task handler.
     */
    public Maybe<Function<T,R>> getTask() {
        return Generics.unchecked(Maybe.ofNullable(task));
    }
}
