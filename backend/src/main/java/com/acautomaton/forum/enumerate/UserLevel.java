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
    IRON(0, "黑铁", 1024),
    BRONZE(1, "青铜", 2048),
    SILVER(2, "白银", 4096),
    GOLD(3, "黄金", 8192),
    PLATINUM(4, "铂金", 16384),
    DIAMOND(5, "钻石", 32768),
    ASCENDANT(6, "超凡", 65536),
    IMMORTAL(7, "神话", 131072),
    RADIANT(8, "辐能", 999999999);

    @EnumValue
    private final Integer index;
    private final String value;
    private final Integer maxPoint;

    @JsonCreator
    public static UserLevel getById(Integer index) throws ForumIllegalArgumentException {
        for (UserLevel value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 UserLevel index 枚举值: " + index);
    }

    public static UserLevel getByPoint(Integer point) {
        UserLevel level = UserLevel.RADIANT;
        for (UserLevel value: values()) {
            if (value.maxPoint > point) {
                level = value;
                break;
            }
        }
        return level;
    }
}
