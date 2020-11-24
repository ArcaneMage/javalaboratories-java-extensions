package org.javalaboratories.core.concurrency;

import lombok.EqualsAndHashCode;
import org.javalaboratories.core.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

@EqualsAndHashCode(callSuper=false)
public final class TransmuteAction<T,R> extends AbstractAction<R> {
    private final Function<T,R> transmute;

    private TransmuteAction(final Function<T,R> transmute) {
        this(transmute, null);
    }

    private TransmuteAction(final Function<T,R> transmute, final BiConsumer<R,Throwable> completeHandler) {
        super(completeHandler);
        this.transmute = transmute;
    }

    public static <T,R> TransmuteAction<T,R> of(Function<T,R> transmute) {
        return new TransmuteAction<>(transmute);
    }

    public static <T,R> TransmuteAction<T,R> of(Function<T,R> transmute, BiConsumer<R,Throwable> completeHandler) {
        return new TransmuteAction<>(transmute,completeHandler);
    }

    public Nullable<Function<T,R>> getTransmute() {
        return Nullable.ofNullable(transmute);
    }
}
