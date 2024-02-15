package org.javalaboratories.core.event;

import lombok.Getter;
import nl.altindag.log.LogCaptor;
import org.javalaboratories.core.concurrency.utils.Torrent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
public class EventBroadcasterTest implements EventSubscriber<EventBroadcasterTest.TestEventB>, EventSource  {

    private static final Logger logger = LoggerFactory.getLogger(EventBroadcasterTest.class);

    private static final TestEventA TEST_EVENT_A = new TestEventA("Hello World, A");
    private static final TestEventB TEST_EVENT_B = new TestEventB("Hello World, B");
    private static final TestEventC TEST_EVENT_C = new TestEventC("Hello World, C");

    private EventPublisher<TestEventA> publisherA;
    private EventPublisher<TestEventB> publisherB;
    private EventPublisher<TestEventC> publisherC;

    private EventSubscriber<TestEventA> subscriberA;
    private EventSubscriber<TestEventB> subscriberB;
    private EventSubscriber<TestEventB> toxicSubscriberB;
    private EventSubscriber<TestEventC> subscriberC;

    @Getter
    static class TestEventA extends AbstractEvent {
        private static final long serialVersionUID = -2400612074279477143L;
        private final String state;

        public TestEventA(final String state) {
            super();
            this.state = state;
        }
    }

    @Getter
    static class TestEventB extends AbstractEvent {
        private static final long serialVersionUID = 5051020250921053195L;
        private final String state;

        public TestEventB(final String state) {
            super();
            this.state = state;
        }
    }

    @Getter
    static class TestEventC extends AbstractEvent {
        private static final long serialVersionUID = -9116076407163983269L;
        private final String state;

        public TestEventC clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }
        public TestEventC(final String state) {
            super();
            this.state = state;
        }
    }

    @BeforeEach
    public void setup() {
        publisherA = new EventBroadcaster<>(this);
        publisherB = new EventBroadcaster<>();
        publisherC = new EventBroadcaster<>();

        subscriberA = event -> logger.debug("(Subscriber - A) received value \"{}\" from event {}",
                event.getState(),event.getEventId());
        subscriberB = this;
        toxicSubscriberB = event -> {
            logger.debug("(Toxic subscriber - B) received value \"{}\" from event {}", event.getState(),event.getEventId());
            throw new IllegalStateException("Object state error -- toxic subscriber");
        };
        subscriberC = event -> logger.debug("(Subscriber - C) received value \"{}\" from event {}",
                event.getState(),event.getEventId());

    }

    @Test
    public void testNew_Publisher_Pass() {
        // Given
        //   - Publisher objects created in setup method, one with a source and one without.

        // Then
        assertEquals("[subscribers=0,source=EventBroadcasterTest]", publisherA.toString());
        assertEquals("[subscribers=0,source=UNKNOWN]", publisherB.toString());
    }

    @Test
    public void testPublish_Events_Pass() {
        LogCaptor logCaptor = LogCaptor.forClass(EventBroadcasterTest.class);

        // Given
        publisherA.subscribe(subscriberA);
        publisherB.subscribe(subscriberB);

        // When
        publisherA.publish(TEST_EVENT_A); // Subscriber A
        publisherB.publish(TEST_EVENT_B); // Subscriber B

        // Then
        String[] log = {
                "(Subscriber - A) received value \"Hello World, A\" from event {TEST_EVENT_A}",
                "(Subscriber - B) received value \"Hello World, B\" from event {TEST_EVENT_B}",
        };

        long count = logCaptor.getDebugLogs().stream()
                .filter(l -> l.contains(log[0]) || l.contains(log[1]))
                .count();

        assertEquals(2, count);
        assertEquals(2, logCaptor.getDebugLogs().size());

        // Verify event objects
        assertTrue(TEST_EVENT_A.toString().contains("{TEST_EVENT_A}"));
        assertTrue(TEST_EVENT_B.toString().contains("{TEST_EVENT_B}"));
    }

    @Test
    public void testPublish_IncompatibleEvents_Fail() {
        // Given
        publisherA.subscribe(subscriberA);
        publisherC.subscribe(subscriberC);

        // Then
        publisherA.publish(TEST_EVENT_A); // Subscriber A
        EventException exception = assertThrows(EventException.class, () -> publisherC.publish(TEST_EVENT_C));

        Event incompatibleEvent = exception.getEvent()
                .orElseThrow();

        assertEquals("[subscribers=1,source=UNKNOWN]", publisherC.toString());

        // Verify event objects
        assertTrue(TEST_EVENT_A.toString().contains("{TEST_EVENT_A}"));
        assertTrue(incompatibleEvent.toString().contains("{TEST_EVENT_C}"));
    }

    @Test
    public void testPublish_ToxicSubscriber_Pass() {
        LogCaptor logCaptor = LogCaptor.forClass(EventBroadcasterTest.class);

        // Given
        publisherA.subscribe(subscriberA);
        publisherB.subscribe(subscriberB);
        publisherB.subscribe(toxicSubscriberB); // <-- toxic subscriber

        // When
        publisherA.publish(TEST_EVENT_A); // Subscriber A
        publisherB.publish(TEST_EVENT_B); // Subscriber B

        // Then
        String[] log = {
                "(Subscriber - A) received value \"Hello World, A\" from event {TEST_EVENT_A}",
                "(Subscriber - B) received value \"Hello World, B\" from event {TEST_EVENT_B}",
                "(Toxic subscriber - B) received value \"Hello World, B\" from event {TEST_EVENT_B}"
        };

        long count = logCaptor.getDebugLogs().stream()
                .filter(l -> l.contains(log[0]) || l.contains(log[1]) || l.contains(log[2]))
                .count();


        assertEquals(3, count);
        assertEquals(3, logCaptor.getDebugLogs().size());
        assertEquals("[subscribers=1,source=UNKNOWN]", publisherB.toString());

        // Verify event objects
        assertTrue(TEST_EVENT_A.toString().contains("{TEST_EVENT_A}"));
        assertTrue(TEST_EVENT_B.toString().contains("{TEST_EVENT_B}"));
    }

    @Test
    public void testSubscribe_NullSubscriber_Fail() {
        assertThrows(NullPointerException.class,() -> publisherA.subscribe(null));
    }

    @Test
    public void testSubscribe_SubscriberAlreadyExists_Fail() {
        // Given
        publisherA.subscribe(subscriberA);

        // Then
        assertThrows(EventException.class, () -> publisherA.subscribe(subscriberA));
    }

    @Test
    public void testUnsubscribe_Events_Pass() {
        LogCaptor logCaptor = LogCaptor.forClass(EventBroadcasterTest.class);

        // Given
        publisherB.subscribe(subscriberB);
        publisherB.subscribe(toxicSubscriberB); // <-- toxic subscriber

        // When
        boolean removed = publisherB.unsubscribe(toxicSubscriberB);
        publisherB.publish(TEST_EVENT_B); // Subscribers

        // Then
        String[] log = {
                "(Subscriber - B) received value \"Hello World, B\" from event {TEST_EVENT_B}",
        };

        long count = logCaptor.getDebugLogs().stream()
                .filter(l -> l.contains(log[0]))
                .count();

        assertTrue(removed);
        assertEquals(1, count);
        assertEquals(1, logCaptor.getDebugLogs().size());

        assertEquals("[subscribers=1,source=UNKNOWN]", publisherB.toString());

        // Verify event objects
        assertTrue(TEST_EVENT_B.toString().contains("{TEST_EVENT_B}"));
    }

    @Test
    public void testUnsubscribe_UnregisteredSubscriber_Pass() {
        // Given (0 subscribers)

        // When
        boolean removed = publisherA.unsubscribe(subscriberA);

        // Then
        assertFalse(removed);
        assertEquals("[subscribers=0,source=EventBroadcasterTest]", publisherA.toString());
    }

    @Test
    public void testPublish_ThreadSafety_Pass() {
        // Given
        // (3 subscribers)
        publisherB.subscribe(subscriberB);
        publisherB.subscribe(toxicSubscriberB); // <-- toxic subscriber
        publisherB.subscribe(event -> logger.debug("(Subscriber - B3) received value \"{}\" from event {}",event.getState(), event.getEventId()));

        Torrent torrent = Torrent.builder(EventPublisher.class)
                .withFloodgate("Publisher",16,255,() -> publisherB.publish(TEST_EVENT_B))
                .withFloodgate("Unsubscribe",16,255,() -> publisherB.unsubscribe(subscriberB))
                .build();

        torrent.open();
        torrent.flood();

        assertEquals(1, publisherB.subscribers());
        logger.info("Publisher state: {}", publisherB);
    }

    @Override
    public void notify(TestEventB event) {
        logger.debug("(Subscriber - B) received value \"{}\" from event {}",event.getState(), event.getEventId());
    }


}
