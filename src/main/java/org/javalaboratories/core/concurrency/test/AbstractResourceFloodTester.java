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

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * Partially implements the {@link ResourceFloodTester} interface, providing
 * {@link Target} object.
 * <p>
 * It is recommended to extend this class to provide behaviour for flooding
 * targets with requests.
 *
 * @param <T> Type of return value from targeted {@code resource} under test.
 * @see Target
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractResourceFloodTester<T> implements ResourceFloodTester<T> {

    @EqualsAndHashCode.Include
    private final Target target;

    /**
     * Default constructor of this {@link ResourceFloodTester} object.
     * <p>
     * Use this constructor when targeting multiple targets.
     *
     * @see Target
     */
    public AbstractResourceFloodTester() {
        target = Target.getIndeterminateTarget();
    }

    /**
     * Constructs this {@link ResourceFloodTester} object.
     * <p>
     * Use this constructor when targeting a specific aspect of the target,
     * for example web-server API or a particular method of the target
     * object and it is encouraged to report the {@link Target} to clients
     * of this object.
     *
     * @param clazz class object type of {@code resource} under test.
     * @param <U> type of resource object.
     * @see Target
     */
    public <U> AbstractResourceFloodTester(final Class<U> clazz) {
        Class<U> c = Objects.requireNonNull(clazz);
        target = new Target(c);
    }
}
