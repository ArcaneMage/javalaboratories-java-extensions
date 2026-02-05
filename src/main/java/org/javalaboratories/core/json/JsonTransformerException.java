package org.javalaboratories.core.json;

import lombok.Getter;

@Getter
public class JsonTransformerException extends RuntimeException {
    private final String json;

    public JsonTransformerException(String message, Throwable cause, String json) {
        super(message, cause);
        this.json = json;
    }

    public JsonTransformerException(String message, Throwable cause) {
        this(message,cause,null);

    }

    public JsonTransformerException(String message, String json) {
        this(message,null,json);
    }

    public JsonTransformerException(String message) {
        this(message,null,null);
    }
}
