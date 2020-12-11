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
 * The object that implements this interface can target an object or a resource
 * with multiple threads at the same time.
 * <p>
 * The purpose of this is to test the {@code target's} stability under load,
 * revealing performance issues and/or thread-safety concerns. Use these
 * {@link MultithreadedFloodTester} objects in unit tests, then evaluate the
 * {@code target's} state. It is not the role of this object to evaluate the
 * {@code target's} state, but to simply pummel it with requests.
 *
 * @param <T> Type of value(s) returned from the target.
 * @see Floodgate
 * @see Torrent
 */
public interface MultithreadedFloodTester<T> {

    /**
     * These are three states that represents the current status of this
     * object.
     * <p>
     * Below illustrates the state of machine of this object:
     * <p>
     * <prep>
     *     {@code
     *          CLOSED --> open() --> OPENED --> flood() --> FLOODED
     *     }
     * </prep>
     * Initially, {@link MultithreadedFloodTester} objects starts with the
     * {@code CLOSED} state.
     */
    enum States {CLOSED,OPENED,FLOODED}

    /**
     * Initialises the {@code target} or {@code resource}, preparing it for the
     * {@code flood}.
     * <p>
     * This method must be called ahead of the {@link MultithreadedFloodTester#flood()},
     * and it can only be used once. Successive calls will result in the exception
     * {@link  IllegalStateException} being thrown.
     * <p>>
     * @return {@code true} if opened successfully, and the state of the {@code tester}
     * will transition from {@code CLOSED} to {@code OPENED} state.
     * @throws IllegalStateException if the state is not in {@code CLOSED} state.
     */
    boolean open();

    /**
     * Floods the target object or resource with requests to assess its stability
     * and/or performance.
     * <p>
     * This method will block waiting for the conclusion of the requests, after
     * the state of this object will transition from {@code OPENED} to
     * {@code FLOODED} and will not be re-runnable.
     *
     * @return results to calling thread.
     * @throws IllegalStateException if state is not in {@code OPENED} state.
     */
    T flood();

    /**
     * These are three states that represents the current status of this object.
     * <p>
     * Below illustrates the state of machine of this object:
     * <p>
     * <prep>
     *     {@code
     *          CLOSED --> open() --> OPENED --> flood() --> FLOODED
     *     }
     * </prep>
     * Initially, {@link MultithreadedFloodTester} objects starts with the
     * {@code CLOSED} state.
     * <p>
     * @return current state of this object.
     */
    States getState();
}
