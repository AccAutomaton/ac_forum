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
public enum UserLevel {
    IRON(0, "黑铁"),
    BRONZE(1, "青铜"),
    SILVER(2, "白银"),
    GOLD(3, "黄金"),
    PLATINUM(4, "铂金"),
    DIAMOND(5, "钻石"),
    ASCENDANT(6, "超凡"),
    IMMORTAL(7, "神话"),
    RADIANT(8, "辐能");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static UserLevel getById(Integer index) throws ForumIllegalArgumentException {
        for (UserLevel value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 UserLevel index 枚举值: " + index);
    }
}
