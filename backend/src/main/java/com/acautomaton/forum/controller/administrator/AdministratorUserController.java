package com.acautomaton.forum.controller.administrator;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.server.MessageWebSocketServer;
import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/administrator/user")
public class AdministratorUserController {
    UserService userService;

    @Autowired
    public AdministratorUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/online")
    public Response getOnlineUserCount() {
        return Response.success(Map.of("total", userService.getTotalUserCount(), "online", MessageWebSocketServer.getOnlineCount()));
    }
}
