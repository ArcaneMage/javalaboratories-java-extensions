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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Factory to create an instance of a {@link ManagedPoolService} pool service to
 * process {@link Promise} objects asynchronously.
 * <p>
 * {@link PromiseConfiguration} informs the factory the implementation of the
 * {@link ManagedPoolService} thread pool. Failure to create an instance of the
 * the {@code pool} will render promise objects inoperable. If there is a need
 * to provide a custom implementation, it is recommended to inherit from the
 * {@link ManagedPromisePoolExecutor} class and configure the
 * "{@code promise-configuration.properties}" file or alternatively provide
 * configuration with system property values as VM arguments (-D property values).
 *
 * @param <T> Type of managed pool to return.
 * @see PromiseConfiguration
 */
@SuppressWarnings("WeakerAccess")
public final class PromisePoolServiceFactory<T extends ManagedPoolService> {

    private Logger logger = LoggerFactory.getLogger(PromisePoolServiceFactory.class);

    private static volatile ManagedPoolService instance;

    private final PromiseConfiguration configuration;

    public PromisePoolServiceFactory(final PromiseConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration,"No configuration?");
    }

    /**
     * Creates and returns an implementation of {@link ManagedPoolService}
     * <p>
     * This is essentially a managed pool of threads, managed in the sense that
     * the pool will automatically clean up resources including outstanding running
     * threads at application termination.
     *
     * @return an implementation of {@link ManagedPoolService}
     * @see ManagedPoolService
     * @see ManagedPromisePoolExecutor
     * @see PromiseConfiguration
     */
    public T newPoolService() {
        if ( instance == null ) {
            synchronized (PromisePoolServiceFactory.class) {
                String clazzname = configuration.getPoolServiceClassName();
                try {
                    int capacity = configuration.getPoolServiceCapacity();
                    Class<?> clazz = Class.forName(clazzname);
                    if (clazz != ManagedPromisePoolExecutor.class) {
                        // Attempt to instantiate custom promise pool service
                        Constructor<?> constructor = clazz.getConstructor(int.class);
                        instance = unchecked(constructor.newInstance(capacity));
                    } else {
                        // Resort to default implementation
                        instance = unchecked(new ManagedPromisePoolExecutor(capacity));
                    }
                    logger.debug("Promise pool service {} created and initialised with capacity {} successfully", clazz, capacity);
                } catch (ClassCastException e) {
                    logger.error("Promise pool service {} class needs to inherit from {} class", clazzname, ManagedPromisePoolExecutor.class);
                } catch (NoSuchMethodException e) {
                    logger.error("Promise pool service {} class needs to have a constructor with a single int parameter", clazzname);
                } catch (InvocationTargetException e) {
                    logger.error("Promise pool service {} class constructor could not be invoked", clazzname);
                } catch (ClassNotFoundException e) {
                    logger.error("Class not found: {}", clazzname);
                } catch (IllegalAccessException e) {
                    logger.error("Illegal access to method/constructor, class {}", clazzname, e);
                } catch (InstantiationException e) {
                    logger.error("Instantiation exception for {} class", clazzname, e);
                }
            }
        }
        return unchecked(instance);
    }

    private static <T extends ManagedPoolService> T unchecked(Object object) {
        @SuppressWarnings("unchecked")
        T result = (T) object;
        return result;
    }
}
