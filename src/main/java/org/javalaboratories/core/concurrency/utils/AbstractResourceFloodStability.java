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

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * Partially implements the {@link ResourceFloodStability} interface, providing
 * {@link ResourceFloodStability.Target} object.
 * <p>
 * It is recommended to extend this class to provide behaviour for flooding
 * targets with requests. It formally registers the {@code target} that is to
 * undergo the flood test. All {@link ResourceFloodStability} objects should
 * inherit from this class, calling its constructor.
 *
 * @param <T> Type of return value from targeted {@code resource} under test.
 * @see ResourceFloodStability.Target
 * @see AbstractConcurrentResourceFloodStability
 * @see Floodgate
 * @see Torrent
 */
@Getter
@EqualsAndHashCode
public abstract class AbstractResourceFloodStability<T> implements ResourceFloodStability<T> {

    private final Target target;

    /**
     * Default constructor of this {@link ResourceFloodStability} object.
     * <p>
     * Use this constructor when targeting multiple targets.
     *
     * @see ResourceFloodStability.Target
     */
    public AbstractResourceFloodStability() {
        target = Target.getIndeterminateTarget();
    }

    /**
     * Constructs this {@link ResourceFloodStability} object.
     * <p>
     * Use this constructor when targeting a specific aspect of the target,
     * for example web-server API or a particular method of the target
     * object and it is encouraged to report the {@link
     * ResourceFloodStability.Target} to clients of this object.
     * <p>
     * A {@code tag} provides a meaningful name of the {@code resource}
     * under test. It will form part of the {@code target} name. This would be
     * useful for reporting purposes but it is not essential.
     *
     * @param clazz class object type of {@code resource} under test.
     * @param tag   a tag is a meaningful name that describes the
     * {@code resource}
     * @param <U> type of resource object.
     *
     * @throws NullPointerException if {@code clazz} is null
     * @see ResourceFloodStability.Target
     */
    public <U> AbstractResourceFloodStability(final Class<U> clazz, final String tag) {
        Class<U> c = Objects.requireNonNull(clazz);
        target = new Target(c,tag);
    }

    /**
     * Returns a string with {@code target} name prepended to the {@code msg}
     * value.
     * <p>
     * Use the method when outputting/logging test information and/or current
     * status of this object.
     *
     * @param message message to prepend {@code target} details.
     * @return a string with {@code target} name.
     */
    protected String message(final String message) {
        return (target.getName()+": "+message);
    }
}
