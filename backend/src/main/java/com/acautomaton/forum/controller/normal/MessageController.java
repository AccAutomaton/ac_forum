package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.dto.message.DoReadMessageDTO;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.MessageService;
import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
public class MessageController {
    UserService userService;
    MessageService messageService;

    @Autowired
    public MessageController(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @GetMapping("/get/list")
    public Response getList(@RequestParam Integer pageNumber,
                            @RequestParam Integer pageSize,
                            @RequestParam(required = false) Boolean seen) {
        return Response.success(messageService.getMessagesByUid(
            userService.getCurrentUser().getUid(), seen, pageNumber, pageSize
        ));
    }

    @GetMapping("/get/count/notSeen")
    public Response getNotSeenMessagesCount() {
        return Response.success(messageService.getNotSeenMessagesCountByuid(userService.getCurrentUser().getUid()));
    }

    @PatchMapping("/read")
    public Response doReadMessage(@Validated @RequestBody DoReadMessageDTO dto) {
        messageService.readMessage(dto.getMessageId(), userService.getCurrentUser().getUid());
        return Response.success();
    }
}
