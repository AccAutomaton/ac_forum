package com.acautomaton.forum.vo.user;

import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.UserStatus;
import com.acautomaton.forum.enumerate.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetDetailsVO {
    Integer uid;
    String username;
    String email;
    UserStatus status;
    UserType userType;
    String nickname;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date updateTime;

    public GetDetailsVO(User user) {
        this.uid = user.getUid();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.status = user.getStatus();
        this.userType = user.getUserType();
        this.nickname = user.getNickname();
        this.createTime = user.getCreateTime();
        this.updateTime = user.getUpdateTime();
    }
}
