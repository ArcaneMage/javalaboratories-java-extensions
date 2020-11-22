package org.javalaboratories.core.concurrency;

import lombok.EqualsAndHashCode;
import org.javalaboratories.core.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@EqualsAndHashCode(callSuper=false)
public final class Action<T> extends AbstractAction<Void> {
    private final Consumer<T> task;

    private Action(final Consumer<T> task) {
        this(task, null);
    }

    private Action(final Consumer<T> task, final BiConsumer<Void,Throwable> result) {
        super(result);
        this.task = task;
    }

    public static <T> Action<T> of(Consumer<T> task) {
        return new Action<>(task);
    }

    public static <T> Action<T> of(Consumer<T> task, BiConsumer<Void,Throwable> result) {
        return new Action<>(task, result);
    }

    public Nullable<Consumer<T>> getTask() {
        return Nullable.ofNullable(task);
    }
}
