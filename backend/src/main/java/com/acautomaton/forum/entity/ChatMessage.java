package com.acautomaton.forum.entity;

import com.acautomaton.forum.enumerate.ChatMessageType;
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
@TableName(value = "chat_message", schema = "ac_forum", autoResultMap = true)
public class ChatMessage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer chatId;
    private ChatMessageType type;
    private Integer sender;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    @JsonIgnore
    @TableLogic
    private Integer deleteFlag;
}
