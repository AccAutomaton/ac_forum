package com.acautomaton.forum.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "artist", schema = "ac_forum", autoResultMap = true)
public class Artist {
    @TableId(type = IdType.INPUT)
    private Integer uid;
    private Integer collections;
    private Integer follows;
    private Integer fans;
    private Integer articles;
    @JsonIgnore
    @TableLogic
    private Integer deleteFlag;
}
