package com.acautomaton.forum.entity;

import com.acautomaton.forum.enumerate.CoinRecordStatus;
import com.acautomaton.forum.enumerate.CoinRecordType;
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
@TableName(value = "coin_record", schema = "ac_forum", autoResultMap = true)
public class CoinRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private CoinRecordType type;
    private Integer coinVolume;
    private Integer coinBalance;
    private String project;
    private String comment;
    private CoinRecordStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @JsonIgnore
    @TableLogic
    private Integer deleteFlag;
}
