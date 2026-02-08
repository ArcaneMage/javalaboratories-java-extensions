package org.javalaboratories.core.json;

import lombok.Getter;

/**
 * This exception is raised during transformation of the JSON structure.
 * <p>
 * The exceptions can include syntactical errors in the JSON structure and/or
 * mapping errors within the schema encountered during the transformation.
 * <p>
 * This is not a checked exception, and so there no need for the client to
 * compulsorily handle exception.
 */
@Getter
public class JsonTransformerException extends RuntimeException {
    private final String json;

    /**
     * Constructs this exception with {@code message}, {@code cause} and
     * erroneous JSON data.
     *
     * @param message reason/details of exception
     * @param cause underlying cause of exception
     * @param json erroneous JSON data
     */
    public JsonTransformerException(String message, Throwable cause, String json) {
        super(message, cause);
        this.json = json;
    }

    /**
     * Constructs this exception with {@code message} and {@code cause}.
     *
     * @param message reason/details of exception
     * @param cause underlying cause of exception
     */
    public JsonTransformerException(String message, Throwable cause) {
        this(message,cause,null);

    }

    /**
     * Constructs this exception with {@code message} and erroneous JSON data.
     *
     * @param message reason/details of exception
     * @param json erroneous JSON data
     */
    public JsonTransformerException(String message, String json) {
        this(message,null,json);
    }

    /**
     * Constructs this exception with {@code message} detail.
     *
     * @param message reason/details of exception
     */
    public JsonTransformerException(String message) {
        this(message,null,null);
    }
}
