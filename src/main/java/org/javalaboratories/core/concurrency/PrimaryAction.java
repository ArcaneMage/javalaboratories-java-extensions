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

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * This is the first or initial action required by the {@link Promise} object.
 * <p>
 * Use the factory methods {@code PrimaryAction#of(...)} to create the instance
 * of the {@link PrimaryAction}.
 * <p>
 * It is not compulsory to provide a {@code completionHandler}, hence the
 * multiple factory methods. However, this action does expect the task to
 * return a resultant object.
 *
 * @param <T> Type of the object to be returned from the task.
 * @see Promise
 * @see TaskAction
 * @see TransmuteAction
 */
@EqualsAndHashCode(callSuper=false)
public final class PrimaryAction<T> extends AbstractAction<T> {
    private final Supplier<T> task;

    /**
     * Constructor to setup internal handlers.
     * <p>
     * Recommended to use factory methods for creation.
     * @param task main task handler that will be executed asynchronously.
     * @throws NullPointerException if task parameter is null.
     */
    private PrimaryAction(final Supplier<T> task) {
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
    private PrimaryAction(final Supplier<T> task, final BiConsumer<T,Throwable> completionHandler) {
        super(completionHandler);
        this.task = Objects.requireNonNull(task);
    }

    /**
     * Factory method to construct this {@link PrimaryAction} object,
     * <p>
     * @param task main task handler that will be executed asynchronously.
     * @throws NullPointerException if task parameter is null.
     */
    public static <T> PrimaryAction<T> of(Supplier<T> task) {
        return new PrimaryAction<>(task);
    }

    /**
     * Factory method to construct this {@link PrimaryAction} object,
     * <p>
     * @param task main task handler that will be executed asynchronously.
     * @param completionHandler to handle task completion -- this is optional.
     * @throws NullPointerException if task parameter is null.
     */
    public static <T> PrimaryAction<T> of(Supplier<T> task, BiConsumer<T,Throwable> completionHandler) {
        return new PrimaryAction<>(task, completionHandler);
    }

    /**
     * @return this {@link PrimaryAction} task handler.
     */
    public Maybe<Supplier<T>> getTask() {
        return Maybe.ofNullable(task);
    }
}
