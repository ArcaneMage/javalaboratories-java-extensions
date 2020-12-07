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

/**
 * All events fired from the {@link EventPublisher} implement this interface.
 * <p>
 * This object supplies information to the {@link EventSubscriber} objects about
 * the current state change of the {@code publisher} object, upon which the
 * {@code subscriber} would then perform an action.
 * <p>
 * As a guide to properly implement an {code Event} object, it is recommended to
 * ensure that the {@code equals} and {@code hashCode} methods are correctly
 * implemented; preferably, the event object is immutable; each event type has a
 * unique identifier. These considerations are taken into account in the
 * {@link AbstractEvent}, so would be easier to inherit from the class for
 * expediency.
 *
 * @see AbstractEvent
 * @see EventPublisher
 * @see EventSubscriber
 */
public interface Event extends Serializable {

    /**
     * @return a unique event type identifier.
     */
    String getEventId();

    /**
     * @return the source of the event.
     */
    EventSource getSource();
}
