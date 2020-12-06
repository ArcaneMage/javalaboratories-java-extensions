package org.javalaboratories.core.event;

/**
 * A marker interface primarily implemented by {@link EventPublisher} objects or
 * the object that encapsulates the {@code publisher}.
 * <p>
 * {@link EventSubscriber} objects will receive an implementation of the
 * {@code EventSource} to identify the origins of the {@link Event}.
 */
public interface EventSource {

    /**
     * Generally used for scenarios where the {@link EventSource} object is
     * not determinable, for example events declared in the {@link CommonEvents}
     * class.
     */
    EventSource EVENT_SOURCE_UNKNOWN = new EventSource() {};
}
