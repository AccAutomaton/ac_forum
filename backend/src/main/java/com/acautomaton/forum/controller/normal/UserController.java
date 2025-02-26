package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.dto.user.GetEmailVerifyCodeForSettingEmailDTO;
import com.acautomaton.forum.dto.user.SetEmailDTO;
import com.acautomaton.forum.dto.user.SetNicknameDTO;
import com.acautomaton.forum.dto.user.SetPasswordDTO;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.UserService;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.acautomaton.forum.vo.user.GetDetailsVO;
import com.acautomaton.forum.vo.user.GetNavigationBarInformationVO;
import com.alipay.api.AlipayApiException;
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
        GetNavigationBarInformationVO vo = userService.getNavigationBarInformationByUid(userService.getCurrentUser().getUid());
        return Response.success(vo);
    }

    @GetMapping("/get/avatar")
    public Response getAvatar() {
        return Response.success(CosFolderPath.AVATAR + userService.getAvatarByUid(userService.getCurrentUser().getUid()));
    }

    @GetMapping("/get/avatar/updateAuthorization")
    public Response getAvatarUpdateAuthorization() {
        CosAuthorizationVO vo = userService.getAvatarUpdateAuthorizationByUid(userService.getCurrentUser().getUid());
        return Response.success(Map.of("targetAvatar", vo));
    }

    @PatchMapping("/set/avatar/customization")
    public Response setAvatarCustomization() {
        userService.setAvatarCustomization(userService.getCurrentUser().getUid());
        return Response.success();
    }

    @GetMapping("/get/details")
    public Response getDetails() {
        GetDetailsVO vo = userService.getDetailsByUid(userService.getCurrentUser().getUid());
        return Response.success(vo);
    }

    @PatchMapping("/set/nickname")
    public Response setNickname(@Validated @RequestBody SetNicknameDTO dto) {
        userService.setNickname(userService.getCurrentUser().getUid(), dto.getNewNickname());
        return Response.success();
    }

    @PostMapping("/getEmailVerifyCode/setEmail")
    public Response getEmailVerifyCodeForSettingEmail(@Validated @RequestBody GetEmailVerifyCodeForSettingEmailDTO dto) {
        userService.getEmailVerifyCodeForSettingEmail(dto.getNewEmail(), dto.getCaptchaUUID(), dto.getCaptchaCode());
        return Response.success();
    }

    @PatchMapping("/set/email")
    public Response setEmail(@Validated @RequestBody SetEmailDTO dto) {
        userService.setEmail(userService.getCurrentUser().getUid(), dto.getNewEmail(), dto.getVerifyCode());
        return Response.success();
    }

    @PatchMapping("/set/password")
    public Response setPassword(@Validated @RequestBody SetPasswordDTO dto) {
        userService.setPassword(userService.getCurrentUser().getUid(), dto.getOldPassword(), dto.getNewPassword());
        return Response.success();
    }

    @GetMapping("/get/balance/coin")
    public Response getCoinBalance() {
        Integer balance = userService.getCoinsByUid(userService.getCurrentUser().getUid());
        return Response.success(Map.of("coins", balance));
    }

    @PostMapping("/buy/coin")
    public Response buyCoins(@RequestParam Integer coins) throws AlipayApiException {
        if (coins <= 0 || coins > 99999999) {
            throw new ForumIllegalArgumentException("充值数非法");
        }
        return Response.success(Map.of("pageRedirectionData", userService.buyCoins(userService.getCurrentUser().getUid(), coins)));
    }

    @PostMapping("/buy/coin/payed")
    public Response buyCoinsAfterPaying(@RequestParam String tradeId) throws AlipayApiException {
        userService.afterPaying(userService.getCurrentUser().getUid(), tradeId);
        return Response.success();
    }

    @PostMapping("/pay/coin/refresh")
    public Response refreshPayingStatus() throws AlipayApiException {
        Boolean result = userService.refreshPayingStatusByUid(userService.getCurrentUser().getUid());
        return Response.success(Map.of("hasNewStatus", result));
    }
}
