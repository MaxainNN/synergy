package io.mkalugin.synergy.exception;

public class ContactsLoadingException extends RuntimeException {
    public ContactsLoadingException(String message, Throwable cause) {
        super(message,cause);
    }
}
