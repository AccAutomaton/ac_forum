package com.acautomaton.forum.exception;

public class ForumExistentialityException extends RuntimeException {
    public ForumExistentialityException(String message) {
        super(message);
    }

    public ForumExistentialityException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForumExistentialityException(Throwable cause) {
        super(cause);
    }

    public ForumExistentialityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
