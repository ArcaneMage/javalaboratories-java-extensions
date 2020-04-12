package com.excelsior.core.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ModeCalculatorTest {

    private ModeCalculator<Long> modeCalculator1;
    private ModeCalculator<Long> modeCalculator2;
    private ModeCalculator<Long> modeCalculator3;
    private ModeCalculator<Long> modeCalculator4;

    @BeforeEach
    public void setup() {
        List<Long> terms1 = Arrays.asList(17L,19L,21L,13L,16L,18L,24L,20L,20L);
        List<Long> terms2 = Arrays.asList(18L,16L,14L,11L,13L,10L,9L,20L);
        List<Long> terms3 = Arrays.asList(18L,18L);

        // Odd number of terms
        modeCalculator1 = new ModeCalculator<>();
        terms1.forEach(t -> modeCalculator1.accept(t));

        // Even number of terms
        modeCalculator2 = new ModeCalculator<>();
        terms2.forEach(t -> modeCalculator2.accept(t));


        modeCalculator3 = new ModeCalculator<>();
        terms3.forEach(t -> modeCalculator3.accept(t));

        modeCalculator4 = new ModeCalculator<>();
    }

    @Test
    public void testAdd_Pass() {
        assertEquals(9, modeCalculator1.getData().size());
        assertEquals(8, modeCalculator2.getData().size());
        assertEquals(0, modeCalculator4.getData().size());
    }

    @Test
    public void testGetResult_Pass() {
        modeCalculator1.getResult().ifPresent(r -> assertEquals(20L, r));

        modeCalculator3.getResult().ifPresent(r -> assertEquals(18L, r));
    }

    @Test
    public void testGetResult_Fail() {
        assertThrows (NoSuchElementException.class, () -> modeCalculator2.getResult().get());

        assertThrows (InsufficientPopulationException.class, () -> modeCalculator4.getResult());
    }
}
