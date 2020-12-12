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

import java.util.Objects;

/**
 * Partially implements the {@link ResourceFloodTester} interface, providing
 * {@link Target} object.
 * <p>
 * It is recommended to extend this class to provide behaviour for flooding
 * targets with requests.
 * <p>
 * @param <T> Type of return value from targeted {@code resource} under test.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractResourceFloodTester<T> implements ResourceFloodTester<T> {

    @EqualsAndHashCode.Include
    private final Target target;

    /**
     * Constructs this {@link ResourceFloodTester} object.
     *
     * @param clazz class object type of {@code resource} under test.
     * @param <U> type of resource object.
     */
    public <U> AbstractResourceFloodTester(final Class<U> clazz) {
        Class<U> c = Objects.requireNonNull(clazz);
        target = new Target(c);
    }

    @Override
    public Target getTarget() {
        return target;
    }
}
