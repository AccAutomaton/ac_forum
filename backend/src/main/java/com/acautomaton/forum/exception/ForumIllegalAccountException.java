package com.acautomaton.forum.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ForumIllegalAccountException extends RuntimeException {
    public ForumIllegalAccountException(String message) {
        super(message);
    }

    public ForumIllegalAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForumIllegalAccountException(Throwable cause) {
        super(cause);
    }

    public ForumIllegalAccountException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
