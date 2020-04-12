package com.excelsior.core.statistics;

import java.util.List;

public class StandardDeviationCalculator<T extends Number> implements StatisticalCalculator<T,Double> {

    private VarianceCalculator<T> variance;

    public StandardDeviationCalculator() {
        variance = new VarianceCalculator<>();
    }

    @Override
    public void accept(T data) {
        variance.accept(data);
    }

    @Override
    public List<T> getData() {
        return variance.getData();
    }

    @Override
    public Double getResult() {
        if ( variance.getData().size() == 0 )
            throw new InsufficientPopulationException("Could not calculate standard deviation");
        return Math.sqrt(variance.getResult());
    }
}
