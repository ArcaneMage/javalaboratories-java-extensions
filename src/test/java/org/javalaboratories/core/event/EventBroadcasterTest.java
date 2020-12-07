package org.javalaboratories.core.event;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.javalaboratories.core.event.CommonEvents.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventBroadcasterTest implements EventSubscriber<String>, EventSource  {

    private static final Logger logger = LoggerFactory.getLogger(EventBroadcasterTest.class);

    private static final Event TEST_EVENT_A = new TestEventA();
    private static final Event TEST_EVENT_B = new TestEventB();

    private EventPublisher<EventBroadcasterTest,String> publisher;
    private EventPublisher<EventBroadcasterTest,String> publisher2;

    private EventSubscriber<String> subscriberA;
    private EventSubscriber<String> subscriberB;

    class Publisher extends EventBroadcaster<EventBroadcasterTest,String> {
        public Publisher() {
            super();
        }
        public Publisher(EventBroadcasterTest source) {
            super(source);
        }
    }

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

    @BeforeEach
    public void setup() {
        publisher = new Publisher(this);
        publisher2 = new Publisher();
        subscriberA = (event,value) -> logger.debug("(Subscriber - A) received value \"{}\" from event {}",
                value,event.getEventId());
        subscriberB = this;
    }

    @Test
    public void testNew_Publisher_Pas() {
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
        assertThrows(IllegalArgumentException.class, () -> publisher.subscribe(subscriberA,ANY_EVENT));
    }

    @Test
    public void testUnsubscribe_Events_Pass() {
        LogCaptor logCaptor = LogCaptor.forClass(EventBroadcasterTest.class);

        // Given
        publisher.subscribe(subscriberA,NOTIFY_EVENT,TEST_EVENT_A,TEST_EVENT_B);
        publisher.subscribe(subscriberB,ACTION_EVENT,TEST_EVENT_B);
        publisher.unsubscribe(subscriberA);

        // When
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

        assertEquals(2, count);
        assertEquals(2, logCaptor.getDebugLogs().size());
    }

    @Override
    public void notify(Event event, String value) {
        logger.debug("(Subscriber - B) received value \"{}\" from event {}",value, event.getEventId());
    }
}
