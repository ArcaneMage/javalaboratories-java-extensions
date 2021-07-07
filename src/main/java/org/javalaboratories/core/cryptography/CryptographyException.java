package org.javalaboratories.core.cryptography;

public class CryptographyException extends RuntimeException {

    public CryptographyException() {
        super();
    }

    public CryptographyException(String message) {
        super(message);

    }
    public CryptographyException(String message, Throwable e) {
        super(message,e);
    }
}
