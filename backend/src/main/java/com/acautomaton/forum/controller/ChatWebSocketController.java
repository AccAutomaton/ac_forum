package com.acautomaton.forum.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.acautomaton.forum.config.WebSocketConfiguration;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.service.UserService;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint(value = "/webSocket/chat", configurator = WebSocketConfiguration.class)
public class ChatWebSocketController implements ApplicationContextAware {
    private static final ConcurrentHashMap<Integer, ChatWebSocketController> webSocketMap = new ConcurrentHashMap<>();
    private static ApplicationContext applicationContext;
    private Session session;
    private User user;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        try {
            ChatWebSocketController.applicationContext = applicationContext;
        }
        catch (BeansException e) {
            log.error("初始化 WebSocket-Chat 上下文时出现异常: {}", e.getMessage());
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        Integer uid = (Integer) getHeader("uid", this.session);
        this.user = getBean(UserService.class).getUserByUid(uid);
        if (webSocketMap.containsKey(this.user.getUid())) {
            webSocketMap.remove(this.user.getUid());
            webSocketMap.put(this.user.getUid(), this);
        }
        else {
            webSocketMap.put(this.user.getUid(), this);
        }
        // send some messages ...
        log.info("用户 {} 连接到 WebSocket: {} 成功, 当前在线数 {}", this.user.getUid(), session.getRequestURI().getPath(), getOnlineCount());
    }

    @OnClose
    public void onClose() {
        webSocketMap.remove(this.user.getUid());
        log.info("用户 {} 断开与 WebSocket: {} 的连接, 当前在线数 {}", this.user.getUid(), this.session.getRequestURI().getPath(), getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.debug("收到用户 {} 发送给 {} 的 WebSocket 消息", this.user.getUid(), session.getRequestURI().getPath());
        if (StrUtil.isNotBlank(message)) {
            JSONObject jsonObject = JSONUtil.parseObj(message);
            jsonObject.append("user", this.user);
            // do something then ...
            // remember to handle exception (if is not json)
        }
    }

    @OnError
    public void onError(Throwable error) {
        log.warn("用户 {} 与 WebSocket: {} 通信时发生错误: {}", this.user.getUid(), this.session.getRequestURI().getPath(), error.getMessage());
    }

    public void Send(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        }
        catch (IOException e) {
            log.error("发送 WebSocket: {} 消息给 {} 时发生错误: {}", this.session.getRequestURI().getPath(), this.user.getUid(), e.getMessage());
        }
    }

    public static boolean sendMessage(String message, Integer uid) {
        if (webSocketMap.containsKey(uid)) {
            webSocketMap.get(uid).Send(message);
            return true;
        }
        else {
            log.info("发送 WebSocket 消息给用户 {} 失败(用户不在线)", uid);
            return false;
        }
    }

    public static Object getHeader(String headerName, Session session) {
        Object header = session.getUserProperties().get(headerName);
        if (header == null) {
            try {
                session.close();
                log.warn("session header 为空, WebSocket-Chat 已断开 (session: {})", session);
            }
            catch (IOException e) {
                log.warn("session header 为空, 尝试断开 WebSocket-Chat 失败 (session: {})", session);
            }
        }
        return header;
    }

    public static synchronized int getOnlineCount() {
        return webSocketMap.size();
    }

    public static <T> T getBean(Class<T> type) {
        try {
            return applicationContext.getBean(type);
        }
        catch (NoUniqueBeanDefinitionException e) {
            String beanName = applicationContext.getBeanNamesForType(type)[0];
            return applicationContext.getBean(beanName, type);
        }
    }

    public static <T> T getBean(String beanName, Class<T> type) {
        return applicationContext.getBean(beanName, type);
    }
}
