package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.Message;
import com.acautomaton.forum.enumerate.MessageType;
import com.acautomaton.forum.exception.ForumIllegalAccountException;
import com.acautomaton.forum.mapper.MessageMapper;
import com.acautomaton.forum.server.MessageWebSocketServer;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class MessageService {
    MessageMapper messageMapper;

    @Autowired
    public MessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void createMessage(Integer uid, String title, MessageType type, String content, String targetUrl) {
        Message message = new Message(null, uid, title, type, content, targetUrl, new Date(), 0, 0);
        messageMapper.insert(message);
        log.info("成功创建消息 (id: {}, uid: {})", message.getId(), message.getUid());
        CompletableFuture<Boolean> result = sendMessage(uid, message);
        if (result.get()) {
            log.info("成功发送消息 (id: {}, uid: {})", message.getId(), message.getUid());
        }
    }

    @Async
    public CompletableFuture<Boolean> sendMessage(Integer uid, Message message) {
        Boolean result = MessageWebSocketServer.sendMessage(uid, message);
        return CompletableFuture.completedFuture(result);
    }

    public PageHelperVO<Message> getMessagesByUid(Integer uid, Boolean seen, Integer pageNumber, Integer pageSize) {
        return new PageHelperVO<>(
                PageHelper.startPage(pageNumber, pageSize < 10 ? pageSize : 10).doSelectPageInfo(() -> getMessagesByUid(uid, seen))
        );
    }

    public Long getNotSeenMessagesCountByuid(Integer uid) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq("seen", 0);
        return messageMapper.selectCount(queryWrapper);
    }

    @SuppressWarnings("UnusedReturnValue")
    private List<Message> getMessagesByUid(Integer uid, Boolean seen) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.eq(seen != null, "seen", Boolean.TRUE.equals(seen) ? 1 : 0);
        queryWrapper.orderByDesc("create_time");
        return messageMapper.selectList(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void readMessage(Integer id, Integer uid) {
        UpdateWrapper<Message> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.eq("id", id);
        if (!messageMapper.exists(updateWrapper)) {
            throw new ForumIllegalAccountException("消息不存在");
        }
        updateWrapper.set("seen", 1);
        messageMapper.update(updateWrapper);
        log.info("用户 (uid: {}) 已读消息 (id: {})", uid, id);
    }
}
