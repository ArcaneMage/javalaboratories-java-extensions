package org.javalaboratories.core.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
public final class Promises {

    private static final Logger logger = LoggerFactory.getLogger(Promises.class);

    private static final String PROMISE_CONFIGURATION_FILE="promise-configuration.properties";
    private static final String PROMISE_POOL_SERVICE_FACTORY_PROPERTY="promise.pool.service.factory";
    private static final String PROMISE_POOL_SERVICE_CAPACITY_PROPERTY="promise.pool.service.capacity";
    private static final String DEFAULT_FACTORY_CLASSNAME ="org.javalaboratories.core.concurrency.PromisePoolServiceFactory";

    private static PromisePoolService promisePoolService = createPoolService();

    private Promises() {}

    public static <T> List<Promise<T>> all(final List<PrimaryAction<T>> actions) {
        return all(actions,(action) -> () -> new Promise<>(promisePoolService,action));
    }

    public static <T,U extends Promise<T>> List<Promise<T>> all(final List<PrimaryAction<T>> actions, final Function<PrimaryAction<T>,Supplier<U>> function ) {
        Objects.requireNonNull(actions);
        Objects.requireNonNull(function);
        List<Promise<T>> results = new ArrayList<>();
        actions.forEach(a -> {
            Promise<T> promise = newPromise(a,function.apply(a));
            results.add(promise);
        });
        return Collections.unmodifiableList(results);
    }

    public static <T> Promise<T> newPromise(final PrimaryAction<T> action) {
        return newPromise(action, () -> new Promise<>(promisePoolService,action));
    }

    public static <T, U extends Promise<T>> Promise<T> newPromise(final PrimaryAction<T> action, final Supplier<U> supplier) {
        Objects.requireNonNull(action,"Cannot keep promise -- no action?");
        Promise<T> result = supplier.get();
        result.invokePrimaryAction(action);
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
                factory = PromisePoolService::new;
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
