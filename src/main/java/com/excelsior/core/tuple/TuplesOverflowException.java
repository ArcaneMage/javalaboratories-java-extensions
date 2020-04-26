package com.excelsior.core.tuple;

/**
 * Exception is thrown if a tuple operation exceeds allowed tuple types.
 */
public class TuplesOverflowException extends RuntimeException {
    public TuplesOverflowException() {
        super();
    }

    public TuplesOverflowException(String message) {
        super(message);
    }
}
