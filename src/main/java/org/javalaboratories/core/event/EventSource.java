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
 * A marker interface primarily implemented by {@link EventPublisher} objects or
 * the object that encapsulates the {@code publisher}.
 * <p>
 * {@link EventSubscriber} objects will receive an implementation of the
 * {@code EventSource} to identify the origins of the {@link Event}.
 */
public interface EventSource {

    /**
     * Generally used for scenarios where the {@link EventSource} object is
     * not determinable.
     */
    EventSource EVENT_SOURCE_UNKNOWN = new EventSource() {};
}
