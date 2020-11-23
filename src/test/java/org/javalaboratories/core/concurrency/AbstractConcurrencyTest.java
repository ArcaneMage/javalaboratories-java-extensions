package org.javalaboratories.core.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public abstract class AbstractConcurrencyTest {
    private Logger logger =  LoggerFactory.getLogger(AbstractConcurrencyTest.class);

    int getValue(CountDownLatch latch, AtomicInteger atomicInteger, Supplier<Integer> supplier) {
        try {
            int value = supplier.get();
            atomicInteger.set(value);
            logger.info("Received and processed value ({}) asynchronously", atomicInteger.get());
            return value;
        } finally {
            if ( latch != null )
                latch.countDown();
        }
    }

    void wait(String testcase, Object... params) {
        wait(null,testcase,params);
    }

    void wait(CountDownLatch latch, String testcase, Object... params) {
        String logMessage = testcase+": Waiting for kept promises";
        logger.info(logMessage,params);
        if ( latch != null ) {
            try {
                latch.await(4, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Latch interrupted",e);
            }
        }
    }

    int doLongRunningTaskWithException(String narrative) {
        logger.error("doLongRunningTaskWithException: {} ",narrative);
        throw new IllegalStateException("Threw an exception in promise");
    }

    int doLongRunningTask(String narrative) {
        return doLongRunningTask(null,narrative);
    }

    int doLongRunningTask(CountDownLatch latch,String narrative) {
        //sleep(1000);
        try {
            logger.info("doLongRunningTask: {} asynchronously", narrative);
            return 127;
        } finally {
            if ( latch != null )
                latch.countDown();
        }
    }

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Do nothing
        }
    }

}
