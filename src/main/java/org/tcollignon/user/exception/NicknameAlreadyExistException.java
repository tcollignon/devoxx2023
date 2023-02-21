package org.tcollignon.user.exception;

public class NicknameAlreadyExistException extends UsersException {

    public static final String EXCEPTION_CODE = "002";

    public NicknameAlreadyExistException() {
        super(EXCEPTION_CODE);
    }

    public NicknameAlreadyExistException(String message) {
        super(EXCEPTION_CODE, message);
    }

    public NicknameAlreadyExistException(String message, Throwable cause) {
        super(EXCEPTION_CODE, message, cause);
    }
}
