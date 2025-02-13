package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.dto.vip.BuyVipDTO;
import com.acautomaton.forum.entity.Vip;
import com.acautomaton.forum.enumerate.VipType;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.UserService;
import com.acautomaton.forum.service.VipService;
import com.acautomaton.forum.vo.vip.GetPriceVO;
import com.alipay.api.AlipayApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/vip")
public class VipController {
    VipService vipService;
    UserService userService;

    @Autowired
    public VipController(VipService vipService, UserService userService) {
        this.vipService = vipService;
        this.userService = userService;
    }

    @GetMapping("/get")
    public Response getVip() {
        Vip vip = vipService.getVipByUid(userService.getCurrentUser().getUid());
        return Response.success(vip);
    }

    @GetMapping("/price")
    public Response getPriceForTargetVip(@RequestParam Integer targetVipIndex) {
        GetPriceVO getPriceVO = vipService.getPriceForTargetVip(
                userService.getCurrentUser().getUid(), VipType.getById(targetVipIndex)
        );
        return Response.success(getPriceVO);
    }

    @PostMapping("/buy")
    public Response buyVip(@RequestBody BuyVipDTO buyVipDTO) throws AlipayApiException {
        return Response.success(Map.of("pageRedirectionData", vipService.buyVip(
                userService.getCurrentUser().getUid(), VipType.getById(buyVipDTO.getTargetVipIndex()), buyVipDTO.getMode()
        )));
    }
}
