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

import org.javalaboratories.core.event.Event;
import org.javalaboratories.core.event.EventSubscriber;

/**
 * Implement this interface to receive events from the {@link Promise} object.
 *
 * {@link Promise} object asynchronously notifies events to registered
 * {@code listeners/subscribers} immediately after the {@link Promise}
 * transitions to the {@link Promise.States#FULFILLED} state.
 * Notification of {@code subscribers} is performed asynchronously to avoid
 * blocking the main/current thread. This is also means it is possible to
 * retrieve the result of the asynchronous computation <b>before all</b> the
 * {@link PromiseEventSubscriber} objects are notified of the result with
 * current {@link Event}. This is a reasonable and deliberate design: it is
 * unreasonable to block the {@link Promise#await}, {@link Promise#handle} and
 * {@link Promise#getResult()} methods until all {@link PromiseEventSubscriber}
 * have been notified. It is not the role of {@link Promise} object to be
 * dependent on {@code subscribers}, but it does guarantee to eventually
 * inform all {@code subscribers} of the {@link Event}.
 *
 * @see Promise
 * @see Promises
 * @see AsyncPromiseTaskPublisher
 */
public interface PromiseEventSubscriber extends EventSubscriber<PromiseEvent<?>> {}
