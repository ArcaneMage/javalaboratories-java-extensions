package com.excelsior.util.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MedianCalculator<T extends Number> implements StatisticalCalculator<T,Double> {
    private final List<T> terms = new ArrayList<>();

    public void accept(T value) {
        terms.add(value);
    }

    public Double getResult() {
        if ( terms.size() == 0 )
            throw new InsufficientPopulationException("Could not calculate median");
        List<T> sorted = terms.stream()
                .sorted()
                .collect(Collectors.toList());
        int index;
        index = (sorted.size() + 1) / 2;
        if ( sorted.size() % 2 != 0 ) {
            return sorted.get(index -1).doubleValue();
        } else {
            if ( sorted.size() > 0 ) {
                T lterm = sorted.get(index - 1);
                T rterm = sorted.get(index);
                return (lterm.doubleValue() + rterm.doubleValue()) / 2.0;
            } else {
                return 0.0;
            }
        }
    }

    public List<T> getData() {
        return Collections.unmodifiableList(terms);
    }
}
