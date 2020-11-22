package org.javalaboratories.core.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

@SuppressWarnings("WeakerAccess")
public final class Promises {

    private static final Logger logger = LoggerFactory.getLogger(Promises.class);

    private static final String PROMISE_CONFIGURATION_FILE="promise-configuration.properties";
    private static final String PROMISE_POOL_SERVICE_FACTORY_PROPERTY="promise.pool.service.factory";
    private static final String PROMISE_POOL_SERVICE_CAPACITY_PROPERTY="promise.pool.service.capacity";
    private static final String DEFAULT_FACTORY_CLASSNAME ="org.javalaboratories.core.concurrency.PromisePoolServiceFactory";

    private static PromisePoolService promisePoolService = createPoolService();

    private Promises() {}

    public static <T> void all(final List<PrimaryAction<T>> actions) {
        Objects.requireNonNull(actions);
        actions.forEach(Promises::newPromise);
    }

    public static <T> Promise<T> newPromise(final PrimaryAction<T> action) {
        Objects.requireNonNull(action,"Cannot keep promise -- no action?");
        Promise<T> result = new Promise<>(promisePoolService,action);
        result.invokePrimaryActionAsync(action);
        return result;
    }

    private static PromisePoolService createPoolService() {
        Properties properties = new Properties();
        try {
            properties.load(Promises.class.getClassLoader().getResourceAsStream(PROMISE_CONFIGURATION_FILE));
        } catch (IOException e) {
            // Do-nothing, file I/O error will result in system defaults being applied
        }
        String clazzname = properties.getProperty(PROMISE_POOL_SERVICE_FACTORY_PROPERTY,
                DEFAULT_FACTORY_CLASSNAME);
        int capacity = getCapacity(properties);
        PromisePoolService result = null;
        try {
            PromisePoolServiceFactory factory;
            Class<?> clazz = Class.forName(clazzname);
            if (clazz != PromisePoolServiceFactory.class) {
                // Attempt to instantiate custom promise factory
                factory = (PromisePoolServiceFactory) clazz.newInstance();
            } else {
                // Resort to default implementation
                factory = (PromisePoolService::new);
            }
            result = factory.newPoolService(capacity);
            logger.debug("Promise factory {} created and initialised with capacity {} successfully", clazz, capacity);
        } catch (ClassCastException e) {
            logger.error("Promise factory {} class needs to implement {} interface",clazzname, PromisePoolService.class);
        } catch (ClassNotFoundException e) {
            logger.error("Class not found: {}",clazzname);
        } catch (IllegalAccessException e) {
            logger.error("Illegal access to method/constructor, class {}",clazzname,e);
        } catch (InstantiationException e) {
            logger.error("Instantiation exception for {} class",clazzname, e);
        }

        return result;
    }

    private static int getCapacity(Properties properties) {
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
