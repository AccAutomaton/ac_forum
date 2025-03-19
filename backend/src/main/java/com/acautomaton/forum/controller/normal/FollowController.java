package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.FollowService;
import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follow")
public class FollowController {
    UserService userService;
    FollowService followService;

    @Autowired
    public FollowController(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }

    @PatchMapping("/do")
    public Response follow(@RequestParam Integer targetUid) {
        followService.follow(userService.getCurrentUser().getUid(), targetUid);
        return Response.success();
    }

    @PatchMapping("/undo")
    public Response unfollow(@RequestParam Integer targetUid) {
        followService.unfollow(userService.getCurrentUser().getUid(), targetUid);
        return Response.success();
    }
}
