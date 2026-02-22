/*
 * Copyright 2026 Kevin Henry
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
package org.javalaboratories.core.json.events;

import lombok.Getter;
import org.javalaboratories.core.event.EventSource;

/**
 * This event is fired/published when a mapping/schema has been evaluated
 * successfully.
 */
@Getter
public final class JsonPropertyTransformationEvent extends TransformerEvent {

    private final String target;
    private final String value;
    private final String mapping;

    /**
     * Constructs this event with {@code source}, {@code mapping} and {@code jsonValue}
     *
     * @param source source of the event, normally the publisher
     * @param mapping mapping/schema that dictates final JSON structure
     * @param value transformed JSON value
     */
    public JsonPropertyTransformationEvent(final EventSource source, final String target, final String mapping, final String value) {
        super(source);
        this.target = target;
        this.value = value;
        this.mapping = mapping;
    }
}
