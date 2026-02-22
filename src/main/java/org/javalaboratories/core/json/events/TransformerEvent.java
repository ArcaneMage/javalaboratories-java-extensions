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

import org.javalaboratories.core.event.AbstractEvent;
import org.javalaboratories.core.event.EventSource;

/**
 * This is parent class of the {@link org.javalaboratories.core.json.JsonTransformer}
 * events.
 * <p>
 * Implementors of the {@code JsonTransformer} interface, if they support
 * event publishing, should consider publishing {@link JsonPropertyTransformationEvent},
 * {@link BeforeTransformationEvent} and {@link AfterTransformationEvent}.
 */
public abstract sealed class TransformerEvent extends AbstractEvent
        permits AfterTransformationEvent, BeforeTransformationEvent, JsonPropertyTransformationEvent {

    /**
     * Constructs the {@code transformer} event.
     *
     * @param source source of the event, normally the publisher
     */
    public TransformerEvent(final EventSource source) {
        super(source);
    }
}
