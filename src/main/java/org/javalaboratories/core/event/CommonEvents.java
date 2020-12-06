package org.javalaboratories.core.event;

/**
 * Utility class of the pre-defined {@link Event} types.
 * <p>
 * These are useful to minimise boiler-plate code, but it must be noted that none
 * of these {@link Event} types are unaware of the {@link EventSource} object. If
 * the {@link EventSource} is required, implement user-defined {@link Event}
 * object by subclassing {@link AbstractEvent} class.
 *
 * @see AbstractEvent
 * @see Event
 */
public final class CommonEvents implements EventSource {

    private CommonEvents() {}

    /**
     * Useful for scenarios where you expect the {@link EventSubscriber} objects
     * to perform an action based on the current {@link EventPublisher} state.
     */
    public static final ActionEvent ACTION_EVENT = new ActionEvent(EVENT_SOURCE_UNKNOWN);

    /**
     * This is a generic {@link Event} in that it is useful where the {@link Event}
     * can represent any change in state of the {@link EventPublisher}, or perhaps
     * it can be considered a super type of all {@link Event} objects.
     */
    public static final AnyEvent ANY_EVENT = new AnyEvent(EVENT_SOURCE_UNKNOWN);

    /**
     * Notifies the {@link EventSubscriber} objects of the state change of the
     * {@link EventPublisher}, but the {@code subscriber} would need to
     * understand the context of the notification.
     */
    public static final NotifyEvent NOTIFY_EVENT = new NotifyEvent(EVENT_SOURCE_UNKNOWN);

    private static class ActionEvent extends AbstractEvent<EventSource> {
        public ActionEvent(EventSource source) {
            super(source);
        }
    }

    private static class AnyEvent extends AbstractEvent<EventSource> {
        public AnyEvent(EventSource source) {
            super(source);
        }
    }

    private static class NotifyEvent extends AbstractEvent<EventSource> {
        public NotifyEvent(EventSource source) {
            super(source);
        }
    }
}
