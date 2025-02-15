package com.acautomaton.forum.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CosFolderPath {
    AVATAR("avatar/"),
    TOPIC_AVATAR("topic/avatar/");

    private final String path;

    @Override
    public String toString() {
        return this.path;
    }
}
