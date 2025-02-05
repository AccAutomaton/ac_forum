package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {
    UserService userService;

    @Autowired
    public MessageController(UserService userService) {
        this.userService = userService;
    }
}
