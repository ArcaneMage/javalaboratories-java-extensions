package org.javalaboratories.core.statistics;

import java.util.function.Consumer;

/**
 * Adapter interface used primarily to adapt {@code IntSummaryStatistics},
 * {@code LongSummaryStatistics} and {@code DoubleSummaryStatistics} classes to
 * function with statistical calculators.
 * <p>
 * @param <T> type of underling terms in summary statistics object.
 */
public interface SummaryStatisticsAdapter<T> extends Consumer<T> {

    void combine(SummaryStatisticsAdapter<T> other);

    long getCount();

    double getAverage();

    T getMax();

    T getMin();

    T getSum();

}
