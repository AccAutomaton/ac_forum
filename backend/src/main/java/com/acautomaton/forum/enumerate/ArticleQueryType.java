package com.acautomaton.forum.enumerate;

import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ArticleQueryType {
    BEST_MATCH(0, "最佳匹配", Sort.unsorted()),
    CREATE_TIME_ASC(1, "时间升序", Sort.by(Sort.Direction.ASC, "createTime")),
    CREATE_TIME_DESC(2, "时间降序", Sort.by(Sort.Direction.DESC, "createTime")),
    VISITS_ASC(3, "浏览量升序", Sort.by(Sort.Direction.ASC, "visits")),
    VISITS_DESC(4, "浏览量降序", Sort.by(Sort.Direction.DESC, "visits")),
    THUMBS_UP_ASC(5, "点赞量升序", Sort.by(Sort.Direction.ASC, "thumbsUp")),
    THUMBS_UP_DESC(6, "点赞量降序", Sort.by(Sort.Direction.DESC, "thumbsUp")),
    TIPPING_ASC(7, "投币量升序", Sort.by(Sort.Direction.ASC, "tipping")),
    TIPPING_DESC(8, "投币量降序", Sort.by(Sort.Direction.DESC, "tipping")),
    FORWARDS_ASC(9, "转发量升序", Sort.by(Sort.Direction.ASC, "forwards")),
    FORWARDS_DESC(10, "转发量降序", Sort.by(Sort.Direction.DESC, "forwards"));

    @EnumValue
    private final Integer index;
    private final String value;
    private final Sort sort;

    @JsonCreator
    public static ArticleQueryType getById(Integer index) throws ForumIllegalArgumentException {
        for (ArticleQueryType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 ArticleQueryType index 枚举值: " + index);
    }
}
