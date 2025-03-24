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
public enum ThumbsUpType {
    OTHER(0, "其它"),
    ARTICLE(1, "文章"),
    COMMENT(2, "评论");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static ThumbsUpType getById(Integer index) throws ForumIllegalArgumentException {
        for (ThumbsUpType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 ThumbsUpType index 枚举值: " + index);
    }
}
