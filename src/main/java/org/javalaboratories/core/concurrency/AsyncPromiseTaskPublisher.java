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
package org.javalaboratories.core.concurrency;

import org.javalaboratories.core.event.AbstractEvent;
import org.javalaboratories.core.event.EventBroadcaster;
import org.javalaboratories.core.event.EventPublisher;
import org.javalaboratories.core.event.EventSource;
import org.javalaboratories.core.event.EventSubscriber;
import org.javalaboratories.util.Arguments;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


class AsyncPromiseTaskPublisher<T> extends AsyncPromiseTask<T> implements EventSource {

    public static final PrimaryActionEvent PRIMARY_ACTION_EVENT = new PrimaryActionEvent();
    public static final TokenActionEvent TOKEN_ACTION_EVENT = new TokenActionEvent();
    public static final TransmuteActionEvent TRANSMUTE_ACTION_EVENT = new TransmuteActionEvent();

    private final EventPublisher<PromiseEventState<?>> publisher;

    AsyncPromiseTaskPublisher(final ManagedPoolService service, final PrimaryAction<T> action, final List<EventSubscriber<PromiseEventState<?>>> subscribers) {
        super(service,action);
        Arguments.requireNonNull(() -> new IllegalArgumentException("Arguments null?"),service,action,
                subscribers);
        publisher = new Publisher();
        subscribers.forEach(s -> publisher.subscribe(s,PRIMARY_ACTION_EVENT,TOKEN_ACTION_EVENT,
                TRANSMUTE_ACTION_EVENT));
    }

    /**
     * {@inheritDoc}
     */
    public Promise<T> then(final TaskAction<T> action) {
        Promise<T> result = super.then(action);
        result.getResult()
                .ifPresent(value -> {
                    PromiseEventState<Void> state = new PromiseEventState<>(null);
                    publisher.publish(TOKEN_ACTION_EVENT,state);
                });

        publisher.publish(TOKEN_ACTION_EVENT,null);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public final <R> Promise<R> then(final TransmuteAction<T,R> action) {
        Promise<R> result = super.then(action);
        result.getResult()
                .ifPresent(value -> {
                    PromiseEventState<R> state = new PromiseEventState<>(value);
                    publisher.publish(TRANSMUTE_ACTION_EVENT,state);
                });
        return result;
    }

    /**
     * {@inheritDoc}
     */
    protected CompletableFuture<T> invokePrimaryActionAsync(final PrimaryAction<T> action) {
        T result;
        CompletableFuture<T> future = super.invokePrimaryActionAsync(action);
        try {
            result = future.get();
            PromiseEventState<T> state = new PromiseEventState<>(result);
            publisher.publish(PRIMARY_ACTION_EVENT,state);
        } catch (CancellationException | ExecutionException | InterruptedException e) {
            // Ignore, return optional object instead.
        }
        return future;
    }

    private class Publisher extends EventBroadcaster<AsyncPromiseTaskPublisher<T>, PromiseEventState<?>> {
        private Publisher() {
            super(AsyncPromiseTaskPublisher.this);
        }
    }

    private static class TokenActionEvent extends AbstractEvent {
        public TokenActionEvent() {
            super();
        }
    }

    private static class TransmuteActionEvent extends AbstractEvent {
        public TransmuteActionEvent() {
            super();
        }
    }

    private static class PrimaryActionEvent extends AbstractEvent {
        public PrimaryActionEvent() {
            super();
        }
    }

}
