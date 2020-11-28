package org.javalaboratories.core.concurrency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.javalaboratories.core.concurrency.PromiseConfiguration.PROMISE_POOL_SERVICE_CAPACITY_PROPERTY;
import static org.javalaboratories.core.concurrency.PromiseConfiguration.PROMISE_POOL_SERVICE_CLASS_PROPERTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("WeakerAccess")
public class PromiseConfigurationTest {

    @AfterEach
    public void tearDown() {
        System.clearProperty(PROMISE_POOL_SERVICE_CLASS_PROPERTY);
        System.clearProperty(PROMISE_POOL_SERVICE_CAPACITY_PROPERTY);
    }

    @Test
    public void testNew_SystemPropertyDefaults_Pass() {
        // Given
        System.setProperty(PROMISE_POOL_SERVICE_CLASS_PROPERTY,"com.javalaboratories.core.concurrency.CustomPromisePoolExecutor");
        System.setProperty(PROMISE_POOL_SERVICE_CAPACITY_PROPERTY,"64");

        // When
        PromiseConfiguration configuration = new PromiseConfiguration();

        // Then
        assertEquals("com.javalaboratories.core.concurrency.CustomPromisePoolExecutor",configuration.getPoolServiceClassName());
        assertEquals(64,configuration.getPoolServiceCapacity());
    }

    @Test
    public void testNew_FilePropertyDefaults_Pass() {
        PromiseConfiguration configuration = new PromiseConfiguration();

        assertEquals("org.javalaboratories.core.concurrency.ManagedPromisePoolExecutor",configuration.getPoolServiceClassName());
        assertTrue(configuration.getPoolServiceCapacity() > 0);
    }
}
