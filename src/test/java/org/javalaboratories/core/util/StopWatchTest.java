package org.javalaboratories.core.util;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("WeakerAccess")
public class StopWatchTest {

    private static final Logger logger = LoggerFactory.getLogger(StopWatch.class);

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
    public void testClear_Pass() {
        StopWatch.clear();
        int[] index = new int[1];

        StopWatch.forEach((a,b) -> { logger.info("{} \t-> {}",a,b); index[0]++; });

        assertEquals(0, index[0]);
    }

    @Test
    public void testEquals_Pass() {
        assertEquals(stopWatch1,stopWatch1);
        assertEquals(StopWatch.watch("MethodOne"),stopWatch1);
        assertNotEquals(StopWatch.watch("MethodOne"),stopWatch2);
    }

    @Test
    public void testForEach_Pass() {
        int[] index = new int[1];

        StopWatch.forEach((a,b) -> { logger.info("{} \t-> {}",a,b); index[0]++; });

        assertEquals(3, index[0]);
    }

    @Test
    public void testCompareTo_Pass() {
        // Given
        stopWatch1.time(() -> doSomethingVoidMethod(300));
        stopWatch2.time(() -> doSomethingVoidMethod(150));
        stopWatch3.time(() -> doSomethingVoidMethod(75));

        List<StopWatch> list = Arrays.asList(stopWatch1,stopWatch2,stopWatch3);

        // When
        Collections.sort(list);

        // Then
        assertTrue((list.get(0).getTime() < list.get(1).getTime()) &&
                   (list.get(1).getTime() < list.get(2).getTime()));
    }

    @Test
    public void testHashCode_Pass() {
        assertEquals(StopWatch.watch("MethodOne").hashCode(),stopWatch1.hashCode());
    }

    @Test
    void testPrintln_Pass() {
        // Given
        LogCaptor captor = LogCaptor.forClass(StopWatch.class);
        stopWatch1.time(() -> doSomethingVoidMethod(300));
        stopWatch2.time(() -> doSomethingVoidMethod(150));
        stopWatch3.time(() -> doSomethingVoidMethod(75));

        // When
        logger.info(StopWatch.println());
        StopWatch.println(System.out);

        // Then
        assertTrue(captor.getInfoLogs().stream()
                    .allMatch(p -> p.contains("MethodOne") &&
                                   p.contains("MethodTwo") &&
                                   p.contains("MethodThree")));
    }

    @Test
    public void testTime_Pass() {
        assertEquals(0L,stopWatch1.getTime());

        stopWatch1.time(() -> doSomethingVoidMethod(500));
        assertTrue(stopWatch1.getTime(TimeUnit.MILLISECONDS) <= 600);

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
        assertTrue(stopWatch5.getTime(TimeUnit.MILLISECONDS) > 600);
        assertEquals(stopWatch5.getRawTime(TimeUnit.NANOSECONDS),stopWatch5.getTime());

        // Time supplier
        StopWatch stopWatch6 = StopWatch.watch("MethodSix");
        int value = stopWatch6.time(() -> {
            for (int i = 0; i < 5; i++) {
                doSomethingVoidMethod(125);
            }
            return 512;
        });
        assertTrue(stopWatch6.getTime(TimeUnit.MILLISECONDS) > 600);
        assertEquals(512, value);
    }

    @Test
    public void testTime_Iteration_Pass() {
        // Given
        List<Integer> numbers = Arrays.asList(1,2,3,4);

        // When
        numbers.forEach(stopWatch1.time(n -> doSomethingVoidMethod(100)));

        // Then
        assertTrue(stopWatch1.getTime(TimeUnit.MILLISECONDS) >= 100);
        assertEquals(4, stopWatch1.getCycles());
    }

    @Test
    public void testRawTime_Pass() {
        // Given
        List<Integer> numbers = Arrays.asList(1,2,3,4);
        assertEquals(stopWatch1.getRawTime(),stopWatch1.getTime());

        // Then
        numbers.forEach(stopWatch1.time(n -> doSomethingVoidMethod(100)));

        // Then
        assertTrue(stopWatch1.getTime(TimeUnit.MILLISECONDS) >= 100);
        assertEquals(4, stopWatch1.getCycles());
        assertNotEquals(stopWatch1.getRawTime(),stopWatch1.getTime());
        assertNotEquals(stopWatch1.getRawTime(TimeUnit.NANOSECONDS),stopWatch1.getTime());
    }

    @Test
    public void testTimeMillis_Pass() {
        stopWatch1.time(() -> doSomethingVoidMethod(500));
        assertTrue(stopWatch1.getTime(TimeUnit.MILLISECONDS) <= 600);
    }


    @Test
    public void testReset_Pass() {

        stopWatch1.reset();

        stopWatch1.time(() -> doSomethingVoidMethod(500));
        assertTrue(stopWatch1.getTime(TimeUnit.MILLISECONDS) <= 600);

        stopWatch1.reset();
        assertEquals(0,stopWatch1.getTime());
    }

    @Test
    public void testFormat_Pass() {
        // Given
        stopWatch1.time(() -> doSomethingVoidMethod(1557));

        // When
        String s = stopWatch1.format(DateTimeFormatter.ofPattern("HH:mm:ss.n"));

        // Then
        logger.info("Slept ~1557ms and formatted to {}",s);
        assertTrue(s.startsWith("00:00:01.5"));
    }

    @Test
    public void testToString_Pass() {
        String sw = stopWatch1.toString();
        assertEquals("StopWatch[00:00:00.000]", stopWatch1.toString());

        stopWatch1.time(() -> {
            doSomethingVoidMethod(125);
        });
        String sw2 = stopWatch1.toString();
        assertNotEquals(sw,sw2);
    }

    private void doSomethingVoidMethod(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // Ignored
        }
    }

}
