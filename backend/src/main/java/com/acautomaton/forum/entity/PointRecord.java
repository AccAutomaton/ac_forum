package com.acautomaton.forum.entity;

import com.acautomaton.forum.enumerate.PointRecordStatus;
import com.acautomaton.forum.enumerate.PointRecordType;
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
@TableName(value = "point_record", schema = "ac_forum", autoResultMap = true)
public class PointRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private PointRecordType type;
    private Integer pointVolume;
    private Integer pointBalance;
    private String project;
    private String comment;
    private PointRecordStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @JsonIgnore
    @TableLogic
    private Integer deleteFlag;
}
