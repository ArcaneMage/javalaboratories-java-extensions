package org.javalaboratories.core.event;

import lombok.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class EventBroadcaster<T extends EventSource,V> implements EventPublisher<T,V> {

    private final Map<String,Subscription> subscriptionsMap;
    private static int uniqueIdentity = 0;

    @Value
    private class Subscription {
        Set<Event<? extends T>> captureEvents;
        String identity;
        EventSubscriber<? super T,V> subscriber;
    }

    public EventBroadcaster() {
        subscriptionsMap = new LinkedHashMap<>();
    }

    @Override
    public void publish(final EventSubscriber<? super T,V> subscriber, final Event<? extends T> event, V value) {
        Event<? extends T> e = event;
        subscriptionsMap.forEach((id,subscription) -> {
            if (subscription.getCaptureEvents().contains(event))
                subscription.getSubscriber().notify(e, value);
        });
    }

    @Override
    public void subscribe(final EventSubscriber<? super T, V> subscriber, final Event<? extends T>... captureEvents) {
        EventSubscriber<? super T,V> aSubscriber = Objects.requireNonNull(subscriber,"No subscriber?");
        if ( captureEvents == null || captureEvents.length > 1 )
            throw new IllegalArgumentException("No events to capture");

        Subscription subscription = new Subscription(Collections.unmodifiableSet(new HashSet<>(Arrays.asList(captureEvents))),
                getUniqueIdentity(),aSubscriber);

        subscriptionsMap.put(subscription.getIdentity(),subscription);
    }

    @Override
    public void unsubscribe(final EventSubscriber<? super T, V> subscriber) {
        EventSubscriber<? super T,V> aSubscriber = Objects.requireNonNull(subscriber,"No subscriber?");

        // Derive subscription identity
        String identity = subscriptionsMap.values().stream()
                .filter(s -> s.getSubscriber().equals(aSubscriber))
                .map(Subscription::getIdentity)
                .collect(Collectors.joining());

        // Remove subscription
        subscriptionsMap.remove(identity);
    }

    private String getUniqueIdentity() {
        synchronized (EventBroadcaster.class) {
            return String.format("{subscription-%s}",uniqueIdentity++);
        }
    }
}
