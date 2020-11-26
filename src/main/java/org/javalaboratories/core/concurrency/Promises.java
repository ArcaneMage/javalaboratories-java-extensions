package org.javalaboratories.core.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is a factory for creating {@link Promise} objects.
 * <p>
 * Its role is to ensure {@link PromisePoolServiceFactory},
 * {@link PromisePoolService} and other objects are properly initialised and
 * ready for the production of {@link Promise} objects. For flexibility, the
 * thread pool service can swapped out for an alternative executor -- configure
 * the concrete implementation in the {@code promise-configuration.properties}
 * file; factory methods also provide a means to create custom {@link Promise}
 * objects.
 * <p>
 * A typical example for creating promise objects is shown:
 *
 * <pre>
 *     {@code
 *          Promise<String> promise = Promises
 *              .newPromise(PrimaryAction.of(() -> doLongRunningTask("Reading integer value from database")))
 *              .then(TransmuteAction.of(value -> "Value read from the database: "+value));
 *
 *          String result = promise.getResult()
 *             .IfPresent(result -> System.out::println(result));
 *     }
 * </pre>
 */
@SuppressWarnings("WeakerAccess")
public final class Promises {

    private static PromisePoolService promisePoolService;

    static {
        PromisePoolServiceFactory factory = new PromisePoolServiceFactory(new PromiseConfiguration());
        promisePoolService = factory.newPoolService();
    }

    private Promises() {}

    public static <T> List<Promise<T>> all(final List<PrimaryAction<T>> actions) {
        return all(actions,(action) -> () -> new Promise<>(promisePoolService,action));
    }

    public static <T,U extends Promise<T>> List<Promise<T>> all(final List<PrimaryAction<T>> actions, final Function<PrimaryAction<T>,Supplier<U>> function ) {
        Objects.requireNonNull(actions);
        Objects.requireNonNull(function);
        List<Promise<T>> results = new ArrayList<>();
        actions.forEach(a -> {
            Promise<T> promise = newPromise(a,function.apply(a));
            results.add(promise);
        });
        return Collections.unmodifiableList(results);
    }

    public static <T> Promise<T> newPromise(final PrimaryAction<T> action) {
        return newPromise(action, () -> new Promise<>(promisePoolService,action));
    }

    public static <T, U extends Promise<T>> Promise<T> newPromise(final PrimaryAction<T> action, final Supplier<U> supplier) {
        Objects.requireNonNull(action,"Cannot keep promise -- no action?");
        Promise<T> result = supplier.get();
        result.invokePrimaryAction(action);
        return result;
    }
}
