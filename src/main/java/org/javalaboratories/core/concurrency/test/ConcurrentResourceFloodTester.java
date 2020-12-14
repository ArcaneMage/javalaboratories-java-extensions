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
 * Implementors of this interface leverage {@code threads} with which to
 * {@code target} the resource concurrently with requests.
 * <p>
 * How the threads are managed is completely up to the concrete implementation
 * -- no guidelines are provided.
 *
 * @param <T> Type of object returned from the request sent to the
 * {@code resource}
 */
public interface ConcurrentResourceFloodTester<T> extends ResourceFloodTester<T> {

    /**
     * This is the number of repeated requests to the {@code resource}.
     * <p>
     * The repetition can occur from within the request thread but this is up
     * to the implementer.
     *
     * @return the number of configured repetition.
     */
    int getIterations();

    /**
     * This is the number of threads or {@code flood workers} actively involved
     * in sending requests to the {@link Target} or {@code resource}.
     * <p>
     * @return number of threads.
     */
    int getThreads();
}
