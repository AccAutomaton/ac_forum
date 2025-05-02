package com.acautomaton.forum.entity;

import com.acautomaton.forum.enumerate.ThumbsUpType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@TableName(value = "thumbs_up", schema = "ac_forum", autoResultMap = true)
public class ThumbsUp {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer thumbsUper;
    private ThumbsUpType type;
    private Integer beThumbsUpedId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;
}
