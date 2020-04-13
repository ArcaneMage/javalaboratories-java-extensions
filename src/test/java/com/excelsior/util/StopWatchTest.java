package com.excelsior.util;

import com.excelsior.util.StopWatch.State;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

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
        stopWatch1.time(() -> doSomethingVoidMethod(500));
        assertTrue(stopWatch1.getTimeInSeconds() <= 0.510);

        stopWatch2.time(() -> doSomethingVoidMethod(1000));
        assertTrue(stopWatch2.getTimeInSeconds() <= 1.010);

        // Nested timers
        stopWatch3.time(() -> {
            doSomethingVoidMethod(1500);
            StopWatch stopWatch4 = StopWatch.watch("MethodFour");
            stopWatch4.time(() -> doSomethingVoidMethod(1000));
        });
        assertTrue(stopWatch3.getTimeInSeconds() <= 2.6);

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

        logger.info('\n'+StopWatch.print());
    }

    @Test
    public void testTimeMillis_Pass() {
        stopWatch1.time(() -> doSomethingVoidMethod(500));
        assertTrue(stopWatch1.getTimeInMillis() <= 510);
    }

    @Test
    public void testTotalPercentile_Pass() {
        stopWatch1.time(() -> doSomethingVoidMethod(125));
        assertEquals(100, stopWatch1.getTotalPercentile());
    }

    @Test
    public void testPrint_Pass() {
        String sw = StopWatch.print();

        assertTrue(sw.contains("STAND_BY"));

        stopWatch1.time(() -> doSomethingVoidMethod(500));

        stopWatch2.time(() -> doSomethingVoidMethod(1000));

        stopWatch3.time(() -> doSomethingVoidMethod(1500));

        // Test long names
        StopWatch stopWatch4 = StopWatch.watch("MethodThreeWhichHasALongNameThatIsGreaterThanExpected");
        sw = StopWatch.print();
        assertTrue(sw.contains("MethodThreeWhichHasAL..."));
        logger.info('\n'+sw);
    }

    @Test
    public void testReset_Pass() {
        assertEquals(State.STAND_BY,stopWatch1.getState());

        stopWatch1.reset();
        assertEquals(State.STAND_BY,stopWatch1.getState());

        stopWatch1.time(() -> doSomethingVoidMethod(500));
        assertTrue(stopWatch1.getTimeInSeconds() <= 0.510);
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
        assertEquals("StopWatch[name='MethodOne',state='STAND_BY',cycles=Cycles[count=0]]", stopWatch1.toString());

        stopWatch1.time(() -> doSomethingVoidMethod(125));
        String sw2 = stopWatch1.toString();
        assertNotEquals(sw,sw2);
        assertTrue(sw2.startsWith("StopWatch"));
    }

    private int doSomethingIntMethod(long millis) throws Exception {
        doSomethingVoidMethod(millis);
        if ( millis > 600 )
            throw new Exception();
        return 1;
    }

    private void doSomethingVoidMethod(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignored
        }
    }

}
