package com.acautomaton.forum.exception;

public class ForumObjectExpireException extends RuntimeException {
    public ForumObjectExpireException(String message) {
        super(message);
    }

    public ForumObjectExpireException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForumObjectExpireException(Throwable cause) {
        super(cause);
    }

    public ForumObjectExpireException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
