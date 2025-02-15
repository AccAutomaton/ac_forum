package com.acautomaton.forum.vo.user;

import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.enumerate.UserType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetNavigationBarInformationVO {
    String nickname;
    UserType userType;
    String avatar;

    public GetNavigationBarInformationVO(User user) {
        this.nickname = user.getNickname();
        this.userType = user.getUserType();
        this.avatar = CosFolderPath.AVATAR + user.getAvatar();
    }
}
