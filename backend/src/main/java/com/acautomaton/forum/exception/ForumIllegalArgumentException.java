package com.acautomaton.forum.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ForumIllegalArgumentException extends RuntimeException {
    public ForumIllegalArgumentException(String message) {
        super(message);
    }

    public ForumIllegalArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForumIllegalArgumentException(Throwable cause) {
        super(cause);
    }

    public ForumIllegalArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
