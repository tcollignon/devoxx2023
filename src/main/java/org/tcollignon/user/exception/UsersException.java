package org.tcollignon.user.exception;

public class UsersException extends RuntimeException {

    private String code;

    public String getCode() {
        return code;
    }

    public UsersException(String code) {
        super();
        this.code = code;
    }

    public UsersException(String code, String message) {
        super(message);
        this.code = code;
    }

    public UsersException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
