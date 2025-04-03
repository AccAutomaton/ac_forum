package com.acautomaton.forum.enumerate;

import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ChatMessageType {
    SYSTEM(-1, "系统消息"),
    NORMAL(0, "普通消息"),
    IMAGE(1, "图片消息");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static ChatMessageType getById(Integer index) throws ForumIllegalArgumentException {
        for (ChatMessageType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 ChatMessageType index 枚举值: " + index);
    }
}
