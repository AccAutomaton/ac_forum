package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.RechargeService;
import com.acautomaton.forum.service.UserService;
import com.alipay.api.AlipayApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/continue/{rechargeId}")
    public Response continueRechargeById(@PathVariable Integer rechargeId) throws AlipayApiException {
        return Response.success(Map.of("pageRedirectionData", rechargeService.continueRecharge(rechargeId, userService.getCurrentUser().getUid())));
    }

    @PatchMapping("/cancel/{rechargeId}")
    public Response cancelRechargeById(@PathVariable Integer rechargeId) {
        rechargeService.cancelRecharge(rechargeId, userService.getCurrentUser().getUid());
        return Response.success();
    }
}
