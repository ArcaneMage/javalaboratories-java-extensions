package com.excelsior.core.statistics;

/**
 * An exception thrown by statistical calculators where there isn't enough data
 * to perform the calculation.
 */
public class InsufficientPopulationException extends RuntimeException {

    public InsufficientPopulationException() {
        super();
    }

    public InsufficientPopulationException(String message) {
        super(message);
    }

}
