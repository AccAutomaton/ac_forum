package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.PointService;
import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/point")
public class PointController {
    UserService userService;
    PointService pointService;

    @Autowired
    public PointController(UserService userService, PointService pointService) {
        this.userService = userService;
        this.pointService = pointService;
    }

    @GetMapping("")
    public Response getPoint() {
        return Response.success(pointService.getPoint(userService.getCurrentUser()));
    }

    @GetMapping("/list")
    public Response getPointList(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Response.success(pointService.getPointRecordListByUid(userService.getCurrentUser().getUid(), pageNumber, pageSize));
    }

    @GetMapping("/{pointRecordId}")
    public Response getPointRecordById(@PathVariable Integer pointRecordId) {
        return Response.success(pointService.getPointRecordById(pointRecordId, userService.getCurrentUser().getUid()));
    }
}
