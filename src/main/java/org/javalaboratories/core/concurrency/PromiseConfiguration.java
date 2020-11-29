package org.javalaboratories.core.concurrency;

import lombok.ToString;
import lombok.Value;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
 *          promise.pool.service.class=org.javalaboratories.core.concurrency.ManagedPromisePoolExecutor
 *          promise.pool.service.capacity=-1
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
    static final String PROMISE_POOL_SERVICE_CAPACITY_PROPERTY="promise.pool.service.capacity";
    static final String PROMISE_POOL_SERVICE_CLASS_PROPERTY ="promise.pool.service.class";

    private static final String PROMISE_CONFIGURATION_FILE= "promise-configuration.properties";
    private static final String DEFAULT_POOL_SERVICE_CLASSNAME ="org.javalaboratories.core.concurrency.ManagedPromisePoolExecutor";
    private static final int MINIMUM_CAPACITY = 1;

    @ToString.Exclude
    Map<String,Object> properties;

    int poolServiceCapacity;
    String poolServiceClassName;

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
     */
    public PromiseConfiguration() {
        properties = load();
        poolServiceClassName = getValue(PROMISE_POOL_SERVICE_CLASS_PROPERTY, DEFAULT_POOL_SERVICE_CLASSNAME);
        int capacity = getValue(PROMISE_POOL_SERVICE_CAPACITY_PROPERTY,-1);
        poolServiceCapacity = capacity < MINIMUM_CAPACITY ? Runtime.getRuntime().availableProcessors() : capacity;
    }

    private <T> T getValue(String property, T value) {
        T result = unchecked(properties.getOrDefault(property, value));
        if  (result instanceof String && isInteger(result)) {
            result = unchecked(Integer.valueOf((String) result));
        }
        return result;
    }

    private <T> Map<String,T> load() {
        Map<String,T> result = new HashMap<>();
        if ( properties == null ) {
            synchronized (this) {
                try {
                    Properties fileProperties = new Properties();
                    InputStream stream = PromiseConfiguration.class.getClassLoader()
                            .getResourceAsStream(PROMISE_CONFIGURATION_FILE);
                    if ( stream != null )
                        fileProperties.load(stream);
                    load(fileProperties,result,null);
                } catch (IOException e) {
                    // Do-nothing, file I/O error will result in system overrides being applied, if any.
                } finally {
                    // Load potential overrides from system properties
                    Properties sysProperties = System.getProperties();
                    load(sysProperties,result,k -> k.startsWith("promise."));
                }
            }
        }
        return result;
    }

    private <T> void load(Properties source, Map<String,T> properties, Predicate<String> filter) {
        source.forEach((key, value) -> {
            if ( filter == null ) {
                properties.put((String) key, unchecked(value));
            } else {
                if ( filter.test((String)key) )
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


