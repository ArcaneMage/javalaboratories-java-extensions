package org.javalaboratories.core.concurrency;

import lombok.AllArgsConstructor;
import org.javalaboratories.core.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@AllArgsConstructor()
public abstract class AbstractAction<R> implements CompletableFuture.AsynchronousCompletionTask {
    private final BiConsumer<R,Throwable> result;

    public Nullable<BiConsumer<R,Throwable>> getResult() {
        return Nullable.ofNullable(result);
    }
}