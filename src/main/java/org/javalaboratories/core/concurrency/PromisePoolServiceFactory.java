package org.javalaboratories.core.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public final class PromisePoolServiceFactory<T extends ManagedPromisePool> {

    private Logger logger = LoggerFactory.getLogger(PromisePoolServiceFactory.class);

    private static volatile ManagedPromisePool instance;

    private final PromiseConfiguration configuration;

    public PromisePoolServiceFactory(final PromiseConfiguration configuration) {
        Objects.requireNonNull(configuration);
        this.configuration = configuration;
    }

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

    private static <T extends ManagedPromisePool> T unchecked(Object object) {
        @SuppressWarnings("unchecked")
        T result = (T) object;
        return result;
    }
}
