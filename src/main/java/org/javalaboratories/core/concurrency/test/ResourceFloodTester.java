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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The object that implements this interface can target an object or a resource
 * with multiple threads at the same time.
 * <p>
 * The purpose of this is to test the {@code target's} stability under load,
 * revealing performance issues and/or thread-safety concerns. Use these
 * {@link ResourceFloodTester} objects in unit tests, then evaluate the
 * {@code target's} state. It is not the role of this object to evaluate the
 * state of the {@code target}, but to simply pummel it with requests.
 *
 * @param <T> Type of value(s) returned from the target.
 * @see Floodgate
 * @see Torrent
 */
public interface ResourceFloodTester<T> {

    /**
     * These are several states that represents the current status of this
     * object.
     * <p>
     * Below illustrates the state machine of this object:
     * <p>
     * <prep>
     *     {@code
     *          CLOSED --> open() --> OPENED --> flood() --> CLOSED --> FLOODED
     *     }
     * </prep>
     * Initially, {@link ResourceFloodTester} objects starts with the
     * {@code CLOSED} state.
     */
    enum States {CLOSED,OPENED,FLOODED}

    /**
     * Cleanup allocated resources pertaining to this {@link ResourceFloodTester}.
     * <p>
     * It is likely this object would need to allocate memory and/or I/O
     * components in order to target the resource under test. The purpose of this
     * method is to clean up the allocated resources or return them to the
     * operating system.
     *
     * @throws IllegalStateException if this object is not in an {@code OPENED}
     * state.
     */
    void close();

    /**
     * Initialises the {@code target} preparing it for the {@code flood}.
     * <p>
     * This method must be called ahead of the {@link ResourceFloodTester#flood()},
     * and it can only be used once. It is probable that this object requires
     * access to resources and/or I/O components in order to facilitate testing
     * of the test resource. Successive calls will result in the exception
     * {@link  IllegalStateException} being thrown.
     *
     * @return {@code true} if opened successfully, and the state of the {@code tester}
     * will transition from {@code CLOSED} to {@code OPENED} state.
     * @throws IllegalStateException if the state is not in {@code CLOSED} state.
     */
    boolean open();

    /**
     * Floods the {@code target} object with requests to assess its stability
     * and/or performance.
     * <p>
     * This method will block waiting for the conclusion of the requests, after
     * which the state of this object transitions from {@code OPENED} to
     * {@code CLOSED} and finally to {@code FLOODED} but will not be re-runnable.
     * <p>
     * It is important no matter the outcome of the {@code flood}, this method
     * must release all allocated resources pertaining to this requests.
     *
     * @return results to calling thread.
     * @throws IllegalStateException if state is not in {@code OPENED} state.
     */
    T flood();

    /**
     * These are several states that represents the current status of this object.
     * <p>
     * Below illustrates the state of machine of this object:
     * <p>
     * <prep>
     *     {@code
     *          CLOSED --> open() --> OPENED --> flood() --> CLOSED --> FLOODED
     *     }
     * </prep>
     * Initially, {@link ResourceFloodTester} objects starts with the
     * {@code CLOSED} state.
     * <p>
     * @return current state of this object.
     */
    States getState();

    /**
     * The {@link ResourceFloodTester} has a {@code target}, which has a specific
     * {@code aspect}.
     * <p>
     * Generally speaking an {@code aspect} can be viewed as a specific method
     * of an object or an API of a web service, and so it is expected that the
     * resource under test could have several targets but all with unique
     * identifiable names.
     * <p>
     * Implementors of this interface are encouraged to report {@code target}
     * information to enable easy interpretation of {@code flood} results.
     * <p>
     * @return target object
     */
    Target getTarget();

    /**
     * The {@link ResourceFloodTester} has a {@code target}, which has a specific
     * {@code aspect}.
     * <p>
     * Generally speaking an {@code aspect} can be viewed as a specific method
     * of an object or an API of a web service, and so it is expected that the
     * resource under test could have several targets but all with unique
     * identifiable names.
     * <p>
     * {@link Target} will uniquely generate a name of the target based on the
     * {@link Class} and unique integer value. The integer value will increment
     * by one and will repeat, but it is unique by class name.
     * <p>
     * This object is thread-safe.
     * <p>
     * @see AbstractResourceFloodTester
     */
    @Value
    class Target {
        @Getter(AccessLevel.NONE)
        static Set<String> index = new HashSet<>();

        String name;

        /**
         * Constructs an instance of this {@link Target}.
         * <p>
         * Uniquely creates {@link Target} identifier for tracking and reporting
         * purposes.
         *
         * @param clazz class object on which unique {@code target} name or
         *              identifier is based.
         * @param <U> Type of class.
         */
        public <U> Target(final Class<U> clazz) {
            Class<U> c = Objects.requireNonNull(clazz);
            String cname = c.getSimpleName();
            String value;
            synchronized(this) {
                value = index.stream()
                    .filter(s -> s.contains(cname))
                    .map(this::transform)
                    .findFirst()
                    .orElse("{"+cname+"-001}");
                index.removeIf(s -> s.contains(cname));
                index.add(value);
            }
            name = value;
        }

        private String transform(String s) {
            String[] p = s.split("[{\\-}]");
            return(String.format("{%s-%03d}",p[1],Integer.parseInt(p[2])+1));
        }
    }
}
