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
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.javalaboratories.core.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.javalaboratories.core.concurrency.utils.Floodgate.UNTAGGED;

/**
 * The purpose of this class is to orchestrate multi-thread testing of multiple
 * {@code resources} of a {@code target}.
 * <p>
 * To enable this, it enlists the assistance of the multiple {@link Floodgate}
 * objects and with the aid of the {@link ExternalFloodMarshal} object,
 * instructs them exactly when to simultaneously flood the {@code target}. It is
 * possible to independently create many {@link Floodgate} objects, however
 * they will act autonomously flooding the {@code target} at will, but it is
 * far more effective to carefully time the flooding for maximum impact.
 * <p>
 * If the client requires access to the {@link Floodgate} objects in this
 * container, the class provides both {@link Torrent#iterator()} and
 * {@link Torrent#toList()} methods but the {@code floodgate} objects and the
 * {@link List} object returned are immutable to ensure {@link Torrent} cannot
 * be undermined.
 * <p>
 * Like the {@link Floodgate} object, post-flood it is not the role of this
 * object to test the state and/or stability of the {@code targeted} object.
 * However, a generous amount of log information is generated reporting the
 * current state of the threads {@code flood workers} and the {@code resources}
 * under test. Users of both {@link Floodgate} and {@link Torrent} are encouraged
 * to review the console output and test the state of the {@code target}
 * post-flood.
 * <p>
 * Both {@link Floodgate} and {@link Torrent} classes implement the
 * {@link ConcurrentResourceFloodStability} interface, and so the usage of this
 * class is almost identical to the {@code Floodgate} object. Instantiating this
 * object must achieved with a {@code builder}:
 * <pre>
 *     {@code
 *              // Using tags is optional, but it helps to track flood workers
 *              // and the resources they are targeting.
 *              //
 *              Torrent torrent = Torrent.builder(Statistics.class)
 *                 .withFloodgate("print", () -> unsafe.print())
 *                 .withFloodgate("add", () -> unsafe.add(10))
 *                 .build();
 *
 *              ...
 *              ...
 *
 *              // This example illustrates use of torrent objects without tags
 *              //
 *              Torrent torrent = Torrent.builder(Statistics.class)
 *                 .withFloodgate(() -> unsafe.print())
 *                 .withFloodgate(() -> unsafe.add(10))
 *                 .build();
 *
 *             torrent.open();
 *             torrent.flood();
 *
 *             Map<String, List<?>> result = torrent.flood();
 *     }
 * </pre>
 * Returned value from the {@code torrent.flood()} is a {@link Map} containing
 * {@link List} objects of returned values for each {@code floodgate}. With this
 * inspect the returned values experienced from each {@code flood worker} but be
 * aware of {@link Runnable} objects, these return {@code null}
 */
@Getter
public final class Torrent extends AbstractResourceFloodStability<Map<String,List<?>>> implements
        ConcurrentResourceFloodStability<Map<String,List<?>>>,
        Iterable<ConcurrentResourceFloodStability<?>> {

    private static final Logger logger = LoggerFactory.getLogger(Torrent.class);

    @Getter(AccessLevel.NONE)
    private final List<Floodgate<?>> floodgates;
    @Getter(AccessLevel.NONE)
    private final ExternalFloodMarshal<Torrent> floodMarshal;

    private final FloodExecutorService service;
    private States state;

    /**
     * Default constructor for this {@code torrent} object.
     * <p>
     * This class implements the {@code builder} pattern to help simplify
     * construction. This is the only means by which to construct this object.
     *
     * @param service executor service used by {@code floodgates}
     * @throws NullPointerException if {@code service} parameter is null.
     * @see Torrent#builder(Class)
     * @see Torrent#builder(Class, int, int)
     */
    private Torrent(final FloodExecutorService service) {
        super();
        Objects.requireNonNull(service);
        floodgates = new ArrayList<>();
        floodMarshal = new ExternalFloodMarshal<Torrent>() {
            final CountDownLatch latch = new CountDownLatch(1);
            @Override
            public void halt() throws InterruptedException {
                latch.await();
            }
            @Override
            public void flood() {
                logger.info(message("Marshal is flooding all floodgates simultaneously"));
                latch.countDown();
            }
            @Override
            public Class<Torrent> manager() {
                return Torrent.class;
            }
        };
        this.service = service;
        state = States.CLOSED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        close(false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this implementation, the method will start the {@code flood} process,
     * instructing all {@link Floodgate} objects to commence flooding.
     * <p>
     * All {@code flood workers} across all {@code Floodgates} wait for the
     * authorisation to commence flooding; this can only issued by the
     * {@link ExternalFloodMarshal} object. Calling this method causes the
     * following steps to take place:
     * <ol>
     *     <li>Call the "flood" process in each Floodgate with the flood method</li>
     *     <li>Flood marshal authorises all flood workers to commence their work</li>
     *     <li>Wait for all floodgates to conclude</li>
     *     <li>Return results from floodgates</li>
     * </ol>
     * In the case of step (1), because of the blocking nature of
     * {@link Floodgate#flood()} method, this is called within a separate thread
     * for all {@code floodgates} and the results collated later when they all
     * complete.
     * <p>
     * Essentially {@link Floodgate} objects become {@code slave floodgates}
     * reporting back to their {@code master}, this object.
     *
     * @return a {@link Map} of {@link List} objects containing results from each
     * {@code flood worker}, ready for inspection.
     * @throws IllegalStateException if not in
     * {@link ResourceFloodStability.States#OPENED} state.
     */
    @Override
    public Map<String,List<?>> flood() {
        if (state != States.OPENED)
            throw new IllegalStateException(String.format("Torrent not open, state=%s",state));

        Map<String, List<?>> result = null;
        try {
            CompletableFuture<Map<String, List<?>>> future = CompletableFuture
                .supplyAsync(() -> {
                    Map<String,List<?>> response = new HashMap<>();
                    floodgates.forEach(fg -> response.put(fg.getTarget().getName(),fg.flood()));
                    return response;
                })
                .whenComplete((v, e) -> logger.info(message("Number of floodgates completed: {}"), v.size()));

            floodMarshal.flood();
            logger.info(message("Torrent authorised flood commencement"));

            try {
                result = future.join();
                floodgates.stream()
                    .filter(fg -> fg.getTarget().getStability() == Target.Stability.UNSTABLE)
                    .findFirst()
                    .ifPresent(fg -> logger.info(message("Floodgate(s) encountered unstable target(s) -- review failures")));

            } catch (CancellationException | CompletionException e) {
                logger.error(message("Torrent has encountered an error"),e);
            }
        } finally {
            close();
            state = States.FLOODED;
        }
        return result;
    }

    /**
     * Method provides a {@link List} of {@link ConcurrentResourceFloodStability}
     * objects.
     * <p>
     * This is a collection of {@link Floodgate} objects contained within this
     * object but they are immutable to protect the {@link Torrent} integrity.
     *
     * @return a {@link List} of {@code floodgate} objects within this
     * container.
     */
    public List<ConcurrentResourceFloodStability<?>> toList() {
        return Collections.unmodifiableList(
            floodgates.stream()
                .map(UnmodifiableFloodgate::new)
                .collect(Collectors.toList()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * @return provides iteration of immutable {@link Floodgate} objects.
     */
    @Override
    public Iterator<ConcurrentResourceFloodStability<?>> iterator() {
        return toList().iterator();
    }

    /**
     * Returns the average number of {@code iterations} performed by all {@code
     * flood workers} in {@link Floodgate} objects.
     * <p>
     * {@link Floodgate} objects in this class may have different {@code
     * iterations} assigned to their respective {@code flood workers} --
     * configurable via constructors -- so this method provides some insight
     * into actual number of iterations performed by each {@code flood worker}
     * across all {@link Floodgate} objects.
     *
     * @return average number of iterations performed across all
     * {@code flood workers}
     */
    public float getAverageIterations() {
        return getIterations() / (float) getThreads();
    }

    /**
     * {@inheritDoc}
     * <p>
     * @return the {@code total} number of iterations performed in
     * {@code flood workers} across all {@link Floodgate} objects.
     */
    @Override
    public int getIterations() {
        return floodgates.stream()
                .mapToInt(fg -> fg.getIterations() * fg.getThreads())
                .sum();
    }

    /**
     * {@inheritDoc}
     * <p>
     * @return the {@code total} number of threads (active {@code flood workers})
     * in all {@link Floodgate} objects.
     */
    @Override
    public int getThreads() {
        return floodgates.stream()
                .mapToInt(Floodgate::getThreads)
                .sum();
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@link Torrent} opens all {@link Floodgate} objects in preparation for
     * {@code flooding}.
     */
    @Override
    public boolean open() {
        if (state == States.CLOSED) {
            floodgates.forEach(Floodgate::open);
            state = States.OPENED;
        } else {
            throw new IllegalStateException(String.format("Torrent not closed, state=%s",state));
        }
        return true;
    }

    /**
     * @return the number of {@link Floodgate} objects in this {@link Torrent}
     * object.
     */
    public int size() {
        return floodgates.size();
    }

    @Override
    public String toString() {
        return String.format("[target=%s,state=%s,floodgates=%d,flood-marshal=External]",getTarget(),state,size());
    }

    /**
     * Closes and releases all allocated resources pertaining to {@link
     * Floodgate} objects.
     * <p>
     * If the {@code force} parameter is {@code true}, an attempt is made to
     * shutdown the internal pool of {@code flood workers}, and if there any
     * still processing, rather than waiting they will be shutdown immediately.
     * <p>
     * It's advisable to use the {@link Torrent#close()} and allow this
     * object to take the correct course of action. This method is provided
     * for unit tests purposes, hence {@code default} access-level -- do not
     * alter this.
     *
     * @param force {@code true} to force shutdown of all {@code flood
     * workers}.
     */
    void close(final boolean force) {
        if (state == States.OPENED) {
            service.close(force);
            state = States.CLOSED;
        } else {
            throw new IllegalStateException(String.format("Torrent not opened, state=%s",state));
        }
    }

    /**
     * {@link FloodExecutorService} implementation for {@link Torrent} objects
     * to influence submission order of {@link FloodWorker}.
     * <p>
     * Unlike {@link FloodThreadPoolExecutor}, the submission order of the
     * tasks into the {@code core pool} is by first come, first served. The
     * problem with this approach is that although the workers commence their
     * work at the same time, the order in which they are submitted subtly
     * influences thread scheduler. For example, when {@code Floodgate A} with 5
     * workers is submitted before {@code Floodgate B} with 5 workers, there
     * is a likelihood of {@code Floodgate A} workers having a nanosecond or two
     * chance of starting before {@code Floodgate B} workers, despite all
     * worker threads commence their work at the "same time".
     * <p>
     * To encourage the {@code thread pool} to fairly distribute
     * {@link FloodWorker} objects in the {@code core pool} and therefore the
     * scheduler, the priority of the {@link FloodWorker} is used. Subsequently,
     * when this {@code thread pool} has received all necessary tasks, they are
     * then sorted into priority order then submitted to he {@code core pool},
     * thus doing away with the first come, first served algorithm. This
     * solution is better but not perfect as it is near impossible to have
     * exclusive control over the tread scheduler.
     * <p>
     * @see FloodWorker
     * @see FloodExecutorService
     * @see FloodThreadPoolExecutor
     */
    static class TorrentFloodThreadPoolExecutor extends FloodThreadPoolExecutor {

        /**
         * Creates an instance of this thread pool.
         *
         * @param threads number threads in {@code core pool}
         */
        public TorrentFloodThreadPoolExecutor(int threads) {
            super(null,threads);
        }

        /**
         * {@inheritDoc}
         * <p>
         * In this implementation, the tasks are not immediately submitted to the
         * {@code core pool} until all tasks have been submitted upon which they
         * are then sorted into {@code priority} order before {@code core pool}
         * submission. This enables fairer distribution of tasks in the
         * {@code core pool}.
         * <p>
         * @param task to be submitted.
         * @param <T> type of value returned from task.
         * @return a Future representing pending completion of the task
         */
        @Override
        public <T> Future<T> submit(final Callable<T> task) {
            Future<T> ftask = newTaskFor(task);
            futures.add(ftask);
            if (futures.size() == getCorePoolSize()) {
                Collections.sort(Generics.unchecked(futures));
                futures.forEach(f -> execute((RunnableFuture<?>) f));
            }
            return ftask;
        }
    }


    /**
     * Creates an immutable {@link Floodgate} object to allow clients to review
     * its state, whether pre or post-flood.
     *
     * @param <T> Type of value returned from the {@code target's} {@code
     * resources}.
     * @see Torrent#iterator()
     * @see Torrent#toList()
     */
    static class UnmodifiableFloodgate<T> implements ConcurrentResourceFloodStability<List<T>> {
        private final Floodgate<T> delegate;

        private UnmodifiableFloodgate(final Floodgate<T> floodgate) {
            this.delegate = floodgate;
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException();
        }
        @Override
        public boolean open() {
            throw new UnsupportedOperationException();
        }
        @Override
        public List<T> flood() {
            throw new UnsupportedOperationException();
        }
        @Override
        public States getState() {
            return delegate.getState();
        }
        @Override
        public Target getTarget() {
            return delegate.getTarget();
        }
        @Override
        public int getThreads() {
            return delegate.getThreads();
        }
        @Override
        public int getIterations() {
            return delegate.getIterations();
        }
        @Override
        public String toString() {
            return delegate.toString();
        }
    }

    /**
     * Supporting parent parameter class of the {@link TorrentBuilder} class.
     *
     * @param <T> Type of {@code target} object under test.
     */
    @Getter
    @AllArgsConstructor
    private static class FloodgateParameters<T> {
        private final Class<T> clazz;
        private final String tag;
        private final int threads;
        private final int iterations;
    }

    /**
     * Supporting parameter class of the {@link TorrentBuilder} class for {@link
     * Runnable} objects.
     *
     * @param <T> Type of {@code target} object under test.
     * @param <U> Type of {@code resource} function with which to {@code target}
     *           the underlying {@code resource}.
     */
    @Getter
    private static class RunnableFloodgateParameters<U extends Runnable,T> extends FloodgateParameters<T> {
        private final U resource;
        private RunnableFloodgateParameters(Class<T> clazz, String tag, int threads, int iterations, U resource) {
            super(clazz,tag,threads,iterations);
            this.resource = resource;
        }
    }

    /**
     * Supporting parameter class of the {@link TorrentBuilder} class for {@link
     * Supplier} objects.
     *
     * @param <T> Type of {@code target} object under test.
     * @param <U> Type of {@code resource} function with which to {@code target}
     *           the underlying {@code resource}.
     */
    @Getter
    private static class SupplierFloodgateParameters<U extends Supplier<?>,T> extends FloodgateParameters<T> {
        private final U resource;
        private SupplierFloodgateParameters(Class<T> clazz, String tag, int threads, int iterations, U resource) {
            super(clazz,tag,threads,iterations);
            this.resource = resource;
        }
    }

    /**
     * The builder class to facilitate the creation of {@link Torrent} objects.
     * <p>
     * @param <T> Type of {@code target} object currently being subjected to
     *           tests.
     */
    public final static class TorrentBuilder<T> {
        private final List<FloodgateParameters<T>> parameters;
        private final Class<T> clazz;
        private final int threads;
        private final int iterations;

        /**
         * Constructs an instance of this builder.
         * <p>
         * Both {@code threads} and {@code iterations} parameters can serve as
         * default values for all {@code resources} added in this {@code builder},
         * unless otherwise specified.
         *
         * @param clazz class of {@code target} object currently being subjected
         *             to tests.
         * @param threads number of thread workers required.
         * @param iterations number of iterations/repetitions performed by each
         *                   {@code flood worker}
         */
        private TorrentBuilder(Class<T> clazz, int threads, int iterations) {
            this.parameters = new ArrayList<>();
            this.clazz = clazz;
            this.threads = threads;
            this.iterations = iterations;
        }

        /**
         * Instructs the {@link TorrentBuilder} builder to construct an instance
         * of {@link Floodgate} object with a {@link Runnable} {@code resource}.
         * <p>
         * Both {@code threads} and {@code iterations} are defaulted to respective
         * parameters provided in the {@link TorrentBuilder#builder(Class, int, int)}
         * factory method.
         * <p>
         * {@code Tag} not required for the {@link Floodgate} object.
         * <p>
         * @param resource a {@link Runnable} {@code resource} with which to
         * {@code target}.
         * @return this {@link TorrentBuilder}.
         */
        public TorrentBuilder<T> withFloodgate(Runnable resource) {
            return withFloodgate(UNTAGGED,resource);
        }

        /**
         * Instructs the {@link TorrentBuilder} builder to construct an instance
         * of {@link Floodgate} object with a {@link Supplier} {@code resource}.
         * <p>
         * Both {@code threads} and {@code iterations} are defaulted to respective
         * parameters provided in the {@link TorrentBuilder#builder(Class, int, int)}
         * factory method.
         * <p>
         * {@code Tag} not required for the {@link Floodgate} object.
         *
         * @param resource a {@link Supplier} {@code resource} with which to
         * {@code target}.
         * @param <U> the target object to undergo tests.
         * @return this {@link TorrentBuilder}.
         */
        public <U> TorrentBuilder<T> withFloodgate(Supplier<U> resource) {
            return withFloodgate(UNTAGGED,resource);
        }

        /**
         * Instructs the {@link TorrentBuilder} builder to construct an instance
         * of {@link Floodgate} object with a {@link Runnable} {@code resource}.
         * <p>
         * To override {@code threads} and {@code iterations} defaults, supply them
         * in the parameters.
         * <p>
         * {@code Tag} not required for the {@link Floodgate} object.
         * <p>
         * @param threads override {@code thread} parameter default.
         * @param iterations  override {@code iterations} parameter default.
         * @param resource a {@link Runnable} {@code resource} with which to
         * {@code target}.
         * @return this {@link TorrentBuilder}.
         */
        public TorrentBuilder<T> withFloodgate(int threads, int iterations, Runnable resource) {
            return withFloodgate(UNTAGGED,threads,iterations,resource);
        }

        /**
         * Instructs the {@link TorrentBuilder} builder to construct an instance
         * of {@link Floodgate} object with a {@link Supplier} {@code resource}.
         * <p>
         * To override {@code threads} and {@code iterations} defaults, supply them
         * in the parameters.
         * <p>
         * {@code Tag} not required for the {@link Floodgate} object.
         * <p>
         * @param threads override {@code thread} parameter default.
         * @param iterations  override {@code iterations} parameter default.
         * @param resource a {@link Supplier} {@code resource} with which to
         * {@code target}.
         * @param <U> the target object to undergo tests.
         * @return this {@link TorrentBuilder}.
         */
        public <U> TorrentBuilder<T> withFloodgate(int threads, int iterations, Supplier<U> resource) {
            return withFloodgate(UNTAGGED,threads,iterations,resource);
        }

        /**
         * Instructs the {@link TorrentBuilder} builder to construct an instance
         * of {@link Floodgate} object with a {@link Runnable} {@code resource}.
         * <p>
         * Both {@code threads} and {@code iterations} are defaulted to respective
         * parameters provided in the {@link TorrentBuilder#builder(Class, int, int)}
         * factory method.
         * <p>
         * @param tag supply a meaningful name for the {@code resource} for
         *            reporting purposes.
         * @param resource a {@link Runnable} {@code resource} with which to
         * {@code target}.
         * @return this {@link TorrentBuilder}.
         */
        public TorrentBuilder<T> withFloodgate(String tag, Runnable resource) {
            parameters.add(new RunnableFloodgateParameters<>(clazz,tag,threads,iterations,resource));
            return this;
        }

        /**
         * Instructs the {@link TorrentBuilder} builder to construct an instance
         * of {@link Floodgate} object with a {@link Supplier} {@code resource}.
         * <p>
         * Both {@code threads} and {@code iterations} are defaulted to respective
         * parameters provided in the {@link TorrentBuilder#builder(Class, int, int)}
         * factory method.
         * {@code Tag} not required for the {@link Floodgate} object.
         * <p>
         * @param tag supply a meaningful name for the {@code resource} for
         *            reporting purposes.
         * @param resource a {@link Supplier} {@code resource} with which to
         * {@code target}.
         * @param <U> the target object to undergo tests.
         * @return this {@link TorrentBuilder}.
         */
        public <U> TorrentBuilder<T> withFloodgate(String tag, Supplier<U> resource) {
            parameters.add(new SupplierFloodgateParameters<>(clazz,tag,threads,iterations,resource));
            return this;
        }

        /**
         * Instructs the {@link TorrentBuilder} builder to construct an instance
         * of {@link Floodgate} object with a {@link Runnable} {@code resource}.
         * <p>
         * To override {@code threads} and {@code iterations} defaults, supply them
         * in the parameters.
         * <p>
         * {@code Tag} not required for the {@link Floodgate} object.
         * <p>
         * @param tag supply a meaningful name for the {@code resource} for
         *            reporting purposes.
         * @param threads override {@code thread} parameter default.
         * @param iterations  override {@code iterations} parameter default.
         * @param resource a {@link Runnable} {@code resource} with which to
         * {@code target}.
         * @return this {@link TorrentBuilder}.
         */
        public TorrentBuilder<T> withFloodgate(String tag, int threads, int iterations, Runnable resource) {
            parameters.add(new RunnableFloodgateParameters<>(clazz,tag,threads,iterations,resource));
            return this;
        }

        /**
         * Instructs the {@link TorrentBuilder} builder to construct an instance
         * of {@link Floodgate} object with a {@link Supplier} {@code resource}.
         * <p>
         * To override {@code threads} and {@code iterations} defaults, supply them
         * in the parameters.
         * <p>
         * {@code Tag} not required for the {@link Floodgate} object.
         * <p>
         * @param tag supply a meaningful name for the {@code resource} for
         *            reporting purposes.
         * @param threads override {@code thread} parameter default.
         * @param iterations  override {@code iterations} parameter default.
         * @param resource a {@link Supplier} {@code resource} with which to
         * {@code target}.
         * @param <U> the target object to undergo tests.
         * @return this {@link TorrentBuilder}.
         */
        public <U> TorrentBuilder<T> withFloodgate(String tag, int threads, int iterations, Supplier<U> resource) {
            parameters.add(new SupplierFloodgateParameters<>(clazz,tag,threads,iterations,resource));
            return this;
        }

        /**
         * Builds the {@link Torrent} object from the supplied {@code parameter}
         * objects.
         *
         * @return a {@link Torrent} object constructed with {@link Floodgate}
         * objects bearing the values supplied by the parameters.
         * @throws IllegalArgumentException if no parameters supplied.
         */
        public Torrent build() {
            if (parameters.size() == 0)
                throw new IllegalArgumentException("Torrent has nothing to do");

            int threads = parameters.stream()
                    .mapToInt(FloodgateParameters::getThreads)
                    .sum();
            TorrentFloodThreadPoolExecutor service = new TorrentFloodThreadPoolExecutor(threads);
            Torrent result = new Torrent(service);
            service.setTarget(result.getTarget());

            parameters.forEach(p -> {
                Floodgate<?> floodgate;
                if (p instanceof RunnableFloodgateParameters) {
                    floodgate = new Floodgate<>(p.getClazz(), p.getTag(), p.getThreads(), p.getIterations(),
                            () -> {((RunnableFloodgateParameters<Runnable,T>) p).getResource().run(); return null;},
                            service,result.floodMarshal);
                } else {
                    floodgate = new Floodgate<>(p.getClazz(),p.getTag(), p.getThreads(), p.getIterations(),
                            ((SupplierFloodgateParameters<Supplier<?>,T>) p).getResource(),
                            service,result.floodMarshal);
                }
                result.floodgates.add(floodgate);

            });

            return result;
        }
    }

    /**
     * Factory method supplying a {@code builder} object with which to construct
     * a {@link Torrent} object.
     * <p>
     * {@link Floodgate#DEFAULT_FLOOD_WORKERS} and {@link
     * Floodgate#DEFAULT_FLOOD_ITERATIONS} values serve as default parameters
     * for {@code threads} and {@code iterations} respectively, and are applied
     * in scenarios where those values are absent in the {@code with...} factory
     * methods. If necessary, factory methods are available to override these
     * values.
     *
     * @param clazz the class type of the {@code target} under test.
     * @param <T> Type of {@code target} object currently being subjected to
     *           tests.
     * @return TorrentBuilder object.
     * @see TorrentBuilder#withFloodgate(Runnable)
     * @see TorrentBuilder#withFloodgate(Supplier)
     */
    public static <T> TorrentBuilder<T> builder(final Class<T> clazz) {
        return builder(clazz,Floodgate.DEFAULT_FLOOD_WORKERS,Floodgate.DEFAULT_FLOOD_ITERATIONS);
    }

    /**
     * Factory method supplying a {@code builder} object with which to construct
     * a {@link Torrent} object.
     * <p>
     * {@code threads} and {@code iterations} values serve as default parameters,
     * and are applied in scenarios where those values are absent in the {@code
     * with...} factory methods. If necessary, factory methods are available to
     * override these values.
     *
     * @param clazz the class type of the {@code target} under test.
     * @param threads number of threads.
     * @param iterations  number of iterations each thread must carry out.
     * @param <T> Type of {@code target} object currently undergoing tests.
     *
     * @return TorrentBuilder object.
     * @see TorrentBuilder#withFloodgate(Runnable)
     * @see TorrentBuilder#withFloodgate(Supplier)
     */
    public static <T> TorrentBuilder<T> builder(final Class<T> clazz, final int threads, final int iterations) {
        Class<T> c = Objects.requireNonNull(clazz);
        return new TorrentBuilder<>(c,threads,iterations);
    }
}
