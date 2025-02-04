package com.acautomaton.forum.config;

import com.acautomaton.forum.entity.SecurityUser;
import com.acautomaton.forum.entity.User;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Slf4j
@Configuration
@NoArgsConstructor
public class WebSocketConfiguration extends ServerEndpointConfig.Configurator {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig serverEndpointConfig, HandshakeRequest request, HandshakeResponse response) {
        User user = ((SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        serverEndpointConfig.getUserProperties().put("uid", user.getUid());
        serverEndpointConfig.getUserProperties().put("username", user.getUsername());
        log.info("用户 {} (用户名: {}) 正在与 WebSocket 服务器 {} 握手", user.getUid(), user.getUsername(), request.getRequestURI().getPath());
    }

    @Override
    public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return super.getEndpointInstance(clazz);
    }
}
