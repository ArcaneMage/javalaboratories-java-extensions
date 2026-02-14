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

/**
 * An object that implements this interface can notify its subscribed
 * dependencies with events.
 * <p>
 * This interface is the {@code Subject} component of the Observer Design
 * pattern as described in the "Design Patterns, Elements of Reusable Object-
 * Oriented Software (Gang-of-Four)" book. Therefore, it is a container of
 * of {@code Observers}, {@link EventSubscriber} objects. When an {@link Event},
 * in {@link EventPublisher} has occurred, it will then be published to all
 * {@link EventSubscriber} objects who are interested in the {@code event}.
 * Upon subscribing an {@link EventSubscriber}, it is necessary to specify
 * exactly which {@code events} to which the {@code subscriber} wants to
 * listen.
 * <p>
 * Use the {@link EventPublisher#subscribe(EventSubscriber)} method
 * to register an {@link EventSubscriber} object. The
 * {@link EventPublisher#unsubscribe(EventSubscriber)} method
 * unregisters the {@link EventSubscriber} object from the {@link EventPublisher}.
 *
 * @param <T> type of event
 * @see Event
 * @see EventBroadcaster
 * @see EventSubscriber
 */
public interface EventPublisher<T extends Event, U extends EventSubscriber<T>> extends EventSubscribable<U> {
    /**
     * Publish an {@link Event} to interested {@link EventSubscriber} objects
     * when {@link EventPublisher} or the owning component which encapsulates
     * the {@code EventPublisher} state changes.
     * <p>
     * If the {@code subscriber} has not explicitly stated that it's
     * interested in a particular {@link Event}, it will not receive a
     * notification of that {@link Event}.
     * <p>
     * In situations where {@link EventSubscriber} throws an exception, the
     * {@link EventPublisher} object will consider the {@code subscriber} as
     * unstable/toxic and will automatically cancel its subscription, and then
     * continue notifying outstanding {@code subscribers}.
     *
     * @param event the {@link Event} object with which to publish to recipients.
     * @throws NullPointerException if {@code event} is null.
     */
     void publish(final T event);
}
