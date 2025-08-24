package io.mkalugin.synergy.exception;

public class TransactionalException extends RuntimeException {

    public TransactionalException(String message) {
        super(message);
    }

    public TransactionalException(String message, Throwable cause) {
        super(message, cause);
    }
}
