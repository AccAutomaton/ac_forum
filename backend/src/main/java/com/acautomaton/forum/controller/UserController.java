package com.acautomaton.forum.controller;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.UserService;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.acautomaton.forum.vo.user.GetNavigationBarInformationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/user")
public class UserController {
    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/navigationBarInformation")
    public Response getNavigationBarInformation() {
        GetNavigationBarInformationVO vo = userService.getNavigationBarInformation(userService.getCurrentUser().getUid());
        return Response.success(vo);
    }

    @GetMapping("/avatar")
    public Response getAvatar() {
        CosAuthorizationVO vo = userService.getAvatar(userService.getCurrentUser().getUid());
        return Response.success(Map.of("avatar", vo));
    }
}
