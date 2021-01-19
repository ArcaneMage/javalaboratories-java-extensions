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
 * Utility class of the pre-defined {@link Event} types.
 * <p>
 * These are useful to minimise boiler-plate code, but if additional or
 * alternative event types are necessary, implement the user-defined
 * {@link Event} object(s) by subclassing {@link AbstractEvent} class.
 *
 * @see AbstractEvent
 * @see Event
 */
public final class CommonEvents implements EventSource {

    private CommonEvents() {}

    /**
     * Useful for scenarios where you expect the {@link EventSubscriber} objects
     * to perform an action based on the current {@link EventPublisher} state.
     */
    public static final Event ACTION_EVENT = new ActionEvent(EVENT_SOURCE_UNKNOWN);

    /**
     * This is a generic {@link Event} in that it is useful where the {@link Event}
     * can represent any change in state of the {@link EventPublisher}, or perhaps
     * it can be considered a super type of all {@link Event} objects.
     */
    public static final Event ANY_EVENT = new AnyEvent(EVENT_SOURCE_UNKNOWN);

    /**
     * Notifies the {@link EventSubscriber} objects of the state change of the
     * {@link EventPublisher}, but the {@code subscriber} would need to
     * understand the context of the notification.
     */
    public static final Event NOTIFY_EVENT = new NotifyEvent(EVENT_SOURCE_UNKNOWN);

    private static class ActionEvent extends AbstractEvent {
        public ActionEvent(EventSource source) {
            super(source);
        }
    }

    private static class AnyEvent extends AbstractEvent {
        public AnyEvent(EventSource source) {
            super(source);
        }
    }

    private static class NotifyEvent extends AbstractEvent {
        public NotifyEvent(EventSource source) {
            super(source);
        }
    }
}
