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
public enum MessageType {
    NORMAL(0, "普通消息"),
    RECHARGE(1, "充值消息");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static MessageType getById(Integer index) throws ForumIllegalArgumentException {
        for (MessageType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 MessageType index 枚举值: " + index);
    }
}
