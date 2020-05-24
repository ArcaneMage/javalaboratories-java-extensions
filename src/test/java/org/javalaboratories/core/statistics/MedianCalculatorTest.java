package org.javalaboratories.core.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("WeakerAccess")
public class MedianCalculatorTest {

    private MedianCalculator<Long> medianCalculator1;
    private MedianCalculator<Long> medianCalculator2;
    private MedianCalculator<Long> medianCalculator3;
    private MedianCalculator<Long> medianCalculator4;

    @BeforeEach
    public void setup() {
        List<Long> terms1 = Arrays.asList(17L,19L,21L,13L,16L,18L,24L,22L,20L);
        List<Long> terms2 = Arrays.asList(18L,16L,14L,11L,13L,10L,9L,20L);
        List<Long> terms3 = Arrays.asList(18L,16L);

        // Odd number of terms
        medianCalculator1 = new MedianCalculator<>();
        terms1.forEach(t -> medianCalculator1.accept(t));

        // Even number of terms
        medianCalculator2 = new MedianCalculator<>();
        terms2.forEach(t -> medianCalculator2.accept(t));


        medianCalculator3 = new MedianCalculator<>();
        terms3.forEach(t -> medianCalculator3.accept(t));

        medianCalculator4 = new MedianCalculator<>();
    }

    @Test
    public void testAdd_Pass() {
        assertEquals(9,medianCalculator1.getData().size());
        assertEquals(8,medianCalculator2.getData().size());
        assertEquals(0,medianCalculator4.getData().size());

    }

    @Test
    public void testGetResult_Pass() {
        assertEquals(19.0,medianCalculator1.getResult());
        assertEquals(13.5,medianCalculator2.getResult());

        assertEquals(17.0,medianCalculator3.getResult());
    }

    @Test
    public void testGetResult_Fail() {
        assertThrows(InsufficientPopulationException.class, () ->  medianCalculator4.getResult());
    }

}
