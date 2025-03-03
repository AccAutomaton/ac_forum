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
    NONE(0, "大众会员", 0, 0, 8),
    WEEKLY(1, "周度会员", 8800, 7, 16),
    MONTHLY(2, "月度会员", 16800, 30, 32),
    SEANSONLY(3, "季度会员", 32800, 90, 64),
    YEARLY(4, "年度会员", 64800, 365, 128);

    @EnumValue
    private final Integer index;
    private final String value;
    private final Integer price;
    private final Integer days;
    private final Integer pointsPerDay;

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
