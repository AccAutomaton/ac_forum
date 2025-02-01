package com.acautomaton.forum.controller;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.UserService;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.acautomaton.forum.vo.user.GetNavigationBarInformationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/get/navigationBarInformation")
    public Response getNavigationBarInformation() {
        GetNavigationBarInformationVO vo = userService.getNavigationBarInformation(userService.getCurrentUser().getUid());
        return Response.success(vo);
    }

    @GetMapping("/get/avatar/getAuthorization")
    public Response getAvatarGetAuthorization() {
        CosAuthorizationVO vo = userService.getAvatarGetAuthorization(userService.getCurrentUser().getUid());
        return Response.success(Map.of("avatar", vo));
    }

    @GetMapping("/get/avatar/updateAuthorization")
    public Response getAvatarUpdateAuthorization() {
        CosAuthorizationVO vo = userService.getAvatarUpdateAuthorization(userService.getCurrentUser().getUid());
        return Response.success(Map.of("targetAvatar", vo));
    }

    @PatchMapping("/set/avatar/customization")
    public Response setAvatarCustomization() {
        userService.setAvatarCustomization(userService.getCurrentUser().getUid());
        return Response.success();
    }

    @GetMapping("/get/details")
    public Response getDetails() {
        GetDetailsVO vo = userService.getDetails(userService.getCurrentUser().getUid());
        return Response.success(vo);
    }
}
