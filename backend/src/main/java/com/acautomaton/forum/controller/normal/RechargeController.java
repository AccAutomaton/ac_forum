package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.RechargeService;
import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recharge")
public class RechargeController {
    RechargeService rechargeService;
    UserService userService;

    @Autowired
    public RechargeController(RechargeService rechargeService, UserService userService) {
        this.rechargeService = rechargeService;
        this.userService = userService;
    }

    @GetMapping("/get/list")
    public Response getRechargeList(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Response.success(rechargeService.getRechargeList(userService.getCurrentUser().getUid(), pageNumber, pageSize));
    }
    @GetMapping("/get/{rechargeId}")
    public Response getRechargeById(@PathVariable Integer rechargeId) {
        return Response.success(rechargeService.getRechargeById(rechargeId, userService.getCurrentUser().getUid()));
    }
}
