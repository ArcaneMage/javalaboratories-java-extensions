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

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Getter
public final class Torrent extends AbstractResourceFloodTester<Map<String,List<?>>> {

    private static final Logger logger = LoggerFactory.getLogger(Torrent.class);

    @Getter(AccessLevel.NONE)
    private final List<Floodgate<?>> floodgates;
    @Getter(AccessLevel.NONE)
    private final FloodController floodController;

    private States state;

    private Torrent() {
        super();
        floodgates = new ArrayList<>();
        floodController = new ExternalFloodController() {
            final CountDownLatch latch = new CountDownLatch(1);
            @Override
            public void halt() throws InterruptedException {
                latch.await();
            }
            @Override
            public void flood() {
                logger.info("{}: Torrent is flooding all floodgates simultaneously",getTarget().getName());
                latch.countDown();
            }
        };
        state = States.CLOSED;
    }

    @Override
    public void close() {
        close(false);
    }

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

    @Override
    public Map<String,List<?>> flood() {
        if (state != States.OPENED)
            throw new IllegalStateException(String.format("Torrent not open, state=%s",state));

        Map<String, List<?>> result = null;
        try {
            CompletableFuture<Map<String, List<?>>> cfuture = CompletableFuture
                    .supplyAsync(() -> {
                        Map<String,List<?>> response = new HashMap<>();
                        floodgates.forEach(fg -> response.put(fg.getTarget().getName(),fg.flood()));
                        return response;
                    })
                    .whenComplete((v, e) -> {
                        if (e != null) {
                            logger.error("{}: An error was encountered in one of the floodgates {}", getTarget().getName(),
                                    e.getMessage());
                        } else {
                            logger.info("{}: Number of floodgates completed: {}",getTarget().getName(),
                                    v.size());
                        }
                    });
            floodController.flood();
            try {
                result = cfuture.get();
                floodgates.stream()
                    .filter(fg -> fg.getTarget().getStability() == Target.Stability.UNSTABLE)
                    .findFirst()
                    .ifPresent(fg -> logger.info("{}: Floodgate(s) encountered unstable target(s) -- review failures",
                            getTarget().getName()));

            } catch (InterruptedException | ExecutionException ignored) {
            }
        } finally {
            state = States.FLOODED;
        }
        return result;
    }

    public List<ConcurrentResourceFloodTester<?>> floodgates() {
        List<ConcurrentResourceFloodTester<?>> result = new ArrayList<>();
        forEach(result::add);
        return Collections.unmodifiableList(result);
    }

    public void forEach(final Consumer<? super ConcurrentResourceFloodTester<?>> consumer) {
        Objects.requireNonNull(consumer);
        floodgates.forEach(fg -> consumer.accept(new UnmodifiableFloodgate<>(fg)));
    }

    public int size() {
        return floodgates.size();
    }

    @Override
    public String toString() {
        String controller = floodController instanceof ExternalFloodController ? "External" : "Internal";
        return String.format("[target=%s,state=%s,floodgates=%d,flood-controller=%s]",getTarget(),state,size(),
                controller);
    }

    void close(final boolean force) {
        if (state == States.OPENED) {
            floodgates.forEach(fg -> fg.close(force));
            state = States.CLOSED;
        } else {
            throw new IllegalStateException(String.format("Torrent not opened, state=%s",state));
        }
    }

    @Getter
    @AllArgsConstructor
    private static class FloodgateParameters<T> {
        private final Class<T> clazz;
        private final int threads;
        private final int iterations;
    }

    @Value
    @EqualsAndHashCode(callSuper=true)
    private static class RunnableFloodgateParameters<U extends Runnable,T> extends FloodgateParameters<T> {
        U resource;
        RunnableFloodgateParameters(Class<T> clazz, int threads, int iterations, U resource) {
            super(clazz,threads,iterations);
            this.resource = resource;
        }
    }

    @Value
    @EqualsAndHashCode(callSuper=true)
    private static class SupplierFloodgateParameters<U extends Supplier<?>,T> extends FloodgateParameters<T> {
        U resource;
        SupplierFloodgateParameters(Class<T> clazz, int threads, int iterations, U resource) {
            super(clazz,threads,iterations);
            this.resource = resource;
        }
    }

    final static class UnmodifiableFloodgate<T> implements ConcurrentResourceFloodTester<List<T>> {
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


    final static class TorrentBuilder<T> {
        private final List<FloodgateParameters<T>> parameters;
        private final Class<T> clazz;
        private final int threads;
        private final int iterations;

        private TorrentBuilder(Class<T> clazz, int threads, int iterations) {
            this.parameters = new ArrayList<>();
            this.clazz = clazz;
            this.threads = threads;
            this.iterations = iterations;
        }

        public TorrentBuilder<T> withFloodgate(Runnable resource) {
            parameters.add(new RunnableFloodgateParameters<>(clazz,threads,iterations,resource));
            return this;
        }

        public <U> TorrentBuilder<T> withFloodgate(Supplier<U> resource) {
            parameters.add(new SupplierFloodgateParameters<>(clazz,threads,iterations,resource));
            return this;
        }

        public TorrentBuilder<T> withFloodgate(int threads, int iterations, Runnable resource) {
            parameters.add(new RunnableFloodgateParameters<>(clazz,threads,iterations,resource));
            return this;
        }

        public <U> TorrentBuilder<T> withFloodgate(int threads, int iterations, Supplier<U> resource) {
            parameters.add(new SupplierFloodgateParameters<>(clazz,threads,iterations,resource));
            return this;
        }

        public Torrent build() {
            if (parameters.size() == 0)
                throw new IllegalArgumentException("Torrent has nothing to do");

            Torrent result = new Torrent();
            parameters.forEach(p -> {
                Floodgate<?> floodgate;
                if (p instanceof RunnableFloodgateParameters) {
                    floodgate = new Floodgate<>(p.getClazz(), p.getThreads(), p.getIterations(),
                            () -> {((RunnableFloodgateParameters<Runnable,T>) p).getResource().run(); return null;},
                            result.floodController);
                } else {
                    floodgate = new Floodgate<>(p.getClazz(), p.getThreads(), p.getIterations(),
                            ((SupplierFloodgateParameters<Supplier<?>,T>) p).getResource(),
                            result.floodController);
                }
                result.floodgates.add(floodgate);
            });

            return result;
        }
    }

    public static <T> TorrentBuilder<T> builder(final Class<T> clazz) {
        return builder(clazz,Floodgate.DEFAULT_FLOOD_WORKERS,Floodgate.DEFAULT_FLOOD_ITERATIONS);
    }

    public static <T> TorrentBuilder<T> builder(final Class<T> clazz, final int threads, final int iterations) {
        Class<T> c = Objects.requireNonNull(clazz);
        return new TorrentBuilder<>(c,threads,iterations);
    }
}
