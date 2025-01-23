package com.acautomaton.forum.exception;

public class ForumEmailException extends RuntimeException {
    public ForumEmailException(String message) {
        super(message);
    }

    public ForumEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForumEmailException(Throwable cause) {
        super(cause);
    }

    public ForumEmailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
