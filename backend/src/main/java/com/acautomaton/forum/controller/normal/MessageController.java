package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.MessageService;
import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/getList")
    public Response getList(@RequestParam Integer pageNumber,
                            @RequestParam Integer pageSize,
                            @RequestParam(required = false) Boolean seen) {
        return Response.success(messageService.getMessages(
            userService.getCurrentUser().getUid(), seen, pageNumber, pageSize
        ));
    }
}
