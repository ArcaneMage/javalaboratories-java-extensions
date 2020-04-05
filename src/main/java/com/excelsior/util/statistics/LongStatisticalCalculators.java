package com.excelsior.util.statistics;

import com.excelsior.util.Nullable;

import java.util.*;

public class LongStatisticalCalculators extends AbstractStatisticalCalculators<Long> {

    private StatisticalCalculator<Long,Nullable<Long>> modes;
    private StatisticalCalculator<Long,Double> median;

    private LongSummaryStatistics adaptee;

    public LongStatisticalCalculators() {
        super();
        adaptee = new LongSummaryStatistics();
        modes = new ModeCalculator<>();
        median = new MedianCalculator<>();
        add(modes);
        add(median);
    }

    @Override
    public void accept(Long value) {
        super.accept(value);
        adaptee.accept(value);
    }

    public void combine(AbstractStatisticalCalculators<Long> summary) {
        super.combine(summary);
        LongStatisticalCalculators other = ((LongStatisticalCalculators) summary);
        adaptee.combine(other.adaptee);
    }

    public final Nullable<Long> getMode() {
        return modes.getResult();
    }

    public final double getMedian() {
        return median.getResult();
    }

    public long getCount() {
        return adaptee.getCount();
    }

    public Long getSum() {
        return adaptee.getSum();
    }

    public Long getMin() {
        return adaptee.getMin();
    }

    public Long getMax() {
        return adaptee.getMax();
    }

    public double getAverage() {
        return adaptee.getAverage();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{count=%d, sum=%d, min=%d, average=%f, max=%d, mode=%s, median=%f}",
                this.getClass().getSimpleName(),
                adaptee.getCount(),
                adaptee.getSum(),
                adaptee.getMin(),
                adaptee.getAverage(),
                adaptee.getMax(),
                getMode(),
                getMedian());
    }
}
