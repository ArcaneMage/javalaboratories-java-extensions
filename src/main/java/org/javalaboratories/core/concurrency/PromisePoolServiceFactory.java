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
package org.javalaboratories.core.concurrency;

/**
 * Factory to create {@code PromisePoolService} thread pool instances.
 * <p>
 * Only one instance of the thread pool can be created at any one time, they are
 * considered to be singletons. All {@code Promise} objects will submit their
 * {@code PrimaryAction}, {@code Action} and {@code TransmuteAction} objects to
 * the thread pool for processing.
 * <p>
 * The {@code promise-configuration.properties} file configures the classname
 * of the factory and workers' capacity. If the file is mis-configured or missing,
 * system defaults will apply.
 * <p>
 * Ensure the custom factory implements this interface.
 *
 * @param <T> Type of thread pool the factory creates. This must be inherited
 *           from {@link PromisePoolService}
 */
@FunctionalInterface
public interface PromisePoolServiceFactory<T extends PromisePoolService> {

    /**
     * Creates a new instance of the {@link PromisePoolService} thread pool
     * to be used by {@link Promise} objects.
     *
     * @param capacity maximum number of thread workers
     * @return new instance of the {@link PromisePoolService} thread pool.
     */
    T newPoolService(int capacity);
}
