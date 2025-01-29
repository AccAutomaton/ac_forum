package com.acautomaton.forum.vo.user;

import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.UserType;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
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
    CosAuthorizationVO avatar;

    public GetNavigationBarInformationVO(User user, String avatarKey, Credentials credentials, Integer expiredSeconds, String bucket, String region) {
        this.username = user.getUsername();
        this.userType = user.getUserType();
        this.avatar = CosAuthorizationVO.keyAuthorization(
                credentials, expiredSeconds, bucket, region, avatarKey
        );
    }
}
