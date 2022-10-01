package org.javalaboratories.core.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public abstract class AbstractConcurrencyTest {
    private final Logger logger =  LoggerFactory.getLogger(AbstractConcurrencyTest.class);

    int getValue(AtomicInteger atomicInteger, Supplier<Integer> supplier) {
        int value = supplier.get();
        atomicInteger.set(value);
        logger.info("Received and processed value ({}) asynchronously", atomicInteger.get());
        return value;
    }

    void wait(String testcase, Object... params) {
        String logMessage = testcase+": Waiting for kept promises";
        logger.info(logMessage,params);
    }

    int doLongRunningTaskWithException(String narrative) {
        logger.error("doLongRunningTaskWithException: {} ",narrative);
        throw new IllegalStateException("Threw an exception in promise");
    }

    public int doLongRunningTask(String narrative) {
        sleep(1000);
        logger.info("doLongRunningTask: {} asynchronously", narrative);
        return 127;
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
            Thread.yield();
        } catch (InterruptedException e) {
            // Do nothing
        }
    }

}
