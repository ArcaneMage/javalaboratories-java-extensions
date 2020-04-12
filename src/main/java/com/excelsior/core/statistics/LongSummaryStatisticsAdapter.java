package com.excelsior.core.statistics;

import java.util.LongSummaryStatistics;

public class LongSummaryStatisticsAdapter implements SummaryStatisticsAdapter<Long> {

    private LongSummaryStatistics adaptee = new LongSummaryStatistics();

    @Override
    public void combine(SummaryStatisticsAdapter<Long> o) {
        LongSummaryStatistics other = ((LongSummaryStatisticsAdapter) o).adaptee;
        this.adaptee.combine(other);
    }

    @Override
    public long getCount() {
        return adaptee.getCount();
    }

    @Override
    public double getAverage() {
        return adaptee.getAverage();
    }

    @Override
    public Long getMax() {
        return adaptee.getMax();
    }

    @Override
    public Long getMin() {
        return adaptee.getMin();
    }

    @Override
    public Long getSum() {
        return adaptee.getSum();
    }

    @Override
    public void accept(Long value) {
        adaptee.accept(value);
    }
}
