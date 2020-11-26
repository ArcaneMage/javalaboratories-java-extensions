package org.javalaboratories.core.concurrency;

import lombok.Value;

import java.io.IOException;
import java.util.Properties;

@SuppressWarnings("WeakerAccess")
@Value
public class PromiseConfiguration {
    private static final String PROMISE_CONFIGURATION_FILE="promise-configuration.properties";
    private static final String PROMISE_POOL_SERVICE_PROPERTY ="promise.pool.service";
    private static final String PROMISE_POOL_SERVICE_CAPACITY_PROPERTY="promise.pool.service.capacity";
    private static final String DEFAULT_POOL_SERVICE_CLASSNAME ="org.javalaboratories.core.concurrency.PromisePoolService";

    private static volatile Properties properties = load();

    String poolServiceClassName;
    int poolServiceCapacity;

    public PromiseConfiguration() {
        poolServiceClassName = properties.getProperty(PROMISE_POOL_SERVICE_PROPERTY, DEFAULT_POOL_SERVICE_CLASSNAME);
        poolServiceCapacity = calculateCapacity();
    }

    private static Properties load() {
        if ( properties == null ) {
            synchronized (PromiseConfiguration.class) {
                properties = new Properties();
                try {
                    properties.load(PromiseConfiguration.class.getClassLoader().getResourceAsStream(PROMISE_CONFIGURATION_FILE));
                } catch (IOException e) {
                    // Do-nothing, file I/O error will result in system defaults being applied
                }
            }
        }
        return properties;
    }

    private int calculateCapacity() {
        int cores = Runtime.getRuntime().availableProcessors();
        int capacity = cores;
        try {
            int configValue = Integer.parseInt(properties.getProperty(PROMISE_POOL_SERVICE_CAPACITY_PROPERTY));
            capacity = configValue < 0 ? cores : configValue;
        } catch (NumberFormatException e) {
            // Do noting
        }
        return capacity;
    }
}


