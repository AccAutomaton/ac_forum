package com.acautomaton.forum.entity;

import com.acautomaton.forum.enumerate.MessageType;
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
@TableName(value = "message", schema = "ac_forum", autoResultMap = true)
public class Message {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private String title;
    private MessageType type;
    private String content;
    private String targetUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Integer seen;
    @JsonIgnore
    @TableLogic
    private Integer deleteFlag;
}
