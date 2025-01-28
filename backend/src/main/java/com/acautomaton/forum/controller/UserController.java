package com.acautomaton.forum.controller;

import com.acautomaton.forum.entity.SecurityUser;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.UserService;
import com.acautomaton.forum.vo.user.GetNavigationBarInformationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Response navigationBarInformation() {
        GetNavigationBarInformationVO vo = userService.getNavigationBarInformation(getCurrentUser().getUid());
        return Response.success(vo);
    }

    public User getCurrentUser() {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return securityUser.getUser();
    }
}
