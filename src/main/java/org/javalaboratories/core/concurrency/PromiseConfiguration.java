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

import lombok.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * Class to represent {@link Promise} configuration.
 * <p>
 * Reads configuration from "{@code promise-configuration.properties}" file. For
 * the configuration file to be active, it must be made available on the
 * application classpath. Any of configuration properties are overridable from
 * the system properties. Following properties are supported:
 * <pre>
 *     {@code
 *          promise.managed.service.class=org.javalaboratories.core.concurrency.ManagedPromisePoolExecutor
 *          promise.managed.service.capacity=-1
 *     }
 * </pre>
 * Setting the property value {@code promise.pool.service.capacity} to -1
 * informs the {@link PromiseConfiguration} to calculate the capacity.
 * <p>
 * If the configuration file is unavailable and system properties unspecified,
 * the above configuration property values will apply. The properties are derived
 * in the following priority, from left to right, leftmost has the highest
 * priority:
 * <pre>
 *     {@code
 *          system property --> file property --> hardcoded default
 *     }
 * </pre>
 * This object is immutable.
 */
@Value
public class PromiseConfiguration {

    public static final String PROMISE_MANAGED_SERVICE_CAPACITY_PROPERTY ="promise.managed.service.capacity";
    public static final String PROMISE_MANAGED_SERVICE_CLASS_PROPERTY ="promise.managed.service.class";

    public static final String DEFAULT_MANAGED_SERVICE_CLASSNAME ="org.javalaboratories.core.concurrency.ManagedThreadPoolPromiseExecutor";

    private static final String PROMISE_CONFIGURATION_FILE= "promise-configuration.properties";
    private static final int MINIMUM_CAPACITY = 1;

    @Getter(AccessLevel.NONE)
    ReentrantLock lock;

    @ToString.Exclude
    Map<String,Object> properties;

    int serviceCapacity;
    String serviceClassName;

    /**
     * Constructs an instance of this object.
     * <p>
     * Loads and processes configuration for {@link Promise} objects. The
     * properties are processed in the following priority, from left to right,
     * leftmost has the highest priority:
     * <pre>
     *     {@code
     *          system property --> file property --> hardcoded default
     *     }
     * </pre>
     */
    public PromiseConfiguration() {
        this(PROMISE_CONFIGURATION_FILE);
    }

    /**
     * Constructs an instance of this object.
     * <p>
     * Loads and processes configuration for {@link Promise} objects. The
     * properties are processed in the following priority, from left to
     * right, leftmost has the highest priority:
     * <pre>
     *     {@code
     *          system property --> file property --> hardcoded default
     *     }
     * </pre>
     * <p>
     * Set the {@code filename} to process file-based configuration,
     * which is the value in the default constructor of
     * {@link PromiseConfiguration}. If the file doesn't exist, system property
     * values and/or hardcoded values would then apply.
     * <p>
     * However, this constructor's access-level has default package access to
     * enable unit test cases access to validate the object behaviour -- do not
     * change access level of constructor!
     *
     * @param filename to enable file-based configuration to be taken into
     *                 account.
     */
    PromiseConfiguration(final String filename) {
        lock = new ReentrantLock();
        properties = load(filename);
        serviceClassName = getValue(PROMISE_MANAGED_SERVICE_CLASS_PROPERTY, DEFAULT_MANAGED_SERVICE_CLASSNAME);
        int capacity = getValue(PROMISE_MANAGED_SERVICE_CAPACITY_PROPERTY,-1);
        serviceCapacity = capacity < MINIMUM_CAPACITY ? Runtime.getRuntime().availableProcessors() : capacity;
    }

    private <T> T getValue(String property, T value) {
        T result = getOrDefault(property, value);
        if  (result instanceof String && isInteger(result)) {
            result = unchecked(Integer.valueOf((String) result));
        }
        return result;
    }

    private <T> T getOrDefault(String property, T value) {
        T result;
        result =  unchecked(properties.getOrDefault(property, value));
        if (result instanceof String && ((String) result).isEmpty())
            result = value;
        return result;
    }

    private <T> Map<String,T> load(final String filename) {
        Map<String,T> result = new HashMap<>();
        if (properties == null) {
            lock.lock();
            try {
                if (filename != null) {
                    Properties fileProperties = new Properties();
                    InputStream stream = PromiseConfiguration.class.getClassLoader()
                            .getResourceAsStream(filename);
                    // Load file if it exists
                    if (stream != null)
                        fileProperties.load(stream);
                    load(fileProperties, result, null);
                }
            } catch (IOException e) {
                // Do-nothing, file I/O error will result in system overrides being applied, if any.
            } finally {
                // Load potential overrides from system properties
                Properties sysProperties = System.getProperties();
                load(sysProperties,result,k -> k.startsWith("promise."));
                lock.unlock();
            }
        }
        return result;
    }

    private <T> void load(Properties source, Map<String,T> properties, Predicate<String> filter) {
        source.forEach((key, value) -> {
            if (filter == null) {
                properties.put((String) key, unchecked(value));
            } else {
                if (filter.test((String)key))
                    properties.put((String) key, unchecked(value));
            }
        });
    }

    private <T> boolean isInteger(T value) {
        boolean result;
        try {
            Integer.parseInt((String) value);
            result = true;
        } catch (NumberFormatException e) {
            result = false;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> T unchecked(Object value) {
        return (T) value;
    }
}


