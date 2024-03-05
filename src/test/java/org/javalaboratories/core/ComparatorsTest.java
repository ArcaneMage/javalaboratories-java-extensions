package org.javalaboratories.core;

import org.javalaboratories.core.util.Holders.Holder;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComparatorsTest {

    private static final Logger logger = LoggerFactory.getLogger(ComparatorsTest.class);

    @Test
    public void testEvalComparator_Pass() {
        List<Eval<Integer>> list = Arrays.asList(Eval.eager(9),Eval.eager(5),Eval.eager(3),Eval.eager(8));

        String sorted = list.stream()
                .sorted(Comparators.evalComparator())
                .peek(c -> logger.info(String.valueOf(c)))
                .map(e -> e.fold("",String::valueOf))
                .collect(Collectors.joining(","));

        assertEquals("3,5,8,9",sorted);
    }

    @Test
    public void testMaybeComparator_Pass() {
        List<Maybe<Integer>> list = Arrays.asList(Maybe.of(9),Maybe.of(5),Maybe.of(3),Maybe.of(8));

        String sorted = list.stream()
                .sorted(Comparators.maybeComparator())
                .peek(c -> logger.info(String.valueOf(c)))
                .map(m -> m.fold("",String::valueOf))
                .collect(Collectors.joining(","));

        assertEquals("3,5,8,9",sorted);
    }

    @Test
    public void testHolderComparator_Pass() {
        List<Holder<Integer>> list = Arrays.asList(Holder.of(9),Holder.of(5),Holder.of(3),Holder.of(8));

        String sorted = list.stream()
                .sorted(Comparators.holderComparator())
                .peek(c -> logger.info(String.valueOf(c)))
                .map(h -> h.fold("",String::valueOf))
                .collect(Collectors.joining(","));

        assertEquals("3,5,8,9",sorted);
    }
}
