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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.EventObject;
import java.util.stream.Collectors;

import static org.javalaboratories.core.event.EventSource.EVENT_SOURCE_UNKNOWN;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public abstract class AbstractEvent extends EventObject implements Event {

    @EqualsAndHashCode.Include
    private final String eventId;

    public AbstractEvent() {
        this(EVENT_SOURCE_UNKNOWN);
    }

    public AbstractEvent(EventSource source) {
        super(source);
        String name = this.getClass().getSimpleName();
        String identity = Arrays.stream(name.split(""))
                .skip(1)
                .map(s -> Character.isUpperCase(s.charAt(0)) ? "_"+s : s)
                .map(String::toUpperCase)
                .collect(Collectors.joining("",name.charAt(0)+"",""));
        this.eventId = String.format("{%s}",identity);
    }

    /**
     * Assigns current {@link EventSource} to this event object.
     * <p>
     * {@link EventSource} is the origin of the {@code event}, in other words
     * the {@code source} of the behaviour change that triggered the event. Only
     * the {@link EventPublisher} will have access to this method.
     *
     * @param source {@link EventSource} of the {@code event}.
     */
    void source(final EventSource source) {
        this.source = source;
    }

    @ToString.Include
    public EventSource getSource() {
        return (EventSource) super.getSource();
    }

    public String toString() {
        return "[eventId=" + this.getEventId() + ", source=" + this.getSource() + "]";
    }
}
