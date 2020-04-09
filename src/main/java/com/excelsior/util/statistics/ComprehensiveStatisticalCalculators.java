package com.excelsior.util.statistics;

import com.excelsior.util.Nullable;

public abstract class ComprehensiveStatisticalCalculators<T extends Number> extends AbstractStatisticalCalculators<T> {

    private StatisticalCalculator<T,Nullable<Double>> mode;
    private StatisticalCalculator<T,Double> median;
    private StatisticalCalculator<T,Double> variance;
    private StatisticalCalculator<T,Double> standardDeviation;

    public ComprehensiveStatisticalCalculators(SummaryStatisticsAdapter<T> summary) {
        super(summary);
        mode = new ModeCalculator<>();
        median = new MedianCalculator<>();
        variance = new VarianceCalculator<>();
        standardDeviation = new StandardDeviationCalculator<>();
        add(mode,median,variance,standardDeviation);
    }

    public final Nullable<Double> getMode() {
        return mode.getResult();
    }

    public final double getMedian() {
        return median.getResult();
    }

    public final double getVariance() { return variance.getResult(); }

    public final double getStandardDeviation() { return standardDeviation.getResult(); }

    @Override
    public String toString() {
        return String.format(
                "%s{count=%d, sum=%s, min=%s, average=%s, max=%s, mode=%s, median=%f, variance=%f, standard-deviation=%f}",
                this.getClass().getSimpleName(),
                getCount(),
                getSum(),
                getMin(),
                getAverage(),
                getMax(),
                getMode(),
                getMedian(),
                getVariance(),
                getStandardDeviation());
    }
}
