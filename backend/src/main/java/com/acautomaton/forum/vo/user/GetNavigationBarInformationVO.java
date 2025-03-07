package com.acautomaton.forum.vo.user;

import com.acautomaton.forum.entity.Artist;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.entity.Vip;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.enumerate.UserType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetNavigationBarInformationVO {
    Integer uid;
    String nickname;
    UserType userType;
    String avatar;
    Integer coins;
    Integer points;
    Vip vip;
    Integer follows;
    Integer collections;
    Integer articles;

    public GetNavigationBarInformationVO(User user, Vip vip, Artist artist) {
        this.uid = user.getUid();
        this.nickname = user.getNickname();
        this.userType = user.getUserType();
        this.avatar = CosFolderPath.AVATAR + user.getAvatar();
        this.coins = user.getCoins();
        this.points = user.getPoints();
        this.vip = vip;
        this.follows = artist.getFollows();
        this.collections = artist.getCollections();
        this.articles = artist.getArticles();
    }
}
