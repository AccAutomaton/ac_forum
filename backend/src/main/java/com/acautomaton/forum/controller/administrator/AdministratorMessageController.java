package com.acautomaton.forum.controller.administrator;

import com.acautomaton.forum.entity.Message;
import com.acautomaton.forum.enumerate.MessageType;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.server.MessageWebSocketServer;
import com.acautomaton.forum.service.MessageService;
import com.acautomaton.forum.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/administrator/message")
public class AdministratorMessageController {
    UserService userService;
    MessageService messageService;

    @Autowired
    public AdministratorMessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @Data
    public static class SendMessageDTO {
        Integer uid;
        String title;
        Integer type;
        String content;
        String targetUrl;
    }

    @PostMapping("/send/temporary")
    public Response sendTemporaryMessage(@RequestBody SendMessageDTO dto) {
        MessageWebSocketServer.sendMessage(dto.getUid(), new Message(
                null, dto.getUid(), dto.getTitle(), MessageType.getById(dto.getType()), dto.getContent(),
                dto.getTargetUrl(), new Date(), null, null
        ));
        log.info("管理员 {} 发送临时消息给用户 {} 成功", userService.getCurrentUser().getUid(), dto.getUid());
        return Response.success();
    }

    @PutMapping("/send/perpetual")
    public Response sendPerpetualMessage(@RequestBody SendMessageDTO dto) {
        messageService.createMessage(
                dto.getUid(), dto.getTitle(), MessageType.getById(dto.getType()), dto.getContent(), dto.getTargetUrl()
        );
        log.info("管理员 {} 发送永久消息给用户 {} 成功", userService.getCurrentUser().getUid(), dto.getUid());
        return Response.success();
    }
}
