package com.acautomaton.forum.entity;

import com.acautomaton.forum.enumerate.VipType;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "vip", schema = "ac_forum", autoResultMap = true)
public class Vip {
    private Integer uid;
    private VipType type;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expirationTime;
    private Integer coins;
    private Integer points;
    @JsonIgnore
    @TableLogic
    private Integer deleteFlag;
}
