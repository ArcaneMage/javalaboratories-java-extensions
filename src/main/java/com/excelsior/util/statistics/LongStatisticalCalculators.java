package com.excelsior.util.statistics;

import com.excelsior.util.Nullable;

public final class LongStatisticalCalculators extends AbstractStatisticalCalculators<Long> {

    private StatisticalCalculator<Long,Nullable<Long>> modes;
    private StatisticalCalculator<Long,Double> median;

    public LongStatisticalCalculators() {
        super(new LongSummaryStatisticsAdapter());
        median = new MedianCalculator<>();
        modes = new ModeCalculator<>();
        add(median);
        add(modes);
    }

    public final Nullable<Long> getMode() {
        return modes.getResult();
    }

    public final double getMedian() {
        return median.getResult();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{count=%d, sum=%d, min=%d, average=%f, max=%d, mode=%s, median=%f}",
                this.getClass().getSimpleName(),
                getCount(),
                getSum(),
                getMin(),
                getAverage(),
                getMax(),
                getMode(),
                getMedian());
    }
}
