package org.javalaboratories.core.event;

/**
 * An object that implements this interface can notify its subscribed
 * dependencies with events.
 * <p>
 * This interface is the {@code Subject} component of the Observer Design
 * pattern as described in the "Design Patterns, Elements of Reusable Object-
 * Oriented Software (Gang-of-Four)" book. Therefore, it is a container of
 * of {@code Observers}, {@link EventSubscriber} objects. When an {@link Event},
 * in {@link EventPublisher} has occurred, it will then be published to all
 * {@link EventSubscriber} objects who are interested in the {@code event}.
 * Upon subscribing an {@link EventSubscriber}, it is necessary to specify
 * exactly which {@code events} to which the {@code subscriber} wants to
 * listen.
 * <p>
 * Use the {@link EventPublisher#subscribe(EventSubscriber, Event...)} method
 * to register an {@link EventSubscriber} object, indicating the events of
 * interest. The {@link EventPublisher#unsubscribe(EventSubscriber)} method
 * unregisters the {@link EventSubscriber} object from the {@link EventPublisher}.
 *
 * @param <T> Type of source in which the event originated.
 * @param <V> Type of value and/or state forwarded to the {@code subscribers}
 * @see Event
 * @see EventBroadcaster
 * @see EventSubscriber
 */
public interface EventPublisher<T extends EventSource,V> {
    /**
     * Publish an {@link Event} to interested {@link EventSubscriber} objects
     * when {@link EventPublisher} or the owning component which encapsulates
     * the {@code EventPublisher} state changes.
     * <p>
     * If the {@code subscriber} has not explicitly stated that it's
     * interested in a particular {@link Event}, it will not receive a
     * notification of that {@link Event}.
     * <p>
     * In situations where {@link EventSubscriber} throws an exception, the
     * {@link EventPublisher} object will consider the {@code subscriber} as
     * unstable/toxic and will automatically cancel its subscription, and then
     * continue notifying outstanding {@code subscribers}.
     *
     * @param event the {@link Event} object with which to publish to recipients.
     * @param value the state value when {@link EventPublisher} or the owning
     *              component which encapsulates the {@code EventPublisher}
     *              state changes.
     * @throws NullPointerException if {@code event} is null.
     */
    void publish(final Event event, final V value);

    /**
     * Registers the {@link EventSubscriber} with this {@link EventPublisher}.
     * <p>
     * The {@code captureEvents} parameter explicitly state the events the
     * {@link EventSubscriber} object is particularly interested in. Each
     * {@link Event} must be uniquely identifiable -- ensure the {@code equals
     * and hashCode} of the {@code Event} are implemented.
     * <p>
     * Note: It is not possible to subscribe the same subscriber multiple times.
     *
     * @param subscriber the {@link EventSubscriber} object to register.
     * @param captureEvents varargs of {@link Event} objects the
     *                      {@link EventSubscriber} is interested in.
     * @throws NullPointerException if {@code subscriber} is null.
     * @throws IllegalArgumentException if {@code captureEvents} is null or less
     * than 1. Multiple subscription of the same subscriber is not possible.
     */
    void subscribe(final EventSubscriber<V> subscriber, final Event... captureEvents);

    /**
     * Unregisters the {@link EventSubscriber} from this {@link EventPublisher}.
     * <p>
     * Unregistering the {@link EventSubscriber} means that the subscriber will
     * no longer receive notifications from the {@link EventPublisher}.
     *
     * @param subscriber the {@link EventSubscriber} object to unregister.
     * @throws NullPointerException if {@code subscriber} is null.
     */
    void unsubscribe(final EventSubscriber<V> subscriber);
}
