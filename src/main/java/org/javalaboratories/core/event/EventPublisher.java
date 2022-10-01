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
public interface EventPublisher<T extends Event> {
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

    /**
     * Registers the {@link EventSubscriber} with this {@link EventPublisher}.
     * <p>
     * The {@code captureEvents} parameter explicitly state the events the
     * {@link EventSubscriber} object is particularly interested in. Each
     * {@link Event} must be uniquely identifiable -- ensure the {@code equals
     * and hashCode} of the {@code Event} are implemented.
     * <p>
     * Note: It is not possible to subscribe the same subscriber multiple times.
     *
     * @param subscriber the {@link EventSubscriber} object to register.
     * @throws NullPointerException if {@code subscriber} is null.
     * @throws IllegalArgumentException if {@code captureEvents} is null or less
     * than 1. Multiple subscription of the same subscriber is not possible.
     */
    void subscribe(final EventSubscriber<T> subscriber);

    /**
     * Unregisters the {@link EventSubscriber} from this {@link EventPublisher}.
     * <p>
     * Unregistering the {@link EventSubscriber} means that the subscriber will
     * no longer receive notifications from the {@link EventPublisher}.
     *
     * @param subscriber the {@link EventSubscriber} object to unregister.
     * @return {@code true} for successful removal, {@code false} for unrecognised
     *         or unknown {@code subscriber}
     * @throws NullPointerException if {@code subscriber} is null.
     */
    boolean unsubscribe(final EventSubscriber<T> subscriber);

    /**
     * @return number of subscribers registered with this {@code publisher}
     */
    int subscribers();
}
