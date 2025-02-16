package com.acautomaton.forum.vo.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetArticleVO {
    Integer id;
    Integer owner;
    String ownerNickname;
    String ownerAvatar;
    Integer topic;
    String topicTitle;
    String topicAvatar;
    String title;
    String content;
    Integer visits;
    Integer thumbsUp;
    Integer collections;
    Integer tipping;
    Integer forwards;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
}
