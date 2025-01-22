package com.acautomaton.forum.exception;

public class ForumRequestTooFrequentException extends RuntimeException {
    public ForumRequestTooFrequentException(String message) {
        super(message);
    }

    public ForumRequestTooFrequentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForumRequestTooFrequentException(Throwable cause) {
        super(cause);
    }

    public ForumRequestTooFrequentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
