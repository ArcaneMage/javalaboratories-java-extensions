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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.javalaboratories.core.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
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
 * class.
 * <p>
 * The implementation is considered thread-safe, and so subscribing and/or
 * unsubscribing is permitted during {@code subscriber} notification. However
 * be aware of the following:
 * <ul>
 *     <li>Publisher does not lock all {@code subscribers} during notification,
 *     this is considered too restrictive and impinges concurrency, but instead
 *     only locks the current {@code subscriber} that is being notified.</li>
 *     <li>When unsubscribing, if this publisher is publishing to the same
 *     {@code subscriber}, that subscriber will continue to receive events until
 *     it is conveniently removed from the publisher.</li>
 *     <li>In the case of "toxic" subscribers, subscribers raised an exception,
 *     these are automatically "canceled" and eventually removed from the
 *     publisher when it is convenient to do so. It is not possible to publish
 *     events to a "canceled" subscriber, and so all threads publishing with this
 *     publisher are notified to refrain from sending events to "canceled"
 *     subscribers.</li>
 * </ul>
 * To conclude,it is possible to {@link EventBroadcaster#publish},
 * {@link EventBroadcaster#subscribe} and {@link EventBroadcaster#unsubscribe}
 * and maintain reasonable concurrency in an multi-threaded context.
 *
 * @param <T> Type of source in which the event originated.
 *
 * @see Event
 * @see AbstractEvent
 * @see EventSubscriber
 */
public class EventBroadcaster<T extends EventSource,U extends Event> implements EventPublisher<U>, EventSource {

    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    // uniqueIdentity variable has its own lock (intrinsic). Retrieving identity
    // should not be obstructed by the main Lock object.
    private static int uniqueIdentity = 0;

    private final ReentrantLock mainLock;
    private final Map<String,Subscription<U>> subscriptions;
    private final T source;

    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    private static class Subscription<E extends Event> {
        private final ReentrantLock lock = new ReentrantLock();
        @EqualsAndHashCode.Include
        private final String identity;

        private final EventSubscriber<E> subscriber;
        private boolean canceled;
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
     * @param source the object that publishes the events.
     */
    public EventBroadcaster(final T source) {
        this.source = source;
        this.subscriptions = new LinkedHashMap<>();
        this.mainLock = new ReentrantLock();
    }

    @Override
    public void publish(final U event) {
        @SuppressWarnings("unchecked")
        U anEvent = (U) Objects.requireNonNull(event,"No event?")
                .assign(source);

        Set<Subscription<U>> observers;
        mainLock.lock();
        try {
            observers = new HashSet<>(subscriptions.values());
        } finally {
            mainLock.unlock();
        }

        observers.forEach(subscription -> {
            EventSubscriber<U> subscriber = subscription.getSubscriber();
            subscription.getLock().lock();
            try {
                    try {
                        if (!subscription.canceled)
                            subscriber.notify(anEvent);
                    } catch (Throwable e) {
                        logger.error("Subscriber raised an uncaught exception -- canceled subscription", e);
                        subscription.canceled = true;
                        unsubscribe(subscriber);
                    }
            } finally {
                subscription.getLock().unlock();
            }
        });
    }

    @Override
    public void subscribe(final EventSubscriber<U> subscriber) {
        EventSubscriber<U> aSubscriber = Objects.requireNonNull(subscriber,"No subscriber?");
        mainLock.lock();
        try {
            subscriptions.values().stream()
                    .filter(s -> s.getSubscriber().equals(subscriber))
                    .findAny()
                    .ifPresent(s -> {
                        throw new EventException("Subscriber exists -- unsubscribe first");
                    });

            Subscription<U> subscription = new Subscription<>(getUniqueIdentity(), aSubscriber,false);

            subscriptions.put(subscription.getIdentity(), subscription);
        } finally {
            mainLock.unlock();
        }
    }

    @Override
    public boolean unsubscribe(final EventSubscriber<U> subscriber) {
        EventSubscriber<U> aSubscriber = Objects.requireNonNull(subscriber,"No subscriber?");
        mainLock.lock();
        try {
            // Derive subscription identity
            String identity = subscriptions.values().stream()
                    .filter(s -> s.getSubscriber().equals(aSubscriber))
                    .map(Subscription::getIdentity)
                    .collect(Collectors.joining());

            // Remove subscription
            return subscriptions.remove(identity) != null;
        } finally {
            mainLock.unlock();
        }
    }

    @Override
    public String toString() {
        String source = this.source.getClass().getSimpleName();
        source = source.isEmpty() ? "UNKNOWN" : source;
        mainLock.lock();
        try {
            return String.format("[subscribers=%s,source=%s]", subscriptions.size(), source);
        } finally {
            mainLock.unlock();
        }
    }

    @Override
    public int subscribers() {
        mainLock.lock();
        try {
            return subscriptions.size();
        } finally {
            mainLock.unlock();
        }
    }

    private String getUniqueIdentity() {
        // This intrinsic lock is okay: cannot imagine it causing issues with
        // virtual threads.
        synchronized (EventBroadcaster.class) {
            return String.format("{subscription-%s}",uniqueIdentity++);
        }
    }
}
