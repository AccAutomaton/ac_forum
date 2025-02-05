package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.Message;
import com.acautomaton.forum.enumerate.MessageType;
import com.acautomaton.forum.mapper.MessageMapper;
import com.acautomaton.forum.server.MessageWebSocketServer;
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

    @Transactional(rollbackFor = Exception.class)
    public void createMessage(Integer uid, String title, MessageType type, String content, String targetUrl) {
        Message message = new Message(null, uid, title, type, content, targetUrl, new Date(), 0, 0);
        messageMapper.insert(message);
        sendMessage(uid, List.of(message));
    }

    @Async
    public CompletableFuture<Boolean> sendMessage(Integer uid, List<Message> messages) {
        Boolean result = MessageWebSocketServer.sendMessage(uid, messages);
        return CompletableFuture.completedFuture(result);
    }
}
