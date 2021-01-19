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

import lombok.Value;

/**
 * This represents the current state of the {@link Promise} object delivered to
 * all registered {@link PromiseEventSubscriber} objects.
 * <p>
 * For example, the {@code state} could be the resultant value of some asynchronous
 * {@link Action} to be forwarded to the all registered {@code listeners/subscribers}
 * after the {@link Promise} transitions to the {@link Promise.States#FULFILLED}
 * state.
 *
 * @param <T> Type of resultant value returned from asynchronous {@link Action}
 * @see Promises
 * @see PromiseEventSubscriber
 */
@Value
public class EventState<T> {
    T value;

    /**
     * Constructs this {@code event} state.
     * <p>
     * @param value returned from asynchronous {@link Action}.
     */
    public EventState(final T value) {
        this.value = value;
    }
}
