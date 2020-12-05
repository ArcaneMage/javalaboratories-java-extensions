package org.javalaboratories.core.event;

/**
 * An object that implements this interface can notify its subscribed
 * dependencies with events.
 * <p>
 * This interface is the {@code Subject} component of the observer design
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
     * when {@link EventPublisher} or owning application state changes.
     * <p>
     * If the {@code subscriber} has not explicitly stated that it's
     * interested in that particular {@link Event}, it will not receive a
     * notification.
     * <p>
     * @param subscriber the {@link EventSubscriber} object to register.
     * @param event the {@link Event} object with which to publish to recipients.
     * @param value the state value representing the current state of this
     *             {@code publisher}
     */
    void publish(final EventSubscriber<? super T,V> subscriber, final Event<? super T> event, V value);

    /**
     * Registers the {@link EventSubscriber} with this {@link EventPublisher}.
     * <p>
     * The {@code captureEvents} parameter explicitly state the events the
     * {@code subscriber} is particularly interested in. Each {@Link Event}
     * must be uniquely identifiable -- ensure the {@code equals and hashCode} of
     * the {@code Event} are implemented.
     *
     * @param subscriber the {@link EventSubscriber} object to register.
     * @param captureEvents varargs of {@link Event} objects the
     *                      {@link EventSubscriber} is interested in.
     */
    void subscribe(final EventSubscriber<? super T,V> subscriber, final Event<? super T>... captureEvents);

    /**
     * Unregisters the {@link EventSubscriber} from this {@link EventPublisher}.
     * <p>
     * Unregistering the {@link EventSubscriber} means that the subscriber will
     * no longer receive notifications from the {@link EventPublisher}.
     *
     * @param subscriber the {@link EventSubscriber} object to unregister.
     */
    void unsubscribe(final EventSubscriber<? super T,V> subscriber);
}
