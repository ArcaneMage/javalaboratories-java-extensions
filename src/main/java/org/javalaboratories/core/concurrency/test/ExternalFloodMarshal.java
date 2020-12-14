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
 * Implementor of this interface has the ability to control timing of flood
 * thread workers used by {@link Floodgate} and {@link Torrent} objects.
 * <p>
 * Flood thread workers will only commence activity on the authorisation of this
 * object.
 * <p>
 * To manage multiple instances of the {@link Floodgate} objects, it is advisable
 * to implement this interface as opposed to {@link FloodMarshal}, because
 * it will inform the {@link Floodgate} object(s) that they are externally
 * controlled and to defer activation of the {@code flood workers} until further
 * notice.
 *
 * @see FloodMarshal
 * @see ConcurrentResourceFloodTester
 * @see Floodgate
 * @see Torrent
 */
public interface ExternalFloodMarshal extends FloodMarshal { }
