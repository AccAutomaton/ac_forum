package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.RechargeService;
import com.acautomaton.forum.service.UserService;
import com.alipay.api.AlipayApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
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

    @GetMapping("/list")
    public Response getRechargeList(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Response.success(rechargeService.getRechargeList(userService.getCurrentUser().getUid(), pageNumber, pageSize));
    }
    @GetMapping("/{rechargeId}")
    public Response getRechargeById(@PathVariable Integer rechargeId) {
        return Response.success(rechargeService.getRechargeById(rechargeId, userService.getCurrentUser().getUid()));
    }

    @PostMapping("/continue")
    public Response continueRechargeById(@RequestParam Integer rechargeId) throws AlipayApiException {
        return Response.success(Map.of("pageRedirectionData", rechargeService.continueRecharge(rechargeId, userService.getCurrentUser().getUid())));
    }

    @PatchMapping("/cancel")
    public Response cancelRechargeById(@RequestParam Integer rechargeId) {
        rechargeService.cancelRecharge(rechargeId, userService.getCurrentUser().getUid());
        return Response.success();
    }
}
