package com.excelsior.util;

import com.excelsior.util.StopWatch.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class StopWatchTest {
    Logger logger = LoggerFactory.getLogger(StopWatchTest.class);

    private StopWatch stopWatch1;
    private StopWatch stopWatch2;
    private StopWatch stopWatch3;

    @BeforeEach
    public void setup() {
        StopWatch.clear();
        stopWatch1 = StopWatch.watch("MethodOne");
        stopWatch2 = StopWatch.watch("MethodTwo");
        stopWatch3 = StopWatch.watch("MethodThree");
    }

    @Test
    public void testConstructor_Pass() {
        assertNotNull(stopWatch1);
        assertNotNull(stopWatch2);
        assertNotNull(stopWatch3);
    }

    @Test
    public void testEquals_Pass() {
        assertEquals(stopWatch1,stopWatch1);
        assertEquals(StopWatch.watch("MethodOne"),stopWatch1);
        assertNotEquals(StopWatch.watch("MethodOne"),stopWatch2);
    }

    @Test
    public void testHashCode_Pass() {
        assertEquals(StopWatch.watch("MethodOne").hashCode(),stopWatch1.hashCode());
    }

    @Test
    public void testTime_Pass() {
        assertEquals(State.STAND_BY,stopWatch1.getState());
        assertEquals(0L,stopWatch1.getTime());

        stopWatch1.time(() -> doSomethingVoidMethod(500));
        assertTrue(stopWatch1.getTime(TimeUnit.MILLISECONDS) <= 508);

        stopWatch2.time(() -> doSomethingVoidMethod(1000));
        assertEquals(1, stopWatch2.getTime(TimeUnit.SECONDS));

        // Nested timers
        stopWatch3.time(() -> {
            doSomethingVoidMethod(1500);
            StopWatch stopWatch4 = StopWatch.watch("MethodFour");
            stopWatch4.time(() -> doSomethingVoidMethod(1000));
        });
        assertEquals(2, stopWatch3.getTime(TimeUnit.SECONDS));

        StopWatch stopWatch5 = StopWatch.watch("MethodFive");
        stopWatch5.time(() -> {
            for (int i = 0; i < 5; i++) {
                doSomethingVoidMethod(125);
            }
        });

        assertEquals(1,stopWatch5.getCycles().getCount());

        List<Integer> numbers = Arrays.asList(1,2,3);
        StopWatch stopWatch6 = StopWatch.watch("MethodSix");
        numbers.forEach(stopWatch6.time(s -> {
            logger.info("testTime_Pass(): logging forEach loop {}",s);
            doSomethingVoidMethod(100);
        }));

        assertEquals(3, stopWatch6.getCycles().getCount());
        assertTrue(stopWatch6.getTime(TimeUnit.MILLISECONDS) <= 319);
        assertTrue(stopWatch6.getCycles().getMeanTime(TimeUnit.MILLISECONDS) <= 109);

        logger.info('\n'+StopWatch.print());
    }

    @Test
    public void testTimeMillis_Pass() {
        stopWatch1.time(() -> doSomethingVoidMethod(500));
        assertTrue(stopWatch1.getTime(TimeUnit.MILLISECONDS) <= 510);
    }

    @Test
    public void testTotalPercentile_Pass() {
        stopWatch1.time(() -> doSomethingVoidMethod(125));
        assertEquals(100, stopWatch1.getTotalPercentile());
    }

    @Test
    public void testPrint_Pass() {
        assertTrue(!StopWatch.print().contains("RUNNING"));

        stopWatch1.time(() -> {
            assertTrue(StopWatch.print().contains("RUNNING"));
            doSomethingVoidMethod(500);
        });

        stopWatch2.time(() -> doSomethingVoidMethod(1000));

        stopWatch3.time(() -> doSomethingVoidMethod(1500));

        // Test long names
        StopWatch.watch("MethodThreeWhichHasALongNameThatIsGreaterThanExpected");
        assertTrue(StopWatch.print().contains("MethodThreeWhichHasAL..."));
        logger.info('\n'+StopWatch.print());
    }

    @Test
    public void testReset_Pass() {
        assertEquals(State.STAND_BY,stopWatch1.getState());

        stopWatch1.reset();
        assertEquals(State.STAND_BY,stopWatch1.getState());

        stopWatch1.time(() -> doSomethingVoidMethod(500));
        assertTrue(stopWatch1.getTime(TimeUnit.MILLISECONDS) <= 510);
        assertEquals(State.STOPPED,stopWatch1.getState());

        stopWatch1.reset();
        assertEquals(State.STAND_BY,stopWatch1.getState());
    }

    @Test
    public void testReset_Fail() {
        stopWatch1.time(() -> {
            doSomethingVoidMethod(125);
            assertThrows(IllegalStateException.class, stopWatch1::reset);
        });

    }

    @Test
    public void testStates_Pass() {
        assertEquals(State.STAND_BY,stopWatch1.getState());
        stopWatch1.reset();

        assertEquals(State.STAND_BY,stopWatch1.getState());
        stopWatch1.time(() -> {
            doSomethingVoidMethod(125);
            assertEquals(State.RUNNING,stopWatch1.getState());
        });
        assertEquals(State.STOPPED,stopWatch1.getState());
    }

    @Test
    public void testStates_Fail() {
        stopWatch1.time(() -> {
            doSomethingVoidMethod(125);
            assertThrows(IllegalStateException.class, () -> stopWatch1.time(() -> doSomethingVoidMethod(125)));
            assertThrows(IllegalStateException.class, stopWatch1::getTime);
            assertThrows(IllegalStateException.class, stopWatch1::reset);
        });
    }

    @Test
    public void testToString_Pass() {
        String sw = stopWatch1.toString();
        assertEquals("StopWatch[name='MethodOne',time=0,millis=0,seconds=0,total-percentile=0,state='STAND_BY'," +
                "cycles=Cycles[count=0]]", stopWatch1.toString());

        stopWatch1.time(() -> {
            assertTrue(stopWatch1.toString().contains("RUNNING"));
            doSomethingVoidMethod(125);
        });
        String sw2 = stopWatch1.toString();
        assertNotEquals(sw,sw2);
        assertTrue(sw2.startsWith("StopWatch"));
    }

    private void doSomethingVoidMethod(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignored
        }
    }

}
