package org.javalaboratories.core.event;

public interface EventSubscribable<T extends EventSubscriber<? extends Event>> {
    /**
     * Registers the {@link EventSubscriber} with a {@code Broadcaster}.
     * <p>
     * The {@code captureEvents} parameter explicitly state the events the
     * {@link EventSubscriber} object is particularly interested in. Each
     * {@link Event} must be uniquely identifiable -- ensure the {@code equals
     * and hashCode} of the {@code Event} are implemented.
     * <p>
     * Note: It is not possible to subscribe the same subscriber multiple times.
     *
     * @param subscriber the {@link EventSubscriber} object to register.
     * @throws NullPointerException if {@code subscriber} is null.
     * @throws IllegalArgumentException if {@code captureEvents} is null or less
     * than 1. Multiple subscription of the same subscriber is not possible.
     */
    void subscribe(final T subscriber);

    /**
     * Unregisters the {@link EventSubscriber} from a {@code Broadcaster}.
     * <p>
     * Unregistering the {@link EventSubscriber} means that the subscriber will
     * no longer receive notifications from the {@code Broadcaster}.
     *
     * @param subscriber the {@link EventSubscriber} object to unregister.
     * @return {@code true} for successful removal, {@code false} for unrecognised
     *         or unknown {@code subscriber}
     * @throws NullPointerException if {@code subscriber} is null.
     */
    boolean unsubscribe(final T subscriber);

    /**
     * @return number of subscribers registered with this {@code publisher}
     */
    int subscribers();
}
