package org.tcollignon.user.exception;

public class UserNotFoundException extends UsersException {

    public static final String EXCEPTION_CODE = "019";

    public UserNotFoundException() {
        super(EXCEPTION_CODE);
    }

    public UserNotFoundException(String message) {
        super(EXCEPTION_CODE, message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(EXCEPTION_CODE, message, cause);
    }
}
