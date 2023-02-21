package org.tcollignon.user.exception;

public class PasswordEmptyException extends RuntimeException {

    public PasswordEmptyException() {
    }

    public PasswordEmptyException(String message) {
        super(message);
    }

    public PasswordEmptyException(String message, Throwable cause) {
        super(message, cause);
    }
}
