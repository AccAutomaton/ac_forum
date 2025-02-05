package com.acautomaton.forum.controller.administrator;

import com.acautomaton.forum.entity.Message;
import com.acautomaton.forum.enumerate.MessageType;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.server.MessageWebSocketServer;
import com.acautomaton.forum.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/administrator/message")
public class AdministratorMessageController {
    UserService userService;

    @Autowired
    public AdministratorMessageController(UserService userService) {
        this.userService = userService;
    }

    @Data
    public static class SendTemporaryMessageDTO {
        Integer uid;
        String title;
        Integer type;
        String content;
        String targetUrl;
    }

    @PostMapping("/send/temporary")
    public Response sendTemporaryMessage(@RequestBody SendTemporaryMessageDTO dto) {
        MessageWebSocketServer.sendMessage(dto.getUid(), List.of(new Message(
                null, dto.getUid(), dto.getTitle(), MessageType.getById(dto.getType()), dto.getContent(),
                dto.getTargetUrl(), new Date(), null, null
        )));
        log.info("管理员 {} 发送临时消息给用户 {} 成功", userService.getCurrentUser().getUid(), dto.getUid());
        return Response.success();
    }
}
