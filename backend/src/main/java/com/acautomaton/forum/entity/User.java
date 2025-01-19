package com.acautomaton.forum.entity;

import com.acautomaton.forum.enumerate.UserStatus;
import com.acautomaton.forum.enumerate.UserType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@TableName(value = "user", schema = "ac_forum", autoResultMap = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer uid;
    private String username;
    private String password;
    private String email;
    private UserStatus status;
    private String phone;
    private UserType userType;
    private String avatar;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @JsonIgnore
    @TableLogic
    private Integer deleteFlag;
}
