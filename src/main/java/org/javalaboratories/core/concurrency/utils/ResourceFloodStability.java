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
package org.javalaboratories.core.concurrency.utils;

import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The object that implements this interface can target an object or a resource
 * with multiple threads at the same time.
 * <p>
 * The purpose of this is to test the {@code target's} stability under load,
 * revealing performance issues and/or thread-safety concerns. Use these
 * {@link ResourceFloodStability} objects in unit tests, then evaluate the
 * {@code target's} state. It is not the role of this object to evaluate the
 * state of the {@code target}, but to simply pummel it with requests.
 *
 * @param <T> Type of value(s) returned from the target.
 * @see Floodgate
 * @see Torrent
 */
public interface ResourceFloodStability<T> {

    /**
     * These are several states that represents the current status of this
     * object.
     * <p>
     * Below illustrates the state machine of this object:
     * <pre>
     *     {@code
     *          CLOSED --> open() --> OPENED --> flood() --> CLOSED --> FLOODED
     *     }
     * </pre>
     * Initially, {@link ResourceFloodStability} objects starts with the
     * {@code CLOSED} state.
     */
    enum States {CLOSED,OPENED,FLOODED}

    /**
     * Cleanup allocated resources pertaining to this {@link ResourceFloodStability}.
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
     * This method must be called ahead of the {@link ResourceFloodStability#flood()},
     * and it can only be used once. It is probable that this object requires
     * access to resources and/or I/O components in order to facilitate testing
     * of the test resource. Successive calls will result in the exception
     * {@link  IllegalStateException} being thrown.
     *
     * @return {@code true} if opened successfully, and the state of this object
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
     * must release all allocated resources pertaining to the requests.
     *
     * @return results to calling thread.
     * @throws IllegalStateException if state is not in {@code OPENED} state.
     */
    T flood();

    /**
     * These are several states that represents the current status of this object.
     * <p>
     * Below illustrates the state of machine of this object:
     * <pre>
     *     {@code
     *          CLOSED --> open() --> OPENED --> flood() --> CLOSED --> FLOODED
     *     }
     * </pre>
     * Initially, {@link ResourceFloodStability} objects starts with the
     * {@code CLOSED} state.
     *
     * @return current state of this object.
     */
    States getState();

    /**
     * The {@link ResourceFloodStability} has a {@code target}, which has a specific
     * {@code aspect}.
     * <p>
     * Generally speaking an {@code aspect/resource} can be viewed as a specific
     * method of an object or an API of a web service, and so it is expected that
     * the {@link Target} under test could have several {@code resources} or
     * {@code aspect} but all with unique identifiable names.
     * <p>
     * Implementors of this interface are encouraged to report {@code target}
     * information to enable easy interpretation of {@code flood} results.
     * <p>
     * Some {@link ResourceFloodStability} objects may target multiple
     * targets and it is reasonable to return {@link Target} to represent this.
     * Refer to {@link Target#getIndeterminateTarget()} method.
     *
     * @return target object
     */
    Target getTarget();

    /**
     * The {@link ResourceFloodStability} has a {@code target}, which has a specific
     * {@code aspect}.
     * <p>
     * Generally speaking an {@code aspect} can be viewed as a specific method
     * of an object or an API of a web service, and so it is expected that the
     * resource under test could have several targets but all with unique
     * identifiable names. It is encouraged to report this information in any logs
     * generated for tracking and monitoring purposes.
     * <p>
     * Additionally, {@link ResourceFloodStability} may mark the target as
     * {@code UNSTABLE} if it encounters errors whilst flooding the {@code target}
     * with requests.
     * <p>
     * {@link Target} will uniquely generate a name of the target based on the
     * {@link Class} and unique integer value. The integer value will increment
     * by one and will repeat, but it is unique by class name.
     * <p>
     * This object is thread-safe.
     *
     * @see AbstractResourceFloodStability
     */
    @Getter
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @ToString
    final class Target {

        public enum Stability {STABLE,UNSTABLE}

        @Getter(AccessLevel.NONE)
        private static final Set<String> index = new HashSet<>();

        @EqualsAndHashCode.Include
        private final String name;
        private volatile Stability stability;

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
            this(clazz,null);
        }

        /**
         * Constructs an instance of this {@link Target}.
         * <p>
         * Uniquely creates {@link Target} identifier for tracking and reporting
         * purposes.
         * <p>
         * A {@code tag} provides a meaningful name of the {@code resource}
         * under test. It will form part of the {@code target} name. This would be
         * useful for reporting purposes but it is not essential.
         *
         * @param clazz class object on which unique {@code target} name or
         *              identifier is based.
         * @param tag   a tag is a meaningful name that describes the {@code
         * resource}
         *
         * @param <U> Type of class.
         */
        public <U> Target(final Class<U> clazz, final String tag) {
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
            name = tag == null ? value : String.format("%s-(%s)",value,tag);
            stability = Stability.STABLE;
        }

        /**
         * {@link ResourceFloodStability} may mark {@link Target} object as
         * {@code UNSTABLE} if it encounters errors during the {@code flood}.
         * <p>
         * If the {@link Target} is {@code UNSTABLE} prior to the {@code flood},
         * the target must not be subjected to requests.
         * <p>
         * This method is impotent in that once used, reverting the state
         * is no longer possible.
         */
        public void unstable() {
            if (stability == Stability.STABLE) {
                stability = Stability.UNSTABLE;
            }
        }

        /**
         * Used to indicate the {@link Target} of the {@link ResourceFloodStability} is
         * not determinable.
         * @return an instance of {@link Target} which is indeterminate.
         */
        public static Target getIndeterminateTarget() {
            return new Target(IndeterminateTarget.class);
        }

        private String transform(String s) {
            String[] p = s.split("[{\\-}]");
            return(String.format("{%s-%03d}",p[1],Integer.parseInt(p[2])+1));
        }

        @Value
        private static class IndeterminateTarget { }
    }
}
