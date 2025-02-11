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
public enum VipType {
    NONE(0, "无会员"),
    WEEKLY(1, "周度会员"),
    MONTHLY(2, "月度会员"),
    SEANSONLY(3, "季度会员"),
    YEARLY(4, "年度会员");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static VipType getById(Integer index) throws ForumIllegalArgumentException {
        for (VipType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 VipType index 枚举值: " + index);
    }
}
