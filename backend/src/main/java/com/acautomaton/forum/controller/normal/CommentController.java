package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.CommentService;
import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    CommentService commentService;
    UserService userService;

    @Autowired
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/{commentId}")
    public Response getComment(@PathVariable Integer commentId) {
        return Response.success(commentService.getCommentById(userService.getCurrentUser().getUid(), commentId));
    }

    @PatchMapping("/{commentId}/thumbsUp")
    public Response thumbsUp(@PathVariable Integer commentId) {
        commentService.thumbsUp(userService.getCurrentUser(), commentId);
        return Response.success();
    }

    @PatchMapping("/{commentId}/unThumbsUp")
    public Response unThumbsUp(@PathVariable Integer commentId) {
        commentService.unThumbsUp(userService.getCurrentUser().getUid(), commentId);
        return Response.success();
    }

    @DeleteMapping("/{commentId}")
    public Response deleteComment(@PathVariable Integer commentId) {
        commentService.deleteCommentById(userService.getCurrentUser().getUid(), commentId);
        return Response.success();
    }
}
