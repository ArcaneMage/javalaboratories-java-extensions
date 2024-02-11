package org.javalaboratories.core.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.javalaboratories.core.concurrency.Promise.States.FULFILLED;
import static org.javalaboratories.core.concurrency.Promise.States.REJECTED;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class PromisePerTaskTest extends PromiseTest {

    @BeforeEach
    public void setup() {
        super.setup();
        Properties properties = new Properties();
        properties.setProperty(PromiseConfiguration.PROMISE_MANAGED_SERVICE_CLASS_PROPERTY,
                "org.javalaboratories.core.concurrency.ManagedThreadPerTaskPromiseExecutor");
        properties.setProperty(PromiseConfiguration.PROMISE_MANAGED_SERVICE_CAPACITY_PROPERTY,
                "1024");
        System.setProperties(properties);
    }

    @AfterEach
    public void teardown() {
        System.clearProperty(PromiseConfiguration.PROMISE_MANAGED_SERVICE_CLASS_PROPERTY);
        System.clearProperty(PromiseConfiguration.PROMISE_MANAGED_SERVICE_CAPACITY_PROPERTY);
    }
}
