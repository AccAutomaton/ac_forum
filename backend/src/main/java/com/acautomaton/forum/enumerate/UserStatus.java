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
public enum UserStatus {
    NORMAL(0, "正常"),
    LOCKED(1, "锁定");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static UserStatus getById(Integer index) throws ForumIllegalArgumentException {
        for (UserStatus value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 UserStatus index 枚举值: " + index);
    }
}
