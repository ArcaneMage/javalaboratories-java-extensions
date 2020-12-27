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

import org.javalaboratories.core.Maybe;

/**
 * Exception raised by {@link EventPublisher} and {@link Event} objects.
 * <p>
 * A reference to the triggered {@link Event} object may be provided
 * for diagnosis.
 */
public class EventException extends RuntimeException {

    private final Event event;

    /**
     * Constructs an instance of this {@code exception} object, but the
     * {@link Event} is unknown at the time of the exception.
     * <p>
     * @param message the message describing the exception.
     */
    public EventException(String message) {
        this(message,null);
    }

    /**
     * Constructs an instance of this {@code exception} object, but the
     * {@link Event} is unknown at the time of the exception.
     * <p>
     * @param message the message describing the exception.
     * @param cause the underlying cause of the exception.
     */
    public EventException(String message, Throwable cause) {
        this(message,cause,null);
    }

    /**
     * Constructs an instance of this {@code exception} object.
     * <p>
     * @param message the message describing the exception.
     * @param cause the underlying cause of the exception.
     * @param event the current {@code event} being processed.
     */
    public EventException(String message, Throwable cause, Event event) {
        super(message,cause);
        this.event = event;
    }

    /**
     * @return the current {@code event} as a {@link Maybe} triggered at
     * the time of the exception.
     */
    public Maybe<Event> getEvent() {
        return Maybe.ofNullable(event);
    }
}
