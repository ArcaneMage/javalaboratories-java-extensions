package org.javalaboratories.core.event;

/**
 * An object that implements this interface can receive notifications from the
 * {@link EventPublisher}.
 * <p>
 * This interface is the {@code Observer} component of the observer design
 * pattern as described in the "Design Patterns, Elements of Reusable Object-
 * Oriented Software (Gang-of-Four)" book. If this object is registered with
 * the {@link EventPublisher} but did not register for a particular {@link Event},
 * it will NOT be notified of that {@code Event}.
 *
 * @param <T> Type of source in which the event originated.
 * @param <V> Type of value and/or state forwarded to the {@code subscribers}
 *
 * @see Event
 * @see EventBroadcaster
 * @see EventPublisher
 */
public interface EventSubscriber<T extends EventSource,V> {

    /**
     * This method is invoked to notify this object that a state change occurred in
     * the source of the {@code Event}.
     * <p>
     * @param event the object event sent from the {@link EventPublisher}.
     * @param value the state or value received from the {@link EventPublisher}
     */
    void notify(final Event<? super T> event, V value);
}
