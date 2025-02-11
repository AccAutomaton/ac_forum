package com.acautomaton.forum.mapper;

import com.acautomaton.forum.entity.ChatMessage;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends MPJBaseMapper<ChatMessage> {
}
