package com.excelsior.util.statistics;

import java.util.List;

/**
 * Objects that implement this interface can perform a statistical function.
 * <p>
 * Typical statistical functions include mode or median and much more. These
 * functions are used in the {@code DoubleStatisticalCalculators},
 * {@code LongStatisticalCalculators} and {@code IntStatisticalCalculators}
 * classes, which are themselves used by the {@code Reducers} class.
 *
 * @param <T> type of terms (data)
 * @param <R> type of calculated result
 */
public interface StatisticalCalculator<T,R> {
    /**
     * Add the data to the calculator for processing.
     * <p>
     * @param data sample data.
     */
    void add(T data);

    /**
     * Returns sample data that the calculation is performed on.
     * <p>
     * @return a list of the sample data.
     */
    List<T> getData();

    /**
     * Returns the calculated result on the sample data.
     * @return result of calculation.
     */
    R getResult();


}
