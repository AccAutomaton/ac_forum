package com.acautomaton.forum.mapper;

import com.acautomaton.forum.entity.Message;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends MPJBaseMapper<Message> {
}
