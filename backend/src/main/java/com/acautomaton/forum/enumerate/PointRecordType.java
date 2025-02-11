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
public enum PointRecordType {
    OTHER(0, "其它"),
    NORMAL_INCOME(1, "普通收益"),
    COIN_INCOME(2, "投币收益"),
    SIGN_IN_INCOME(3, "签到收益");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static PointRecordType getById(Integer index) throws ForumIllegalArgumentException {
        for (PointRecordType value : values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 PointRecordType index 枚举值: " + index);
    }
}
