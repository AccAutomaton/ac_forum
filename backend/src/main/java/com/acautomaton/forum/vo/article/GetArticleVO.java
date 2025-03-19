package com.acautomaton.forum.vo.article;

import com.acautomaton.forum.enumerate.CosFolderPath;
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
    Integer ownerFans;
    Integer ownerArticles;
    Boolean alreadyFollowOwner;
    Integer topic;
    String topicTitle;
    String topicAvatar;
    Integer topicArticles;
    Integer topicVisits;
    String title;
    String content;
    String firstImage;
    Integer visits;
    Integer thumbsUp;
    Boolean alreadyThumbsUp;
    Integer collections;
    Boolean alreadyCollected;
    Integer tipping;
    Integer forwards;
    Integer comments;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;
    String ownerAvatarPrefix = CosFolderPath.AVATAR.getPath();
    String topicAvatarPrefix = CosFolderPath.TOPIC_AVATAR.getPath();
}
