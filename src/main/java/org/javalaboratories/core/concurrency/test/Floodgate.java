/*
 * Copyright 2020 Kevin Henry
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.javalaboratories.core.concurrency.test;

import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Getter
public class Floodgate<T> extends AbstractConcurrentResourceFloodTester<T> {

    public static final int DEFAULT_FLOOD_WORKERS = 5;
    public static final int DEFAULT_FLOOD_ITERATIONS = 5;

    private static final Logger logger = LoggerFactory.getLogger(Floodgate.class);

    @Getter(AccessLevel.NONE)
    private final FloodMarshal floodMarshal;
    @Getter(AccessLevel.NONE)
    private final CountDownLatch workLatch;

    @Getter(AccessLevel.NONE)
    private final Supplier<T> resource;


    public <U> Floodgate(final Class<U> clazz, final Runnable resource) {
        this(clazz,DEFAULT_FLOOD_WORKERS, DEFAULT_FLOOD_ITERATIONS,resource);
    }

    public <U> Floodgate(final Class<U> clazz, final Supplier<T> resource) {
        this(clazz,DEFAULT_FLOOD_WORKERS, DEFAULT_FLOOD_ITERATIONS,resource);
    }

    public <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Runnable resource) {
        this(clazz,threads,iterations,() -> {resource.run(); return null;});
    }

    public <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Supplier<T> resource) {
        this(clazz,threads,iterations,resource, getMarshal());
    }

    <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Supplier<T> resource, final FloodMarshal marshal) {
        super(clazz,threads,iterations);
        if (marshal == null)
            throw new IllegalArgumentException("Review floodgate constructor arguments");
        this.floodMarshal = marshal;
        this.workLatch = new CountDownLatch(threads);
        this.resource = resource;
    }

    @Override
    public String toString() {
        String controller = floodMarshal instanceof ExternalFloodMarshal ? "External" : "Internal";
        return String.format("[target=%s,state=%s,flood-workers=%d,flood-iterations=%d,flood-controller=%s]", getTarget(),
                getState(),getThreads(),getIterations(),controller);
    }

    protected Supplier<T> primeResource() {
        Supplier<T> t = Objects.requireNonNull(resource);
        return () -> {
            T result = null;
            try {
                floodMarshal.halt();
                if (getTarget().getStability() == Target.Stability.STABLE) {
                    int i = 0;
                    while (i++ < getIterations()) {
                        result = t.get();
                    }
                } else {
                    logger.warn("{}: Target state is unstable -- cannot flood",getTarget().getName());
                }
                logger.info("{}: Finished flooding resource object successfully", this.getTarget().getName());
            } catch (InterruptedException e) {
                logger.error("{}: Finished flooding resource object but with interruption", this.getTarget().getName());
            } catch (Throwable throwable) {
                logger.error("{}: Targeted resource raised an exception during flood", this.getTarget().getName(),throwable);
                getTarget().unstable();
            } finally {
                workLatch.countDown();
            }
            return result;
        };
    }

    protected void floodResource(final long timeout, final TimeUnit units) throws InterruptedException {
        logger.info("{}: Flooding resource with {} flood workers, each iterating {} times",getTarget().getName(),
                getThreads(),getIterations());
        if (getService().getActiveCount() < getThreads() )
            logger.warn("{}: Active thread count {}. Not all flood workers are ready",getTarget().getName(),
                    getService().getActiveCount());
        if (!(floodMarshal instanceof ExternalFloodMarshal)) {
            floodMarshal.flood();
        } else {
            logger.info("{}: Flood controller externally managed -- deferred management",getTarget().getName());
        }
        if (!workLatch.await(timeout, units))
            logger.error("{}: Insufficient wait timeout specified, not all flood workers have completed their work",
                    getTarget().getName());
    }

    private static FloodMarshal getMarshal() {
        return new FloodMarshal() {
            final CountDownLatch latch = new CountDownLatch(1);
            @Override
            public void halt() throws InterruptedException {
                latch.await();
            }
            @Override
            public void flood() {
                latch.countDown();
            }
        };
    }
}
