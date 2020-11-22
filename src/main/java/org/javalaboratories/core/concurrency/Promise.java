package org.javalaboratories.core.concurrency;

import lombok.EqualsAndHashCode;
import org.javalaboratories.core.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.javalaboratories.core.concurrency.Promise.States.*;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SuppressWarnings("WeakerAccess")
public class Promise<T> {

    private final Logger logger = LoggerFactory.getLogger(Promise.class);

    public enum States { PENDING, FULFILLED, REJECTED }

    private final AbstractAction action;
    private final PromisePoolService service;
    @EqualsAndHashCode.Include
    private final String identity;
    private CompletableFuture<T> future;

    Promise(final PromisePoolService service, final PrimaryAction<T> action) {
        this(service,action,null);
    }

    private Promise(final PromisePoolService service, final AbstractAction<?> action, final CompletableFuture<T> future) {
        Objects.requireNonNull(action,"No service object?");
        Objects.requireNonNull(action,"No action object?");
        this.service = service;
        this.future = future;
        this.action = action;
        this.identity = String.format("promise-{%s}", UUID.randomUUID());
    }

    public final Promise<T> then(final Action<T> action) {
        Consumer<T> actionable = doMakeActionable(action);
        CompletableFuture<Void> future = this.future.thenAcceptAsync(actionable,service)
                .whenComplete((value,exception) -> action.getResult()
                        .ifPresent(result -> result.accept(value, exception)));

        return new Promise<>(service,action,toFuture(future));
    }

    public final <R> Promise<R> then(final TransmuteAction<T,R> action) {
        Function<T,R> transmutable = doMakeTransmutable(action);
        CompletableFuture<R> future = this.future.thenApplyAsync(transmutable,service)
                .whenComplete((newValue,exception) -> action.getResult()
                        .ifPresent(result -> result.accept(newValue, exception)));

        return new Promise<>(service,action,future);
    }

    public States getState() {
        return getState(future);
    }

    public final String getIdentity() {
        return identity;
    }

    public final Nullable<T> getResult() {
        T value = null;
        try {
            value = future.get();
        } catch (InterruptedException | ExecutionException e) {
            // Ignore, return optional object instead.
        }
        return Nullable.ofNullable(value);
    }

    public Promise<T> handle(Consumer<Throwable> handle) {
        Objects.requireNonNull(handle,"No handle object?");
        try {
            future.join();
        } catch (CompletionException e) {
            handle.accept(e.getCause());
        }
        return new Promise<>(service,action,future);
    }

    @Override
    public String toString() {
        return String.format("[identity=%s,state=%s,service=%s]",identity,getState(),service);
    }

    protected AbstractAction<T> getAction() {
        @SuppressWarnings("unchecked")
        AbstractAction<T> action = this.action;
        return action;
    }

    protected CompletableFuture<T> doPrimaryActionAsync(final PrimaryAction<T> action) {
        return CompletableFuture.supplyAsync(action.getTask().orElseThrow(),service)
                .whenComplete((value,exception) -> action.getResult()
                        .ifPresent(consumer -> consumer.accept(value, exception)));
    }

    /**
     * Invokes this promise's primary action asynchronously. The method is
     * part of the life-cycle of this object, and therefore must not be called
     * in any other context. This is why the access level is set to package
     * default, and must remain so.
     *
     * @return true is returned if action is executed asynchronously.
     */
    final boolean invokePrimaryActionAsync(PrimaryAction<T> action) {
        future = doPrimaryActionAsync(action);
        logger.debug("Promise [{}] invoked action asynchronously successfully",getIdentity());
        return true;
    }

    private Consumer<T> doMakeActionable(final Action<T> action) {
        return (value) -> {
            Consumer<T> result = action.getTask().orElseThrow();
            result.accept(value);
        };
    }

    private <R> Function<T,R> doMakeTransmutable(final TransmuteAction<T,R> action) {
        return (value) -> {
            Function<T,R> result = action.getTransmute().orElseThrow();
            return result.apply(value);
        };
    }

    private States getState(final CompletableFuture<?super T> future) {
        return future == null ? PENDING : future.isCompletedExceptionally() ? REJECTED : FULFILLED;
    }

    private static <T> CompletableFuture<T> toFuture(CompletableFuture<?> future) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T> result = (CompletableFuture<T>) future;
        return result;
    }
}
