package com.acautomaton.forum.enumerate;

import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WebSocketResponseType {
    HANDSHAKE(0, "handshake"),
    HEARTBEAT(1, "heartbeat"),
    MESSAGE(2, "message"),
    ERROR(3, "error");

    private final Integer index;
    private final String value;

    @JsonCreator
    public static WebSocketResponseType getById(Integer index) throws ForumIllegalArgumentException {
        for (WebSocketResponseType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 WebSocketResponseType index 枚举值: " + index);
    }
}
