/*
 * Copyright 2020 Kevin Henry
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.javalaboratories.core.event;

import lombok.*;
import org.javalaboratories.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
 * {@link CommonEvents} class.
 * <p>
 * The implementation is considered thread-safe, and so subscribing and/or
 * unsubscribing is permitted during {@code subscriber} notification. However
 * be aware of the following:
 * <ul>
 *     <li>When unsubscribing, if this publisher is publishing to the same
 *     {@code subscriber}, that subscriber will continue to receive events until
 *     it is safely removed from the publisher.</li>
 * </ul>
 * @param <T> Type of source in which the event originated.
 * @param <V> Type of value and/or state forwarded to the {@code subscribers}
 *
 * @see Event
 * @see AbstractEvent
 * @see CommonEvents
 * @see EventSubscriber
 */
public abstract class EventBroadcaster<T extends EventSource,V> implements EventPublisher<V>, EventSource {

    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    // uniqueIdentity variable has its own lock (intrinsic). Retrieving identity
    // should not be obstructed by the main Lock object.
    private static int uniqueIdentity = 0;

    private final Object mainLock;
    private final Map<String,Subscription> subscriptions;
    private final T source;

    @Value
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @AllArgsConstructor
    private class Subscription {
        @EqualsAndHashCode.Include
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
        this.source = source;
        this.subscriptions = new LinkedHashMap<>();
        this.mainLock = new Object();
    }

    @Override
    public void publish(final Event event, final V value) {
        Event anEvent = Objects.requireNonNull(event,"No event?")
                .assign(source);

        Set<Subscription> set;
        synchronized(mainLock) {
            set = new HashSet<>(subscriptions.values());
        }

        set.forEach(subscription -> {
            if (subscription.getCaptureEvents().contains(anEvent)) {
                EventSubscriber<V> subscriber = subscription.getSubscriber();
                try {
                    subscriber.notify(anEvent, value);
                } catch (Throwable e) {
                    logger.error("Subscriber raised an uncaught exception -- canceled subscription", e);
                    unsubscribe(subscriber);
                }
            }
        });
    }

    @Override
    public void subscribe(final EventSubscriber<V> subscriber, final Event... captureEvents) {
        EventSubscriber<V> aSubscriber = Objects.requireNonNull(subscriber,"No subscriber?");

        if ( captureEvents == null || captureEvents.length < 1 )
            throw new IllegalArgumentException("No events to capture");

        synchronized(mainLock) {
            subscriptions.values().stream()
                .filter(s -> s.getSubscriber().equals(subscriber))
                .findAny()
                .ifPresent(s -> {
                    throw new EventException("Subscriber exists -- unsubscribe first");
                });

            Subscription subscription = new Subscription(getUniqueIdentity(), aSubscriber,
                    Collections.unmodifiableSet(new HashSet<>(Arrays.asList(captureEvents))));

            subscriptions.put(subscription.getIdentity(), subscription);
        }
    }

    @Override
    public boolean unsubscribe(final EventSubscriber<V> subscriber) {
        EventSubscriber<V> aSubscriber = Objects.requireNonNull(subscriber,"No subscriber?");

        synchronized(mainLock) {
            // Derive subscription identity
            String identity = subscriptions.values().stream()
                    .filter(s -> s.getSubscriber().equals(aSubscriber))
                    .map(Subscription::getIdentity)
                    .collect(Collectors.joining());

            // Remove subscription
            return subscriptions.remove(identity) != null;
        }
    }

    @Override
    public String toString() {
        String source = this.source.getClass().getSimpleName();
        source = source.isEmpty() ? "UNKNOWN" : source;
        synchronized (mainLock) {
            return String.format("[subscribers=%s,source=%s]", subscriptions.size(), source);
        }
    }

    @Override
    public int subscribers() {
        synchronized(mainLock) {
            return subscriptions.size();
        }
    }

    private String getUniqueIdentity() {
        synchronized (EventBroadcaster.class) {
            return String.format("{subscription-%s}",uniqueIdentity++);
        }
    }
}
