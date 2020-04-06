package com.excelsior.util.statistics;

import java.util.IntSummaryStatistics;

public class IntSummaryStatisticsAdapter implements SummaryStatisticsAdapter<Integer> {

    private IntSummaryStatistics adaptee = new IntSummaryStatistics();

    @Override
    public void combine(SummaryStatisticsAdapter<Integer> o) {
        IntSummaryStatistics other = ((IntSummaryStatisticsAdapter) o).adaptee;
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
    public Integer getMax() {
        return adaptee.getMax();
    }

    @Override
    public Integer getMin() {
        return adaptee.getMin();
    }

    @Override
    public Integer getSum() {
        // TODO: Need to think about this type conversion
        return (int) adaptee.getSum();
    }

    @Override
    public void accept(Integer value) {
        adaptee.accept(value);
    }
}
