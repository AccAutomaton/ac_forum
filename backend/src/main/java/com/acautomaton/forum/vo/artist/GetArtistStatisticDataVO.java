package com.acautomaton.forum.vo.artist;

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
public class GetArtistStatisticDataVO {
    Integer uid;
    String nickname;
    String avatar;
    Integer points;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    Integer articles;
    Integer topics;
    Integer thumbsUp;
    Integer collections;
    Integer follows;
    Integer fans;
    String avatarPrefix = CosFolderPath.AVATAR.getPath();

    public GetArtistStatisticDataVO(Integer uid, String nickname, String avatar, Integer points, Date createTime, Integer articles, Integer thumbsUp, Integer topics, Integer collections, Integer follows, Integer fans) {
        this.uid = uid;
        this.nickname = nickname;
        this.avatar = avatar;
        this.points = points;
        this.createTime = createTime;
        this.articles = articles;
        this.thumbsUp = thumbsUp;
        this.topics = topics;
        this.collections = collections;
        this.follows = follows;
        this.fans = fans;
    }
}
