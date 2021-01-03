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

import lombok.AllArgsConstructor;
import org.javalaboratories.core.Maybe;
import org.javalaboratories.util.Generics;

import java.util.function.BiConsumer;

/**
 * All action objects inherit from this abstract class and therefore the
 * implement the {@link Action} interface.
 * <p>
 * For more information on the role of this abstract class, refer to the {@link Action}
 * interface.
 * <p>
 * Action objects encapsulate task and completion handlers that are executed
 * within their own thread context.
 *
 * @param <T> Type of the object returned from the task.
 * @see AbstractAction
 * @see PrimaryAction
 * @see TaskAction
 * @see TransmuteAction
 */
@AllArgsConstructor()
public abstract class AbstractAction<T> implements Action<T>  {
    private final BiConsumer<? super T,Throwable> completionHandler;

    @Override
    public Maybe<BiConsumer<T,Throwable>> getCompletionHandler() {
        return Generics.unchecked(Maybe.ofNullable(completionHandler));
    }
}