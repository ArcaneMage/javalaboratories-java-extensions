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
package org.javalaboratories.core.concurrency.utils;

import lombok.AccessLevel;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * This class has the ability to send multiple requests concurrently to the
 * {@link Target's} {@code resource(s)}.
 * <p>
 * It leverages the concept of {@code flood marshals}, {@link FloodMarshal}.
 * These marshals have the authority to tell the {@code flood workers} exactly
 * when to start flooding the {@code target}, consider a track and field
 * official with a starter pistol and athletes on the starter blocks --
 * similar concept.
 * <p>
 * Furthermore, it will wait until all {@code flood workers} have completed
 * their work before returning control to the client (as long as the workers)
 * conclude their task within the allotted time of
 * {@link AbstractConcurrentResourceFloodStability#DEFAULT_TIMEOUT_MINUTES}.
 * Alternatively, use the
 * {@link AbstractConcurrentResourceFloodStability#flood(long, TimeUnit)} method.
 * Example usage is as follows:
 * <pre>
 *     {@code
 *             Floodgate<Integer> floodgate = new Floodgate<>(UnsafeStatistics.class, () -> unsafe.add(10));
 *             ...
 *             floodgate.open();
 *             floodgate.flood();
 *             ...
 *     }
 * </pre>
 * The command automatically creates 5 {@code flood workers} with 5 repetitions,
 * which means the {@code unsafe.add()} method will be subjected to
 * 25 {@code requests} in total, 5 repeated from 5 {@code flood workers}
 * working simultaneously. Both {@code threads} and {@code iterations/repetitions}
 * can be overridden via alternative constructors.
 *
 * @param <T> Type of value returned from {@link Target} {@code resource}
 * @see Floodgate
 */
@Getter
public class Floodgate<T> extends AbstractConcurrentResourceFloodStability<T> {

    private static final Logger logger = LoggerFactory.getLogger(Floodgate.class);

    /**
     * Default number of threads
     */
    public static final int DEFAULT_FLOOD_WORKERS = 5;

    /**
     * Default number of iterations/repetitions
     */
    public static final int DEFAULT_FLOOD_ITERATIONS = 5;

    /**
     * Indicates tag are not required.
     */
    public static final String UNTAGGED = null;

    @Getter(AccessLevel.NONE)
    private final FloodMarshal floodMarshal;
    @Getter(AccessLevel.NONE)
    private final CountDownLatch workLatch;

    @Getter(AccessLevel.NONE)
    private final Supplier<T> resource;

    /**
     * Constructs this {@link Floodgate} object with targeted {@code resource}.
     * <p>
     * The number of {@code threads} and {@code iterations/repetitions} are
     * defaulted to {@link Floodgate#DEFAULT_FLOOD_WORKERS} and
     * {@link Floodgate#DEFAULT_FLOOD_WORKERS} respectively. Use this
     * constructor for {@code resource} that does not return a {@code value}.
     *
     * @param clazz {@code class} type of {@code target} subjected to tests.
     * @param resource the actual resource of the {@code target}. Consider the
     *                 {@code resource} as the method or API of the targeted
     *                 object, expected type is {@link Runnable}
     *
     * @param <U> Type of {@code target} under test.
     */
    public <U> Floodgate(final Class<U> clazz, final Runnable resource) {
        this(clazz,DEFAULT_FLOOD_WORKERS, DEFAULT_FLOOD_ITERATIONS,resource);
    }

    /**
     * Constructs this {@link Floodgate} object with targeted {@code resource}.
     * <p>
     * The number of {@code threads} and {@code iterations/repetitions} are
     * defaulted to {@link Floodgate#DEFAULT_FLOOD_WORKERS} and
     * {@link Floodgate#DEFAULT_FLOOD_WORKERS} respectively. Use this
     * constructor for {@code resource} that returns a {@code value}.
     *
     * @param clazz {@code class} type of {@code target} subjected to tests.
     * @param resource the actual resource of the {@code target}. Consider the
     *                 {@code resource} as the method or API of the targeted
     *                 object, expected type is {@link Supplier}
     *
     * @param <U> Type of {@code target} under test.
     */
    public <U> Floodgate(final Class<U> clazz, final Supplier<T> resource) {
        this(clazz,DEFAULT_FLOOD_WORKERS, DEFAULT_FLOOD_ITERATIONS,resource);
    }

    /**
     * Constructs this {@link Floodgate} object with targeted {@code resource}.
     * <p>
     * The number of {@code threads} and {@code iterations/repetitions} are
     * configurable with {@code threads} and {@code iterations} parameters. If
     * no values are returned from the {@code resource}, use this constructor.
     *
     * @param clazz {@code class} type of {@code target} subjected to tests.
     * @param threads number of threads {@code flood workers} required for the
     *                flood.
     * @param iterations number of request repetitions each thread will perform.
     * @param resource the actual resource of the {@code target}. Consider the
     *                 {@code resource} as the method or API of the targeted
     *                 object, expected type is {@link Runnable}
     * @param <U> Type of {@code target} under test.
     */
    public <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Runnable resource) {
        this(clazz,threads,iterations,() -> {resource.run(); return null;});
    }

    /**
     * Constructs this {@link Floodgate} object with targeted {@code resource}.
     * <p>
     * The number of {@code threads} and {@code iterations/repetitions} are
     * configurable with {@code threads} and {@code iterations} parameters. If
     * values are returned from the {@code resource}, use this constructor.
     *
     * @param clazz {@code class} type of {@code target} subjected to tests.
     * @param threads number of threads {@code flood workers} required for the
     *                flood.
     * @param iterations number of request repetitions each thread will perform.
     * @param resource the actual resource of the {@code target}. Consider the
     *                 {@code resource} as the method or API of the targeted
     *                 object,
     *                 expected type is {@link Supplier}
     * @param <U> Type of {@code target} under test.
     */
    public <U> Floodgate(final Class<U> clazz, final int threads, final int iterations, final Supplier<T> resource) {
        this(clazz,UNTAGGED,threads,iterations,resource, getMarshal());
    }

    /**
     * Constructs this {@link Floodgate} object with targeted {@code resource}.
     * <p>
     * This constructor offers considerable control over the behaviour of
     * the {@link Floodgate} via the {@link FloodMarshal} and therefore it is
     * not designed to be generally called by clients. Preferably use the
     * alternative constructors with {@code public} access level. The constructor
     * should be used if the concept of {@link FloodMarshal} objects is
     * understood.
     * <p>
     * The number of {@code threads} and {@code iterations/repetitions} are
     * configurable with {@code threads} and {@code iterations} parameters.
     * <p>
     * Optionally a {@code tag} can be supplied to enable easy identification
     * of {@code resource} under test in reports. This particularly useful with
     * multiple instances of the {@code floodgates} reporting at the same time.
     *
     * @param clazz {@code class} type of {@code target} subjected to tests.
     * @param tag a meaningful name to describe the resource under test.
     * @param threads number of threads {@code flood workers} required for the
     *                flood.
     * @param iterations number of request repetitions each thread will perform.
     * @param resource the actual resource of the {@code target}. Consider the
     *                 {@code resource} as the method or API of the targeted
     *                 object,
     *                 expected type is {@link Supplier}
     * @param marshal  the {@link FloodMarshal} object that will manage the
     *                 {@code flood workers}.
     * @param <U> Type of {@code target} under test.
     * @see FloodMarshal
     * @see ExternalFloodMarshal
     */
    <U> Floodgate(final Class<U> clazz, final String tag, final int threads, final int iterations,
                  final Supplier<T> resource, final FloodMarshal marshal) {
        super(clazz,tag,threads,iterations);
        if (resource == null || marshal == null)
            throw new IllegalArgumentException("Review floodgate constructor arguments");
        this.workLatch = new CountDownLatch(threads);
        this.resource = resource;
        this.floodMarshal = marshal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String marshal = floodMarshal instanceof ExternalFloodMarshal ? "External" : "Internal";
        return String.format("[target=%s,state=%s,flood-workers=%d,flood-iterations=%d,flood-marshal=%s]", getTarget(),
                getState(),getThreads(),getIterations(),marshal);
    }

    /**
     * {@inheritDoc}
     * <p>
     * This {@link Floodgate} implementation decorates the {@code resource}
     * object with additional behaviour to enable {@link FloodMarshal} support,
     * and useful logging information for tracking of {@code resource} state.
     * If the {@code resource} causes raises an exception, the {@code target}
     * would be considered as {@code unstable}.
     * <p>
     * {@code Floodgate} is notified when the {@code flood workers} complete their
     * task.
     * @see ResourceFloodStability.Target
     * @see FloodMarshal
     * @see ExternalFloodMarshal
     */
    protected final Supplier<T> primeResource() {
        Supplier<T> resource = super.primeResource();
        return () -> {
            T result = null;
            try {
                floodMarshal.halt();
                logger.info(message("Received authorisation to commence flood"));
                result = resource.get();
                logger.info(message("Finished flooding resource object successfully"));
            } catch (InterruptedException e) {
                logger.error(message("Finished flooding resource object but with interruption"));
            } finally {
                workLatch.countDown();
            }
            return result;
        };
    }

    /**
     * @return underlying resource subjected to tests.
     */
    @Override
    protected final Supplier<T> getResource() {
        return resource;
    }

    /**
     * {@inheritDoc}
     *
     * {@code Flood workers} notify this object of task completion, and so
     * {@code await} can wait for all them to complete but within the allotted
     * time.
     *
     * @param timeout maximum time in which to wait for threads to complete
     * @param unit the unit of timeout.
     * @see FloodMarshal
     * @see Torrent
     */
    protected void await(long timeout, TimeUnit unit) throws InterruptedException {
        if (!workLatch.await(timeout, unit))
            logger.error(message("Insufficient wait timeout specified, not all flood workers have completed their work"));
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this {@link Floodgate} implementation, {@link FloodMarshal} support is
     * enabled. This method authorises the {@code flood workers} to start on the
     * {@link FloodMarshal} orders. For this to work correctly, all workers need
     * to be already running and waiting for authorisation.
     * <p>
     * However, if an {@link ExternalFloodMarshal} is provided, the workers will
     * NOT be authorised to start their work from this method. An external object
     * will provide authorization instead, but details of the {@code marshal} are
     * reported. This approach is commonly used with multiple instances of
     * {@code floodgates}.
     */
    protected void superviseFlood() {
        if (!(floodMarshal instanceof ExternalFloodMarshal)) {
            super.superviseFlood();
            floodMarshal.flood();
        } else {
            ExternalFloodMarshal<?> marshal = (ExternalFloodMarshal<?>) floodMarshal;
            String manager = marshal.manager().getSimpleName();
            logger.info(message("Floodgate supporting \"{}\" via external flood marshal"),manager);
        }
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
