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
public enum RechargeType {
    REFUND(-1, "退款"),
    OTHER(0, "其它"),
    INCOME(1, "购买");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static RechargeType getById(Integer index) throws ForumIllegalArgumentException {
        for (RechargeType value : values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 RechargeType index 枚举值: " + index);
    }
}
