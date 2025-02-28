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
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName(value = "recharge", schema = "ac_forum", autoResultMap = true)
public class Recharge {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private String uuid;
    private String tradeId;
    private RechargeChannel channel;
    private RechargeType type;
    private RechargeStatus status;
    private Integer amount;
    private String subject;
    private String comment;
    private Integer coinRecordId;
    @JsonIgnore
    private String systemInfo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @JsonIgnore
    @TableLogic
    private Integer deleteFlag;
}
