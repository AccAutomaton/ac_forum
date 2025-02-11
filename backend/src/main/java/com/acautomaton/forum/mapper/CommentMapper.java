package com.acautomaton.forum.mapper;

import com.acautomaton.forum.entity.Comment;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends MPJBaseMapper<Comment> {
}
