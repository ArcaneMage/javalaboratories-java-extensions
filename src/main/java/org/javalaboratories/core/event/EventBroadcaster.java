package org.javalaboratories.core.event;

import lombok.Value;
import org.javalaboratories.util.Generics;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.javalaboratories.core.event.EventSource.*;

/**
 * This class has the ability to notify {@link EventSubscriber} recipients with
 * {@code events} when this object's state changes.
 * <p>
 * Although it's an abstract class it fully implements the {@link EventPublisher}
 * interface, and so extend this class to enable notification of {@code subscribers}
 * of events when object state changes. Alternatively, encapsulate this object
 * within an {@code owning} object ({@code composition}).
 * <p>
 * To complete the observer design pattern, implement the {@link EventSubscriber}
 * interface and introduce {@code event} types by subclassing {@link AbstractEvent}
 * class and/or use the out-of-the-box {@link Event} objects defined in the
 * {@link CommonEvents} class. However, it is important to note that these
 * pre-defined events are unaware of the the {@link EventSource}.
 *
 * @param <T> Type of source in which the event originated.
 * @param <V> Type of value and/or state forwarded to the {@code subscribers}
 *
 * @see Event
 * @see AbstractEvent
 * @see CommonEvents
 * @see EventSubscriber
 */
public abstract class EventBroadcaster<T extends EventSource,V> implements EventPublisher<T,V>, EventSource {

    private static int uniqueIdentity = 0;
    private final Map<String,Subscription> subscriptionsMap;
    private final T source;

    @Value
    private class Subscription {
        String identity;
        EventSubscriber<V> subscriber;
        Set<Event> captureEvents;
    }

    /**
     * Default constructor
     * <p>
     * Create an instance of this object with {@link EventSource} set to this.
     */
    public EventBroadcaster() {
        this(Generics.unchecked(EVENT_SOURCE_UNKNOWN));
    }

    /**
     * Constructs an instance of this object with an alternative {@link
     * EventSource}.
     * <p>
     * An alternative {@link EventSource} is useful when this object is a
     * composite of a "parent" object that requires an {@link EventPublisher}
     * object.
     * <p>
     * Create an instance of this object with {@link EventSource} set to this.
     */
    public EventBroadcaster(final T source) {
        this.subscriptionsMap = new LinkedHashMap<>();
        this.source = source;
    }

    @Override
    public void publish(final Event event, final V value) {
        Event anEvent = Objects.requireNonNull(event,"No event?");
        subscriptionsMap.forEach((id,subscription) -> {
            if (subscription.getCaptureEvents().contains(anEvent)) {
                EventSubscriber<V> subscriber = subscription.getSubscriber();
                try {
                    source(anEvent);
                    subscriber.notify(anEvent, value);
                } catch (Throwable e) {
                    // TODO: Handle this important situation
                }
            }
        });
    }

    @Override
    public void subscribe(final EventSubscriber<V> subscriber, final Event... captureEvents) {
        EventSubscriber<V> aSubscriber = Objects.requireNonNull(subscriber,"No subscriber?");

        subscriptionsMap.values().stream()
                .filter(s -> s.getSubscriber().equals(subscriber))
                .findAny()
                .ifPresent(s ->  {
                    throw new IllegalArgumentException("Subscriber exists -- unsubscribe first");
                });

        if ( captureEvents == null || captureEvents.length < 1 )
            throw new IllegalArgumentException("No events to capture");

        Subscription subscription = new Subscription(getUniqueIdentity(),aSubscriber,
                Collections.unmodifiableSet(new HashSet<>(Arrays.asList(captureEvents))));

        subscriptionsMap.put(subscription.getIdentity(),subscription);
    }

    @Override
    public void unsubscribe(final EventSubscriber<V> subscriber) {
        EventSubscriber<V> aSubscriber = Objects.requireNonNull(subscriber,"No subscriber?");

        // Derive subscription identity
        String identity = subscriptionsMap.values().stream()
                .filter(s -> s.getSubscriber().equals(aSubscriber))
                .map(Subscription::getIdentity)
                .collect(Collectors.joining());

        // Remove subscription
        subscriptionsMap.remove(identity);
    }

    @Override
    public String toString() {
        String source = this.source.getClass().getSimpleName();
        source = source.isEmpty() ? "UNKNOWN" : source;
        return String.format("[subscribers=%s,source=%s]",subscriptionsMap.size(),source);
    }

    private void source(Event event) {
        if (event instanceof AbstractEvent)
            ((AbstractEvent) event).source(source);
    }

    private String getUniqueIdentity() {
        synchronized (EventBroadcaster.class) {
            return String.format("{subscription-%s}",uniqueIdentity++);
        }
    }
}
