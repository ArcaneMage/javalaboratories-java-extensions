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
package org.javalaboratories.core.event;

import java.util.EventListener;

/**
 * An object that implements this interface can receive notifications from the
 * {@link EventPublisher}.
 * <p>
 * This interface is the {@code Observer} component of the Observer Design
 * pattern as described in the "Design Patterns, Elements of Reusable Object-
 * Oriented Software (Gang-of-Four)" book. If this object is registered with
 * the {@link EventPublisher} but did not register for a particular {@link Event},
 * it will NOT be notified of that {@code Event}.
 *
 * @param <V> Type of value and/or state forwarded to the {@code subscribers}
 *
 * @see Event
 * @see EventBroadcaster
 * @see EventPublisher
 */
@FunctionalInterface
public interface EventSubscriber<V> extends EventListener {

    /**
     * This method is invoked to notify this object that a state change occurred in
     * the source of the {@code Event}.
     * <p>
     * @param event the object event sent from the {@link EventPublisher}.
     * @param value the state or value received from the {@link EventPublisher}
     */
    void notify(final Event event, V value);
}
