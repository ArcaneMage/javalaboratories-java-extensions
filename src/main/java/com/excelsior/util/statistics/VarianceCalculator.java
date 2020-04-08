package com.excelsior.util.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VarianceCalculator<T extends Number> implements StatisticalCalculator<T,Double> {

    private List<T> terms;
    private double sum, sumsq;

    public VarianceCalculator() {
        terms = new ArrayList<>();
        sum = 0.0;
        sumsq = 0.0;
    }

    @Override
    public void accept(T data) {
        terms.add(data);
        sum += data.doubleValue();
        sumsq += (data.doubleValue() * data.doubleValue());
    }

    @Override
    public List<T> getData() {
        return Collections.unmodifiableList(terms);
    }

    @Override
    public Double getResult() {
        if ( terms.size() == 0 )
            throw new InsufficientPopulationException("Could not calculate variance");
        double n = (double) terms.size();
        double mean = sum / n;
        double meansq = sumsq / n;
        return meansq - (mean * mean);
    }
}
