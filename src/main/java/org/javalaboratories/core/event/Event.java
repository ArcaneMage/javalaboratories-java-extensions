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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * All events fired from the {@link EventPublisher} implement this interface.
 * <p>
 * This object supplies information to the {@link EventSubscriber} objects about
 * the current state change of the {@code publisher} object, upon which the
 * {@code subscriber} would then perform an action.
 * <p>
 * As a guide to properly implement an {code Event} object: it is recommended to
 * ensure that the {@code equals}, {@code hashCode} and {@code clone} methods are
 * correctly implemented; preferably, the event object is immutable; each event
 * type has a unique identifier. These considerations are taken into account in the
 * {@link AbstractEvent}, so would be easier to inherit from that class for
 * expediency.
 *
 * @see AbstractEvent
 * @see EventPublisher
 * @see EventSubscriber
 */
public interface Event extends Serializable, Cloneable {

    /**
     * Assigns current {@link EventSource} to event object.
     * <p>
     * {@link EventSource} is the origin of the {@code event}, in other words
     * the {@code source} of the behaviour change that triggered the event. To
     * maintain immutability, a new instance of the {@link Event} object is
     * returned encapsulating the {@link EventSource}.
     *
     * @param source {@link EventSource} of the {@code event}
     * @return an {@link Event} object with new {@link EventSource}
     * @throws UnsupportedOperationException problem occurred attempting to
     * create a new instance of the object.
     */
    Event assign(final EventSource source);

    /**
     * @return a unique event type identifier.
     */
    String getEventId();

    /**
     * @return the source of the event.
     */
    EventSource getSource();

    /**
     * Returns {@code true} if any of the events matches this {@code event}.
     *
     * @param events vararg of events to match.
     * @return {@code true} if any of {@code events} matches this event.
     */
    default boolean isAny(Event... events) {
        Objects.requireNonNull(events);
        return Arrays.stream(events)
                .anyMatch(event -> !this.equals(event));
    }
}
