package org.javalaboratories.core.event;

import nl.altindag.log.LogCaptor;
import org.javalaboratories.core.concurrency.utils.Torrent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.javalaboratories.core.event.CommonEvents.ACTION_EVENT;
import static org.javalaboratories.core.event.CommonEvents.ANY_EVENT;
import static org.javalaboratories.core.event.CommonEvents.NOTIFY_EVENT;
import static org.junit.jupiter.api.Assertions.*;

public class EventBroadcasterTest implements EventSubscriber<String>, EventSource  {

    private static final Logger logger = LoggerFactory.getLogger(EventBroadcasterTest.class);

    private static final Event TEST_EVENT_A = new TestEventA();
    private static final Event TEST_EVENT_B = new TestEventB();
    private static final Event TEST_EVENT_C = new TestEventC();

    private EventPublisher<String> publisher;
    private EventPublisher<String> publisher2;

    private EventSubscriber<String> subscriberA;
    private EventSubscriber<String> subscriberB;
    private EventSubscriber<String> subscriberC;

    static class TestEventA extends AbstractEvent {
        public TestEventA() {
            super();
        }
    }

    static class TestEventB extends AbstractEvent {
        public TestEventB() {
            super();
        }
    }

    static class TestEventC extends AbstractEvent {
        public TestEventC clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }
        public TestEventC() {
            super();
        }
    }

    @BeforeEach
    public void setup() {
        publisher = new EventBroadcaster<>(this);
        publisher2 = new EventBroadcaster<>();
        subscriberA = (event,value) -> logger.debug("(Subscriber - A) received value \"{}\" from event {}",
                value,event.getEventId());
        subscriberB = this;
        subscriberC = (event,value) -> {
            logger.debug("(Subscriber - C) received value \"{}\" from event {}", value,event.getEventId());
            throw new IllegalStateException("Object state error -- toxic subscriber");
        };
    }

    @Test
    public void testNew_Publisher_Pass() {
        // Given
        //   - Publisher objects created in setup method, one with a source and one without.

        // Then
        assertEquals("[subscribers=0,source=EventBroadcasterTest]",publisher.toString());
        assertEquals("[subscribers=0,source=UNKNOWN]",publisher2.toString());
    }

    @Test
    public void testPublish_Events_Pass() {
        LogCaptor logCaptor = LogCaptor.forClass(EventBroadcasterTest.class);

        // Given
        publisher.subscribe(subscriberA,NOTIFY_EVENT,TEST_EVENT_A,TEST_EVENT_B);
        publisher.subscribe(subscriberB,ACTION_EVENT,TEST_EVENT_B);

        // When
        publisher.publish(TEST_EVENT_A,"Hello World, A"); // Subscriber A
        publisher.publish(TEST_EVENT_B,"Hello World, B"); // Subscriber A,B
        publisher.publish(ACTION_EVENT,"Hello World, C"); // Subscriber B

        // Then
        String[] log = {
                "(Subscriber - A) received value \"Hello World, A\" from event {TEST_EVENT_A}",
                "(Subscriber - A) received value \"Hello World, B\" from event {TEST_EVENT_B}",
                "(Subscriber - B) received value \"Hello World, B\" from event {TEST_EVENT_B}",
                "(Subscriber - B) received value \"Hello World, C\" from event {ACTION_EVENT}"
        };

        long count = logCaptor.getDebugLogs().stream()
                .filter(l -> l.contains(log[0]) || l.contains(log[1]) || l.contains(log[2]) || l.contains(log[3]))
                .count();

        assertEquals(4, count);
        assertEquals(4, logCaptor.getDebugLogs().size());

        // Verify event objects
        assertTrue(ACTION_EVENT.toString().contains("{ACTION_EVENT}"));
        assertTrue(NOTIFY_EVENT.toString().contains("{NOTIFY_EVENT}"));
        assertTrue(TEST_EVENT_A.toString().contains("{TEST_EVENT_A}"));
        assertTrue(TEST_EVENT_B.toString().contains("{TEST_EVENT_B}"));
    }

    @Test
    public void testPublish_IncompatibleEvents_Fail() {
        // Given (3 subscribers)
        publisher.subscribe(subscriberA,NOTIFY_EVENT,TEST_EVENT_A,TEST_EVENT_B,TEST_EVENT_C);
        publisher.subscribe(subscriberC,ACTION_EVENT);
        publisher.subscribe(subscriberB,ACTION_EVENT,TEST_EVENT_B);

        // Then
        publisher.publish(TEST_EVENT_A,"Hello World, A"); // Subscriber A
        EventException exception = assertThrows(EventException.class, () -> publisher.publish(TEST_EVENT_C,"Hello World, A"));

        Event incompatibleEvent = exception.getEvent()
                .orElseThrow();

        assertEquals("[subscribers=3,source=EventBroadcasterTest]",publisher.toString());

        // Verify event objects
        assertTrue(ACTION_EVENT.toString().contains("{ACTION_EVENT}"));
        assertTrue(NOTIFY_EVENT.toString().contains("{NOTIFY_EVENT}"));
        assertTrue(TEST_EVENT_A.toString().contains("{TEST_EVENT_A}"));
        assertTrue(TEST_EVENT_B.toString().contains("{TEST_EVENT_B}"));
        assertTrue(incompatibleEvent.toString().contains("{TEST_EVENT_C}"));
    }

    @Test
    public void testPublish_ToxicSubscriber_Pass() {
        LogCaptor logCaptor = LogCaptor.forClass(EventBroadcasterTest.class);

        // Given (3 subscribers)
        publisher.subscribe(subscriberA,NOTIFY_EVENT,TEST_EVENT_A,TEST_EVENT_B);
        publisher.subscribe(subscriberC,ACTION_EVENT); // <-- toxic subscriber
        publisher.subscribe(subscriberB,ACTION_EVENT,TEST_EVENT_B);

        // When
        publisher.publish(TEST_EVENT_A,"Hello World, A"); // Subscriber A
        publisher.publish(ACTION_EVENT,"Hello World, C"); // Subscriber B,C
        publisher.publish(TEST_EVENT_B,"Hello World, B"); // Subscriber A,B

        // Then
        String[] log = {
                "(Subscriber - A) received value \"Hello World, A\" from event {TEST_EVENT_A}",
                "(Subscriber - C) received value \"Hello World, C\" from event {ACTION_EVENT}",
                "(Subscriber - B) received value \"Hello World, C\" from event {ACTION_EVENT}",
                "(Subscriber - A) received value \"Hello World, B\" from event {TEST_EVENT_B}",
                "(Subscriber - B) received value \"Hello World, B\" from event {TEST_EVENT_B}"
        };

        long count = logCaptor.getDebugLogs().stream()
                .filter(l -> l.contains(log[0]) || l.contains(log[1]) || l.contains(log[2]) || l.contains(log[3]) || l.contains(log[4]))
                .count();


        assertEquals(5, count);
        assertEquals(5, logCaptor.getDebugLogs().size());
        assertEquals("[subscribers=2,source=EventBroadcasterTest]",publisher.toString());

        // Verify event objects
        assertTrue(ACTION_EVENT.toString().contains("{ACTION_EVENT}"));
        assertTrue(NOTIFY_EVENT.toString().contains("{NOTIFY_EVENT}"));
        assertTrue(TEST_EVENT_A.toString().contains("{TEST_EVENT_A}"));
        assertTrue(TEST_EVENT_B.toString().contains("{TEST_EVENT_B}"));
    }

    @Test
    public void testSubscribe_CaptureEventsException_Fail() {
        assertThrows(NullPointerException.class,() -> publisher.subscribe(null));

        assertThrows(IllegalArgumentException.class,() -> publisher.subscribe(subscriberA));
    }

    @Test
    public void testSubscribe_SubscriberAlreadyExists_Fail() {
        // Given
        publisher.subscribe(subscriberA,NOTIFY_EVENT,TEST_EVENT_A,TEST_EVENT_B);

        // Then
        assertThrows(EventException.class, () -> publisher.subscribe(subscriberA,ANY_EVENT));
    }

    @Test
    public void testUnsubscribe_Events_Pass() {
        LogCaptor logCaptor = LogCaptor.forClass(EventBroadcasterTest.class);

        // Given
        publisher.subscribe(subscriberA,NOTIFY_EVENT,TEST_EVENT_A,TEST_EVENT_B);
        publisher.subscribe(subscriberB,ACTION_EVENT,TEST_EVENT_B);

        // When
        boolean removed = publisher.unsubscribe(subscriberA);
        publisher.publish(TEST_EVENT_A,"Hello World, A"); // Subscriber A
        publisher.publish(TEST_EVENT_B,"Hello World, B"); // Subscriber A,B
        publisher.publish(ACTION_EVENT,"Hello World, C"); // Subscriber B

        // Then
        String[] log = {
                "(Subscriber - B) received value \"Hello World, B\" from event {TEST_EVENT_B}",
                "(Subscriber - B) received value \"Hello World, C\" from event {ACTION_EVENT}"
        };

        long count = logCaptor.getDebugLogs().stream()
                .filter(l -> l.contains(log[0]) || l.contains(log[1]))
                .count();

        assertTrue(removed);
        assertEquals(2, count);
        assertEquals(2, logCaptor.getDebugLogs().size());

        // Verify event objects
        assertTrue(ACTION_EVENT.toString().contains("{ACTION_EVENT}"));
        assertTrue(NOTIFY_EVENT.toString().contains("{NOTIFY_EVENT}"));
        assertTrue(TEST_EVENT_A.toString().contains("{TEST_EVENT_A}"));
        assertTrue(TEST_EVENT_B.toString().contains("{TEST_EVENT_B}"));
    }

    @Test
    public void testUnsubscribe_UnregisteredSubscriber_Pass() {
        // Given (1 subscriber)
        publisher.subscribe(subscriberA,NOTIFY_EVENT);

        // When
        boolean removed = publisher.unsubscribe(subscriberB);

        // Then
        assertFalse(removed);
        assertEquals("[subscribers=1,source=EventBroadcasterTest]",publisher.toString());
    }

    @Test
    public void testPublish_ThreadSafety_Pass() {
        // Given
        // (3 subscribers)
        publisher.subscribe(subscriberA,ACTION_EVENT,NOTIFY_EVENT,TEST_EVENT_A,TEST_EVENT_B);
        publisher.subscribe(subscriberC,ACTION_EVENT); // <-- toxic subscriber
        publisher.subscribe(subscriberB,ACTION_EVENT,TEST_EVENT_B);

        Torrent torrent = Torrent.builder(EventPublisher.class)
                .withFloodgate("Publisher",16,255,() -> publisher.publish(ACTION_EVENT,"(Action Event): Hello World"))
                .withFloodgate("Unsubscribe",16,255,() -> publisher.unsubscribe(subscriberB))
                .build();

        torrent.open();
        torrent.flood();

        assertEquals(1,publisher.subscribers());
        logger.info("Publisher state: {}",publisher);
    }

    @Override
    public void notify(Event event, String value) {
        logger.debug("(Subscriber - B) received value \"{}\" from event {}",value, event.getEventId());
    }
}
