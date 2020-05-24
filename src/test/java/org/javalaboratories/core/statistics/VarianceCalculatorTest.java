package org.javalaboratories.core.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VarianceCalculatorTest {
    private VarianceCalculator<Long> varianceCalculator1;
    private VarianceCalculator<Long> varianceCalculator2;

    @BeforeEach
    public void setup() {
        List<Long> terms1 = Arrays.asList(17L,19L,21L,13L,16L,18L,24L,20L,20L);

        varianceCalculator1 = new VarianceCalculator<>();
        terms1.forEach(t -> varianceCalculator1.accept(t));

        varianceCalculator2 = new VarianceCalculator<>();
    }

    @Test
    public void testAdd_Pass() {
        assertEquals(9, varianceCalculator1.getData().size());
    }

    @Test
    public void testGetResult_Pass() {
        assertEquals(8.8888888888888,varianceCalculator1.getResult());
    }

    @Test
    public void testGetResult_Fail() {
       assertThrows (InsufficientPopulationException.class, () -> varianceCalculator2.getResult());
    }
}
