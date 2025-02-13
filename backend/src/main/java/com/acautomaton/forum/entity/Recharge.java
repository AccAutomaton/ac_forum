package com.acautomaton.forum.entity;

import com.acautomaton.forum.enumerate.RechargeChannel;
import com.acautomaton.forum.enumerate.RechargeStatus;
import com.acautomaton.forum.enumerate.RechargeType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName(value = "recharge", schema = "ac_forum", autoResultMap = true)
public class Recharge {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String uuid;
    private String trade_id;
    private RechargeChannel channel;
    private RechargeType type;
    private RechargeStatus status;
    private Integer amount;
    private String project;
    private String comment;
    @JsonIgnore
    private String privateComment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @JsonIgnore
    @TableLogic
    private Integer deleteFlag;
}
