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
import org.javalaboratories.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

@Getter
public final class Torrent implements MultithreadedFloodTester<Map<String,List<?>>> {

    private static final Logger logger = LoggerFactory.getLogger(Torrent.class);

    @Getter(AccessLevel.NONE)
    private final List<Floodgate<?>> floodgates;
    @Getter(AccessLevel.NONE)
    private final FloodController floodManagement;

    private States state;

    private Torrent() {
        floodgates = new ArrayList<>();
        floodManagement = new ExternalFloodController() {
            final CountDownLatch latch = new CountDownLatch(1);
            @Override
            public void halt() {
                try {
                    latch.await();
                } catch (InterruptedException ignore) {}
            }
            @Override
            public void flood() {
                logger.info("Torrent is opening all floodgates simultaneously");
                latch.countDown();
            }
        };
        state = States.CLOSED;
    }

    @Getter
    @AllArgsConstructor
    static class FloodgateParameters<T> {
        private final Class<T> clazz;
        private final int threads;
        private final int iterations;
    }

    @Value
    @EqualsAndHashCode(callSuper=true)
    static class RunnableFloodgateParameters<T> extends FloodgateParameters<T> {
        Runnable target;
        RunnableFloodgateParameters(Class<T> name, int threads, int iterations, Runnable target) {
            super(name,threads,iterations);
            this.target = target;
        }
    }

    @Value
    @EqualsAndHashCode(callSuper=true)
    static class SupplierFloodgateParameters<U extends Supplier<?>,T> extends FloodgateParameters<T> {
        U target;
        SupplierFloodgateParameters(Class<T> name, int threads, int iterations, U target) {
            super(name,threads,iterations);
            this.target = target;
        }
    }

    private static class TorrentBuilder<T> {
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

        public TorrentBuilder<T> withFloodgates(Runnable target) {
            parameters.add(new RunnableFloodgateParameters<>(clazz,threads,iterations,target));
            return this;
        }

        public <U> TorrentBuilder<T> withFloodgates(Supplier<U> target) {
            parameters.add(new SupplierFloodgateParameters<>(clazz,threads,iterations,target));
            return this;
        }

        public TorrentBuilder<T> withFloodgates(int threads, int iterations, Runnable target) {
            parameters.add(new RunnableFloodgateParameters<>(clazz,threads,iterations,target));
            return this;
        }

        public <U> TorrentBuilder<T> withFloodgates(int threads, int iterations, Supplier<U> target) {
            parameters.add(new SupplierFloodgateParameters<>(clazz,threads,iterations,target));
            return this;
        }

        public Torrent build() {
            Torrent result = new Torrent();

            parameters.forEach(p -> {
                Floodgate<?> floodgate;
                if ( p instanceof RunnableFloodgateParameters) {
                    floodgate = new Floodgate<>(p.getClazz(), p.getThreads(), p.getIterations(),
                            Generics.unchecked(((RunnableFloodgateParameters<?>) p).getTarget()), result.floodManagement);
                } else {
                    floodgate = new Floodgate<>(p.getClazz(), p.getThreads(), p.getIterations(),
                            ((SupplierFloodgateParameters<Supplier<?>,T>) p).getTarget(), result.floodManagement);
                }
                result.floodgates.add(floodgate);
            });

            return result;
        }
    }

    public static <T> TorrentBuilder<T> builder(Class<T> clazz) {
        return builder(clazz,Floodgate.DEFAULT_FLOOD_WORKERS,Floodgate.DEFAULT_FLOOD_ITERATIONS);
    }

    public static <T> TorrentBuilder<T> builder(Class<T> clazz, int threads, int iterations) {
        return new TorrentBuilder<>(clazz,threads,iterations);
    }

    public boolean open() {
        if (state == States.CLOSED) {
            floodgates.forEach(Floodgate::open);
            state = States.OPENED;
        } else {
            throw new IllegalStateException(String.format("Torrent not closed, state=%s",state));
        }
        return true;
    }

    public Map<String,List<?>> flood() {
        if (state != States.OPENED)
            throw new IllegalStateException(String.format("Torrent not open, state=%s",state));

        final Map<String, List<?>> result = new HashMap<>();
        try {
            CompletableFuture<Map<String, List<?>>> cfuture = CompletableFuture
                    .supplyAsync(() -> {
                        floodgates.forEach(fg -> result.put(fg.getName(), fg.flood()));
                        return result;
                    })
                    .whenComplete((v, e) -> {
                        if (e != null) {
                            logger.error("An error was encountered in one of the floodgates {}", e.getMessage());
                        } else {
                            logger.info("{} floodgate(s) completed successfully,", v.size());
                        }
                    });

            floodManagement.flood();
            try {
                cfuture.get();
            } catch (InterruptedException | ExecutionException ignored) { }
        } finally {
            state = States.FLOODED;
        }
        return result;
    }
}
