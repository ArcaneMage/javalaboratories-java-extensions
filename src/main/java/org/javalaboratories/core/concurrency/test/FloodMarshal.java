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
package org.javalaboratories.core.concurrency.test;

/**
 * Implementation of this interface has the ability to control timing of flood
 * thread workers used by {@link Floodgate} and {@link Torrent} objects.
 * <p>
 * Flood thread workers will only commence activity on the authorisation of this
 * object.
 *
 * @see ExternalFloodMarshal
 */
public interface FloodMarshal {
    /**
     * Informs the flood workers to stand-by and wait for orders to open the
     * gate.
     *
     * @throws InterruptedException if {@code flood worker} is suddenly interrupted.
     */
    void halt() throws InterruptedException;

    /**
     * Orders the flood workers to open the gate.
     */
    void flood();
}
