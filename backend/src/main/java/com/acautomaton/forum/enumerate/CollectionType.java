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
public enum CollectionType {
    OTHER(0, "其它"),
    TOPIC(1, "话题"),
    ARTICLE(2, "贴子");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static CollectionType getById(Integer index) throws ForumIllegalArgumentException {
        for (CollectionType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 CollectionType index 枚举值: " + index);
    }
}
