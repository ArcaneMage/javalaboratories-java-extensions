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

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * All action objects managed by {@link Promise} objects implement this
 * interface.
 * <p>
 * They are considered to be immutable and understand how to perform tasks in
 * their own thread context. When the action <b>task</b> is completed
 * successfully or exceptionally, the {@link Action#getCompletionHandler()} method
 * is invoked, if the action implementation requires it. Whereupon the resultant
 * value or the exception object of the task is presented for processing. This
 * occurs in the current thread context.
 *
 * @param <T> Type of resultant value returned from task performed asynchronously
 * @see AbstractAction
 * @see PrimaryAction
 * @see TaskAction
 * @see TransmuteAction
 */
public interface Action<T> extends CompletableFuture.AsynchronousCompletionTask {

    /**
     * Returns the completion handler to the {@link Promise} object responsible
     * for handling the outcome of the asynchronous task.
     * <p>
     * If the action does not return a completion handler (optional), it will
     * be ignored.
     * <p>
     * @return a {@link Maybe} object encapsulating the completion handler.
     */
    Maybe<BiConsumer<T,Throwable>> getCompletionHandler();
}
