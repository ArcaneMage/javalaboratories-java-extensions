package org.javalaboratories.core.concurrency;

import org.junit.jupiter.api.*;
import java.util.Properties;

@SuppressWarnings("WeakerAccess")
public class PromisePerTaskTest extends PromiseTest {

    @BeforeAll
    public static void setupAll() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(PromiseConfiguration.PROMISE_MANAGED_SERVICE_CLASS_PROPERTY,
                "org.javalaboratories.core.concurrency.ManagedThreadPerTaskPromiseExecutor");
        properties.setProperty(PromiseConfiguration.PROMISE_MANAGED_SERVICE_CAPACITY_PROPERTY,
                "1024");
        System.setProperties(properties);
        ManagedPromiseServiceFactory<ManagedPromiseService> factory = new ManagedPromiseServiceFactory<>(new PromiseConfiguration());
        Promises.managedService = factory.newService();
    }

    @AfterAll
    public static void teardown() {
        System.clearProperty(PromiseConfiguration.PROMISE_MANAGED_SERVICE_CLASS_PROPERTY);
        System.clearProperty(PromiseConfiguration.PROMISE_MANAGED_SERVICE_CAPACITY_PROPERTY);

        ManagedPromiseServiceFactory<ManagedPromiseService> factory = new ManagedPromiseServiceFactory<>(new PromiseConfiguration());
        Promises.managedService = factory.newService();
    }
}
