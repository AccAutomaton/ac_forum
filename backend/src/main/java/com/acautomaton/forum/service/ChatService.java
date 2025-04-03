package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.Chat;
import com.acautomaton.forum.entity.ChatMessage;
import com.acautomaton.forum.entity.Message;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.ChatMessageType;
import com.acautomaton.forum.enumerate.CosActions;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.enumerate.MessageType;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.mapper.ChatMapper;
import com.acautomaton.forum.mapper.ChatMessageMapper;
import com.acautomaton.forum.mapper.MessageMapper;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.server.ChatWebSocketServer;
import com.acautomaton.forum.service.util.CosService;
import com.acautomaton.forum.vo.chat.CreateChatVO;
import com.acautomaton.forum.vo.chat.GetChatListVO;
import com.acautomaton.forum.vo.chat.GetChatVO;
import com.acautomaton.forum.vo.chat.GetItemOfChatListVO;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.tencent.cloud.Credentials;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ChatService {
    ChatMapper chatMapper;
    UserMapper userMapper;
    ChatMessageMapper chatMessageMapper;
    CosService cosService;
    MessageMapper messageMapper;
    MessageService messageService;

    @Autowired
    public ChatService(ChatMapper chatMapper, UserMapper userMapper, ChatMessageMapper chatMessageMapper,
                       CosService cosService, MessageMapper messageMapper, MessageService messageService) {
        this.chatMapper = chatMapper;
        this.userMapper = userMapper;
        this.chatMessageMapper = chatMessageMapper;
        this.cosService = cosService;
        this.messageMapper = messageMapper;
        this.messageService = messageService;
    }

    @Transactional(rollbackFor = Exception.class)
    public CreateChatVO createChat(User user, Integer receiver) {
        if (user.getUid().equals(receiver)) {
            throw new ForumIllegalArgumentException("不能创建与自己的聊天");
        }
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getUid, receiver);
        if (!userMapper.exists(userQueryWrapper)) {
            throw new ForumExistentialityException("用户不存在");
        }
        LambdaQueryWrapper<Chat> chatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatLambdaQueryWrapper
                .and(i -> i
                        .or(j -> j
                                .eq(Chat::getParticipant1, user.getUid())
                                .eq(Chat::getParticipant2, receiver)
                        )
                        .or(j -> j
                                .eq(Chat::getParticipant1, receiver)
                                .eq(Chat::getParticipant2, user.getUid())
                        )
                );
        Chat chat = chatMapper.selectOne(chatLambdaQueryWrapper);
        if (chat == null) {
            Date now = new Date();
            chat = new Chat(null, user.getUid(), receiver, 0, 0, now, 0);
            chatMapper.insert(chat);
            chatMessageMapper.insert(new ChatMessage(null, chat.getId(), ChatMessageType.SYSTEM, user.getUid(),
                    "${nickname} 发起聊天", now, 0));
            return new CreateChatVO(false, chat.getId());
        } else {
            return new CreateChatVO(true, chat.getId());
        }
    }

    public GetChatVO getChatById(Integer uid, Integer chatId) {
        Chat chat = chatMapper.selectById(chatId);
        if (chat == null || !(chat.getParticipant1().equals(uid) || chat.getParticipant2().equals(uid))) {
            throw new ForumExistentialityException("聊天不存在");
        }
        User receiver = userMapper.selectById(chat.getParticipant1().equals(uid) ? chat.getParticipant2() : chat.getParticipant1());
        LambdaQueryWrapper<ChatMessage> chatMessageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        chatMessageLambdaQueryWrapper
                .eq(ChatMessage::getChatId, chatId)
                .eq(ChatMessage::getType, ChatMessageType.NORMAL)
                .orderByDesc(ChatMessage::getCreateTime)
                .last("limit 1");
        ChatMessage lastMessage = chatMessageMapper.selectOne(chatMessageLambdaQueryWrapper);
        Integer notReadMessages = chat.getParticipant1().equals(uid) ? chat.getParticipant1NotReadMessages() : chat.getParticipant2NotReadMessages();
        return new GetChatVO(chatId, receiver.getUid(), receiver.getNickname(), receiver.getAvatar(), notReadMessages, chat.getUpdateTime(),
                lastMessage == null ? null : lastMessage.getContent());
    }

    public GetChatListVO getChatList(Integer uid, Integer pageNumber, Integer pageSize) {
        return new GetChatListVO(
                new PageHelperVO<>(
                        PageHelper.startPage(pageNumber, pageSize < 20 ? pageSize : 20).doSelectPageInfo(() -> {
                            MPJLambdaWrapper<Chat> chatLambdaWrapper = new MPJLambdaWrapper<>();
                            chatLambdaWrapper
                                    .selectAs(Chat::getId, GetItemOfChatListVO::getChatId)
                                    .selectAs(Chat::getParticipant1, GetItemOfChatListVO::getParticipant1Uid)
                                    .selectAs(Chat::getParticipant2, GetItemOfChatListVO::getParticipant2Uid)
                                    .selectAs(Chat::getParticipant1NotReadMessages, GetItemOfChatListVO::getNotReadMessages)
                                    .selectAs(Chat::getParticipant2NotReadMessages, GetItemOfChatListVO::getNotReadMessages2)
                                    .selectAs(Chat::getUpdateTime, GetItemOfChatListVO::getUpdateTime)
                                    .selectAs("participant_1", User::getNickname, GetItemOfChatListVO::getParticipant1Nickname)
                                    .selectAs("participant_1", User::getAvatar, GetItemOfChatListVO::getParticipant1Avatar)
                                    .selectAs("participant_2", User::getNickname, GetItemOfChatListVO::getParticipant2Nickname)
                                    .selectAs("participant_2", User::getAvatar, GetItemOfChatListVO::getParticipant2Avatar)
                                    .selectSub(ChatMessage.class,
                                            i -> i
                                                    .select(ChatMessage::getContent)
                                                    .eq(Chat::getId, ChatMessage::getChatId)
                                                    .eq(ChatMessage::getType, ChatMessageType.NORMAL)
                                                    .orderByDesc(ChatMessage::getCreateTime)
                                                    .last("limit 1"),
                                            GetItemOfChatListVO::getLatestChatMessage)
                                    .and(i -> i
                                            .or(j -> j.eq(Chat::getParticipant1, uid))
                                            .or(j -> j.eq(Chat::getParticipant2, uid))
                                    )
                                    .orderByDesc(Chat::getUpdateTime)
                                    .innerJoin(User.class, "participant_1", User::getUid, Chat::getParticipant1)
                                    .innerJoin(User.class, "participant_2", User::getUid, Chat::getParticipant2);
                            chatMapper.selectJoinList(GetItemOfChatListVO.class, chatLambdaWrapper);
                        })
                ), uid
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public void readChat(Integer uid, Integer chatId) {
        Chat chat = chatMapper.selectById(chatId);
        if (chat == null || !(chat.getParticipant1().equals(uid) || chat.getParticipant2().equals(uid))) {
            throw new ForumExistentialityException("聊天不存在");
        }
        LambdaUpdateWrapper<Chat> chatLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        if (chat.getParticipant1().equals(uid)) {
            chatLambdaUpdateWrapper.set(Chat::getParticipant1NotReadMessages, 0);
        } else {
            chatLambdaUpdateWrapper.set(Chat::getParticipant2NotReadMessages, 0);
        }
        chatMapper.update(chatLambdaUpdateWrapper);
        log.info("用户 {} 已读私信 {}", uid, chatId);
    }

    public PageHelperVO<ChatMessage> getChatMessageList(Integer uid, Integer chatId, Integer pageSize, Integer beforeChatMessageId) {
        Chat chat = chatMapper.selectById(chatId);
        if (chat == null || !(chat.getParticipant1().equals(uid) || chat.getParticipant2().equals(uid))) {
            throw new ForumExistentialityException("聊天不存在");
        }
        return new PageHelperVO<>(
                PageHelper.startPage(1, pageSize < 20 ? pageSize : 20).doSelectPageInfo(() -> {
                    LambdaQueryWrapper<ChatMessage> chatMessageLambdaQueryWrapper = new LambdaQueryWrapper<>();
                    chatMessageLambdaQueryWrapper
                            .eq(ChatMessage::getChatId, chatId)
                            .lt(beforeChatMessageId != null, ChatMessage::getId, beforeChatMessageId)
                            .orderByDesc(ChatMessage::getId);
                    chatMessageMapper.selectList(chatMessageLambdaQueryWrapper);
                })
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public ChatMessage sendChatNormalMessage(User user, Integer chatId, String content) {
        Chat chat = chatMapper.selectById(chatId);
        if (chat == null || !(chat.getParticipant1().equals(user.getUid()) || chat.getParticipant2().equals(user.getUid()))) {
            throw new ForumExistentialityException("聊天不存在");
        }
        Integer receiver = chat.getParticipant1().equals(user.getUid()) ? chat.getParticipant2() : chat.getParticipant1();
        ChatMessage chatMessage = new ChatMessage(null, chatId, ChatMessageType.NORMAL, user.getUid(), content, new Date(), 0);
        chatMessageMapper.insert(chatMessage);
        LambdaUpdateWrapper<Chat> chatLambdaWrapper = new LambdaUpdateWrapper<>();
        chatLambdaWrapper
                .eq(Chat::getId, chatId)
                .setIncrBy(chat.getParticipant1().equals(receiver), Chat::getParticipant1NotReadMessages, 1)
                .setIncrBy(chat.getParticipant2().equals(receiver), Chat::getParticipant2NotReadMessages, 1)
                .set(Chat::getUpdateTime, new Date());
        chatMapper.update(chatLambdaWrapper);
        log.info("用户 {} 发送了普通私信 {} 给用户 {}", user.getUid(), chatMessage.getId(), receiver);
        if (!ChatWebSocketServer.sendMessage(receiver, chatMessage)) {
            sendGlobalMessageForRemindingChatMessage(user.getNickname(), receiver, chatId, content,
                    chat.getParticipant1().equals(receiver) ? chat.getParticipant1NotReadMessages() : chat.getParticipant2NotReadMessages());
        }
        return chatMessage;
    }

    public CosAuthorizationVO getImageMessageUploadAuthorization(Integer uid, Integer chatId) {
        Chat chat = chatMapper.selectById(chatId);
        if (chat == null || !(chat.getParticipant1().equals(uid) || chat.getParticipant2().equals(uid))) {
            throw new ForumExistentialityException("聊天不存在");
        }
        Integer expireSeconds = 60;
        String imageKey;
        do {
            imageKey = CosFolderPath.CHAT_IMAGE + chatId.toString() + "/" + UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() + ".png";
        } while (cosService.objectExists(imageKey));
        Credentials credentials = cosService.getCosAccessAuthorization(
                expireSeconds, CosActions.PUT_OBJECT, List.of(imageKey)
        );
        log.info("用户 {} 请求了图片 {} 上传权限", uid, imageKey);
        return CosAuthorizationVO.keyAuthorization(credentials, expireSeconds, cosService.getBucketName(), cosService.getRegion(), imageKey);
    }

    public CosAuthorizationVO getImageMessageDownloadAuthorization(Integer uid, Integer chatId) {
        Chat chat = chatMapper.selectById(chatId);
        if (chat == null || !(chat.getParticipant1().equals(uid) || chat.getParticipant2().equals(uid))) {
            throw new ForumExistentialityException("聊天不存在");
        }
        Integer expireSeconds = 60 * 60;
        String prefix = CosFolderPath.CHAT_IMAGE + chatId.toString() + "/*";
        Credentials credentials = cosService.getCosAccessAuthorization(
                expireSeconds, CosActions.GET_OBJECT, List.of(prefix)
        );
        log.info("用户 {} 请求了聊天图片 {}/* 查看权限", uid, prefix);
        return CosAuthorizationVO.prefixAuthorization(credentials, expireSeconds, cosService.getBucketName(), cosService.getRegion(), prefix);
    }

    @Transactional(rollbackFor = Exception.class)
    public ChatMessage sendChatImageMessage(User user, Integer chatId, String imageFileName) {
        Chat chat = chatMapper.selectById(chatId);
        if (chat == null || !(chat.getParticipant1().equals(user.getUid()) || chat.getParticipant2().equals(user.getUid()))) {
            throw new ForumExistentialityException("聊天不存在");
        }
        String imageKey = CosFolderPath.CHAT_IMAGE + chatId.toString() + "/" + imageFileName;
        if (!cosService.objectExists(imageKey)) {
            throw new ForumExistentialityException("上传失败，请重试");
        }
        Integer receiver = chat.getParticipant1().equals(user.getUid()) ? chat.getParticipant2() : chat.getParticipant1();
        ChatMessage chatMessage = new ChatMessage(null, chatId, ChatMessageType.IMAGE, user.getUid(), imageKey, new Date(), 0);
        chatMessageMapper.insert(chatMessage);
        LambdaUpdateWrapper<Chat> chatLambdaWrapper = new LambdaUpdateWrapper<>();
        chatLambdaWrapper
                .eq(Chat::getId, chatId)
                .setIncrBy(chat.getParticipant1().equals(receiver), Chat::getParticipant1NotReadMessages, 1)
                .setIncrBy(chat.getParticipant2().equals(receiver), Chat::getParticipant2NotReadMessages, 1)
                .set(Chat::getUpdateTime, new Date());
        chatMapper.update(chatLambdaWrapper);
        log.info("用户 {} 发送了图片私信 {} 给用户 {}", user.getUid(), chatMessage.getId(), receiver);
        if (!ChatWebSocketServer.sendMessage(receiver, chatMessage)) {
            sendGlobalMessageForRemindingChatMessage(user.getNickname(), receiver, chatId, "[图片]",
                    chat.getParticipant1().equals(receiver) ? chat.getParticipant1NotReadMessages() : chat.getParticipant2NotReadMessages());
        }
        return chatMessage;
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void sendGlobalMessageForRemindingChatMessage(String senderNickname, Integer receiverUid, Integer chatId, String content, Integer notReadMessages) {
        notReadMessages++;
        LambdaQueryWrapper<Message> messageLambdaWrapper = new LambdaQueryWrapper<>();
        messageLambdaWrapper
                .eq(Message::getUid, receiverUid)
                .eq(Message::getType, MessageType.CHAT)
                .eq(Message::getTargetUrl, "/chat/" + chatId)
                .eq(Message::getSeen, 0);
        Message message = messageMapper.selectOne(messageLambdaWrapper);
        if (message == null) {
            message = new Message(null, receiverUid, "用户 " + senderNickname + " 发来 " + notReadMessages + " 条新消息",
                    MessageType.CHAT, content, "/chat/" + chatId, new Date(), 0, 0);
            messageMapper.insert(message);
            log.info("成功创建消息 (id: {}, uid: {})", message.getId(), message.getUid());
        } else {
            message.setTitle("用户 " + senderNickname + " 发来 " + notReadMessages + " 条新消息");
            message.setContent(content);
            message.setCreateTime(new Date());
            messageMapper.updateById(message);
            log.info("成功更新消息 (id: {}, uid: {})", message.getId(), message.getUid());
        }
        CompletableFuture<Boolean> result = messageService.sendMessage(receiverUid, message);
        if (result.get()) {
            log.info("成功发送消息 (id: {}, uid: {})", message.getId(), message.getUid());
        }
    }
}
