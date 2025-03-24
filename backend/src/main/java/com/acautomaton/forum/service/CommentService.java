package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.Article;
import com.acautomaton.forum.entity.Comment;
import com.acautomaton.forum.entity.ThumbsUp;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.MessageType;
import com.acautomaton.forum.enumerate.ThumbsUpType;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalAccountException;
import com.acautomaton.forum.mapper.CommentMapper;
import com.acautomaton.forum.mapper.ThumbsUpMapper;
import com.acautomaton.forum.vo.comment.GetCommentVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class CommentService {
    CommentMapper commentMapper;
    ThumbsUpMapper thumbsUpMapper;
    MessageService messageService;

    @Autowired
    public CommentService(CommentMapper commentMapper, ThumbsUpMapper thumbsUpMapper, MessageService messageService) {
        this.commentMapper = commentMapper;
        this.thumbsUpMapper = thumbsUpMapper;
        this.messageService = messageService;
    }

    public GetCommentVO getCommentById(Integer uid, Integer commentId) {
        MPJLambdaWrapper<Comment> commentLambdaWrapper = new MPJLambdaWrapper<>();
        commentLambdaWrapper.eq(Comment::getId, commentId);
        if (!commentMapper.exists(commentLambdaWrapper)) {
            throw new ForumExistentialityException("评论不存在或已被删除");
        }
        commentLambdaWrapper
                .selectAs("t", Comment::getId, GetCommentVO::getId)
                .selectAs("t", Comment::getCommenter, GetCommentVO::getCommenter)
                .selectAs("t", Comment::getContent, GetCommentVO::getContent)
                .selectAs("t", Comment::getTargetArticle, GetCommentVO::getTargetArticle)
                .selectAs("t", Comment::getTargetComment, GetCommentVO::getTargetComment)
                .selectAs("t", Comment::getCreateTime, GetCommentVO::getCreateTime)
                .selectAs("t", Comment::getThumbsUp, GetCommentVO::getThumbsUp)
                .selectAs("comment_user", User::getNickname, GetCommentVO::getCommenterNickname)
                .selectAs("comment_user", User::getAvatar, GetCommentVO::getCommenterAvatar)
                .selectAs("target_comment", Comment::getCommenter, GetCommentVO::getTargetCommenter)
                .selectAs("target_user", User::getNickname, GetCommentVO::getTargetCommenterNickname)
                .selectAs("target_article", Article::getTitle, GetCommentVO::getTargetArticleTitle)
                .selectAs("thumbs_up", ThumbsUp::getId, GetCommentVO::getAlreadyThumbsUp)
                .leftJoin(Comment.class, "target_comment", Comment::getId, "t", Comment::getTargetComment)
                .leftJoin(User.class, "target_user", User::getUid, "target_comment", Comment::getCommenter)
                .innerJoin(User.class, "comment_user", User::getUid, "t", Comment::getCommenter)
                .innerJoin(Article.class, "target_article", Article::getId, "t", Comment::getTargetArticle)
                .leftJoin(ThumbsUp.class, "thumbs_up", on -> on
                        .eq(ThumbsUp::getBeThumbsUpedId, "t", Comment::getId)
                        .eq(ThumbsUp::getThumbsUper, uid)
                        .eq(ThumbsUp::getType, ThumbsUpType.COMMENT));
        return commentMapper.selectJoinOne(GetCommentVO.class, commentLambdaWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void thumbsUp(User user, Integer commentId) {
        LambdaUpdateWrapper<Comment> commentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        commentLambdaUpdateWrapper.eq(Comment::getId, commentId);
        Comment comment = commentMapper.selectOne(commentLambdaUpdateWrapper);
        if (comment == null) {
            throw new ForumExistentialityException("目标评论不存在");
        }
        LambdaQueryWrapper<ThumbsUp> thumbsUpLambdaWrapper = new LambdaQueryWrapper<>();
        thumbsUpLambdaWrapper
                .eq(ThumbsUp::getThumbsUper, user.getUid())
                .eq(ThumbsUp::getType, ThumbsUpType.COMMENT)
                .eq(ThumbsUp::getBeThumbsUpedId, commentId);
        if (thumbsUpMapper.exists(thumbsUpLambdaWrapper)) {
            return;
        }
        thumbsUpMapper.insert(new ThumbsUp(null, user.getUid(), ThumbsUpType.COMMENT, commentId, new Date()));
        commentLambdaUpdateWrapper.setIncrBy(Comment::getThumbsUp, 1);
        commentMapper.update(commentLambdaUpdateWrapper);
        log.info("用户 {} 点赞了评论 {}", user.getUid(), commentId);
        messageService.createMessage(comment.getCommenter(), "用户 " + user.getNickname() + " 给你的评论点赞了",
                MessageType.NORMAL, comment.getContent(), "/article/" + comment.getTargetArticle() + "?showComment=" + commentId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unThumbsUp(Integer uid, Integer commentId) {
        LambdaUpdateWrapper<ThumbsUp> thumbsUpLambdaWrapper = new LambdaUpdateWrapper<>();
        thumbsUpLambdaWrapper
                .eq(ThumbsUp::getThumbsUper, uid)
                .eq(ThumbsUp::getType, ThumbsUpType.COMMENT)
                .eq(ThumbsUp::getBeThumbsUpedId, commentId);
        if (thumbsUpMapper.exists(thumbsUpLambdaWrapper)) {
            thumbsUpMapper.delete(thumbsUpLambdaWrapper);
            log.info("用户 {} 取消点赞评论 {}", uid, commentId);
            LambdaUpdateWrapper<Comment> commentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            commentLambdaUpdateWrapper
                    .eq(Comment::getId, commentId)
                    .setDecrBy(Comment::getThumbsUp, 1);
            commentMapper.update(commentLambdaUpdateWrapper);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteCommentById(Integer uid, Integer commentId) {
        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getId, commentId);
        Comment comment = commentMapper.selectOne(commentLambdaQueryWrapper);
        if (comment == null) {
            throw new ForumExistentialityException("目标评论不存在");
        }
        if (!comment.getCommenter().equals(uid)) {
            throw new ForumIllegalAccountException("无权进行此操作");
        }
        log.info("用户 {} 删除评论 {}", uid, commentId);
        commentMapper.delete(commentLambdaQueryWrapper);
    }
}
