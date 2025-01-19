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
public enum UserType {
    HACKER(0, "银狼"),
    ROOT(1, "根用户"),
    ADMINISTRATOR(2, "管理员"),
    USER(3, "普通用户");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static UserType getById(Integer index) throws ForumIllegalArgumentException {
        for (UserType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 UserType index 枚举值: " + index);
    }
}
