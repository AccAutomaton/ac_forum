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
public enum TopicQueryType {
    BEST_MATCH(0, "最佳匹配", Sort.by(Sort.Direction.DESC, "createTime")),
    CREATE_TIME_ASC(1, "时间升序", Sort.by(Sort.Direction.ASC, "createTime")),
    CREATE_TIME_DESC(2, "时间降序", Sort.by(Sort.Direction.DESC, "createTime")),
    VISITS_ASC(3, "浏览量升序", Sort.by(Sort.Direction.ASC, "visits")),
    VISITS_DESC(4, "浏览量降序", Sort.by(Sort.Direction.DESC, "visits")),
    ARTICLES_ASC(5, "文章数升序", Sort.by(Sort.Direction.ASC, "articles")),
    ARTICLES_DESC(6, "文章数降序", Sort.by(Sort.Direction.DESC, "articles"));

    @EnumValue
    private final Integer index;
    private final String value;
    private final Sort sort;

    @JsonCreator
    public static TopicQueryType getById(Integer index) throws ForumIllegalArgumentException {
        for (TopicQueryType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 TopicQueryType index 枚举值: " + index);
    }
}
