package com.acautomaton.forum.exception;

public class ForumVerifyException extends RuntimeException {
    public ForumVerifyException(String message) {
        super(message);
    }

    public ForumVerifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForumVerifyException(Throwable cause) {
        super(cause);
    }

    public ForumVerifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
