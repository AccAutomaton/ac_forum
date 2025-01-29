package com.acautomaton.forum.vo.user;

import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.enumerate.UserType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.tencent.cloud.Credentials;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetNavigationBarInformationVO {
    String username;
    UserType userType;
    String avatarUrlPrepend;
    String avatarUrlAppened;
    Credentials credentials;

    public GetNavigationBarInformationVO(User user, String avatarUrlPrepend, Credentials credentials) {
        this.username = user.getUsername();
        this.userType = user.getUserType();
        this.avatarUrlPrepend = avatarUrlPrepend + CosFolderPath.AVATAR;
        this.avatarUrlAppened = user.getAvatar();
        this.credentials = credentials;
    }
}
