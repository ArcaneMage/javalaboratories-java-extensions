package org.javalaboratories.core.concurrency;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Properties;

public class DefaultPromisePoolServiceFactory<T extends PromisePoolService> implements PromisePoolServiceFactory<T> {

    private static final String PROMISE_POOL_SERVICE_PROPERTY ="promise.pool.service";
    private static final String PROMISE_POOL_SERVICE_CAPACITY_PROPERTY="promise.pool.service.capacity";
    private static final String DEFAULT_POOL_SRVICE_CLASSNAME ="org.javalaboratories.core.concurrency.PromisePoolService";

    private final Properties configuration;

    public DefaultPromisePoolServiceFactory(final Properties configuration) {
        Objects.requireNonNull(configuration);
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    public T newPoolService() {
        String clazzname = configuration.getProperty(PROMISE_POOL_SERVICE_PROPERTY, DEFAULT_POOL_SRVICE_CLASSNAME);
        T result = null;
        try {
            int capacity = getCapacity();
            Class<?> clazz = Class.forName(clazzname);
            if (clazz != PromisePoolService.class) {
                // Attempt to instantiate custom promise pool service
                Constructor<?> constructor = clazz.getConstructor(int.class);
                result = (T) constructor.newInstance(capacity);
            } else {
                // Resort to default implementation
                result = (T) new PromisePoolService(capacity);
            }
            logger.debug("Promise pool service {} created and initialised with capacity {} successfully", clazz, capacity);
        } catch (ClassCastException e) {
            logger.error("Promise pool service {} class needs to inherit from {} class",clazzname, PromisePoolService.class);
        } catch (NoSuchMethodException e) {
            logger.error("Promise pool service {} class needs to have a constructor with a single int parameter", clazzname);
        } catch (InvocationTargetException e) {
            logger.error("Promise pool service {} class constructor could not be invoked", clazzname);
        } catch (ClassNotFoundException e) {
            logger.error("Class not found: {}", clazzname);
        } catch (IllegalAccessException e) {
            logger.error("Illegal access to method/constructor, class {}",clazzname,e);
        } catch (InstantiationException e) {
            logger.error("Instantiation exception for {} class",clazzname, e);
        }

        return result;
    }

    private int getCapacity() {
        int cores = Runtime.getRuntime().availableProcessors();
        int capacity = cores;
        try {
            int configValue = Integer.parseInt(configuration.getProperty(PROMISE_POOL_SERVICE_CAPACITY_PROPERTY));
            capacity = configValue < 0 ? cores : configValue;
        } catch (NumberFormatException e) {
            // Do noting
        }
        return capacity;
    }
}
