package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.FollowService;
import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
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

    @PutMapping("")
    public Response follow(@RequestParam Integer targetUid) {
        followService.follow(userService.getCurrentUser().getUid(), targetUid);
        return Response.success();
    }

    @DeleteMapping("")
    public Response unfollow(@RequestParam Integer targetUid) {
        followService.unfollow(userService.getCurrentUser().getUid(), targetUid);
        return Response.success();
    }

    @GetMapping("/{beFollowedUid}")
    public Response hadFollowed(@PathVariable Integer beFollowedUid) {
        return Response.success(followService.hadFollowed(userService.getCurrentUser().getUid(), beFollowedUid));
    }

    @GetMapping("/increament")
    public Response getFansIncreamentNearly7Days() {
        return Response.success(followService.getFansIncreamentNearly7Days(userService.getCurrentUser().getUid()));
    }
}
