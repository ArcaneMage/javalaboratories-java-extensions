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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This action object consumes the output value from the previous task and
 * processes it, but does not forward the value to subsequent tasks.
 * <p>
 * Use the factory methods {@code TaskAction#of(...)} to create the instance of
 * the {@link TaskAction}.
 * <p>
 * Invoking {@link TaskAction} objects requires the use of the
 * {@link Promise#then(TaskAction)} method
 * <p>
 * It is not compulsory to provide a {@code completionHandler}, hence the
 * multiple factory methods. However, this action does expect the task to
 * return a resultant object.
 *
 * @param <T> Type of the returned object of the task.
 * @see Promise
 * @see PrimaryAction
 * @see TransmuteAction
 */
@EqualsAndHashCode(callSuper=false)
@SuppressWarnings("WeakerAccess")
public final class TaskAction<T> extends AbstractAction<Void> {
    private final Consumer<T> task;

    /**
     * Constructor to setup internal handlers.
     * <p>
     * Recommended to use factory methods for creation.
     * @param task main task handler that will be executed asynchronously.
     * @throws NullPointerException if task parameter is null.
     */
    private TaskAction(final Consumer<T> task) {
        this(task, null);
    }

    /**
     * Constructor to setup internal handlers.
     * <p>
     * Recommended to use factory methods for creation.
     * @param task main task handler that will be executed asynchronously.
     * @param completionHandler to handle task completion -- this is optional.
     * @throws NullPointerException if task parameter is null.
     */
    private TaskAction(final Consumer<T> task, final BiConsumer<Void,Throwable> completionHandler) {
        super(completionHandler);
        this.task = task;
    }

    /**
     * Factory method to construct this {@link TaskAction} object,
     * <p>
     * @param task main task handler that will be executed asynchronously.
     * @throws NullPointerException if task parameter is null.
     */
    public static <T> TaskAction<T> of(Consumer<T> task) {
        return new TaskAction<>(task);
    }

    /**
     * Factory method to construct this {@link TaskAction} object,
     * <p>
     * @param task main task handler that will be executed asynchronously.
     * @param completionHandler to handle task completion -- this is optional.
     * @throws NullPointerException if task parameter is null.
     */
    public static <T> TaskAction<T> of(Consumer<T> task, BiConsumer<Void,Throwable> completionHandler) {
        return new TaskAction<>(task, completionHandler);
    }

    /**
     * @return this {@link TaskAction} task handler.
     */
    public Nullable<Consumer<T>> getTask() {
        return Nullable.ofNullable(task);
    }
}
