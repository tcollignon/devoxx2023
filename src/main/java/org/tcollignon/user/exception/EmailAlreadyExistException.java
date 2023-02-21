package org.tcollignon.user.exception;

public class EmailAlreadyExistException extends UsersException {

    public static final String EXCEPTION_CODE = "001";

    public EmailAlreadyExistException() {
        super(EXCEPTION_CODE);
    }

    public EmailAlreadyExistException(String message) {
        super(EXCEPTION_CODE, message);
    }

    public EmailAlreadyExistException(String message, Throwable cause) {
        super(EXCEPTION_CODE, message, cause);
    }
}
