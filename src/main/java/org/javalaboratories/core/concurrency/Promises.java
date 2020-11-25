package org.javalaboratories.core.concurrency;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
public final class Promises {

    private static final String PROMISE_CONFIGURATION_FILE="promise-configuration.properties";

    private static PromisePoolService promisePoolService = createPromisePoolService(DefaultPromisePoolServiceFactory::new);

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

    private static <T extends PromisePoolService> T createPromisePoolService(Function<Properties,PromisePoolServiceFactory<T>> function) {
        Objects.requireNonNull(function);
        Properties properties = new Properties();
        try {
            properties.load(Promises.class.getClassLoader().getResourceAsStream(PROMISE_CONFIGURATION_FILE));
        } catch (IOException e) {
            // Do-nothing, file I/O error will result in system defaults being applied
        }
        PromisePoolServiceFactory<T> factory = function.apply(properties);
        return factory.newPoolService();
    }
}
