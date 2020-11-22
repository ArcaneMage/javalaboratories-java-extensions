package org.javalaboratories.core.concurrency;

import lombok.EqualsAndHashCode;
import org.javalaboratories.core.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@EqualsAndHashCode(callSuper=false)
public final class PrimaryAction<T> extends AbstractAction<T> {
    private final Supplier<T> task;

    private PrimaryAction(final Supplier<T> task) {
        this(task, null);
    }

    private PrimaryAction(final Supplier<T> task, final BiConsumer<T,Throwable> result) {
        super(result);
        this.task = task;
    }

    public static <T> PrimaryAction<T> of(Supplier<T> task) {
        return new PrimaryAction<>(task);
    }

    public static <T> PrimaryAction<T> of(Supplier<T> task, BiConsumer<T,Throwable> result) {
        return new PrimaryAction<>(task, result);
    }

    public Nullable<Supplier<T>> getTask() {
        return Nullable.ofNullable(task);
    }
}
