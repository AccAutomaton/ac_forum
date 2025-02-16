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
public enum BrowseRecordType {
    OTHER(0, "其它"),
    TOPIC(1, "话题"),
    ARTICLE(2, "文章");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static BrowseRecordType getById(Integer index) throws ForumIllegalArgumentException {
        for (BrowseRecordType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 BrowseRecordType index 枚举值: " + index);
    }
}
