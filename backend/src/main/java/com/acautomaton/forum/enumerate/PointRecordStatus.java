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
public enum PointRecordStatus {
    SUCCESS(0, "有效"),
    FAILED(1, "无效"),
    OTHER(2, "其它");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static PointRecordStatus getById(Integer index) throws ForumIllegalArgumentException {
        for (PointRecordStatus value : values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 PointRecordStatus index 枚举值: " + index);
    }
}
