package com.acautomaton.forum.exception;

public class ForumException extends RuntimeException {
    public ForumException() {
        super("系统错误，请稍后再试");
    }

    public ForumException(String message) {
        super(message);
    }

    public ForumException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForumException(Throwable cause) {
        super(cause);
    }

    public ForumException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
