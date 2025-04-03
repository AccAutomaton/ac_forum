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

    @GetMapping("/navigationBarInformation")
    public Response getNavigationBarInformation() {
        GetNavigationBarInformationVO vo = userService.getNavigationBarInformationByUid(userService.getCurrentUser().getUid());
        return Response.success(vo);
    }

    @GetMapping("/avatar")
    public Response getAvatar() {
        return Response.success(CosFolderPath.AVATAR + userService.getAvatarByUid(userService.getCurrentUser().getUid()));
    }

    @GetMapping("/avatar/authorization/update")
    public Response getAvatarUpdateAuthorization() {
        CosAuthorizationVO vo = userService.getAvatarUpdateAuthorizationByUid(userService.getCurrentUser().getUid());
        return Response.success(Map.of("targetAvatar", vo));
    }

    @PatchMapping("/avatar/customization")
    public Response setAvatarCustomization() {
        userService.setAvatarCustomization(userService.getCurrentUser().getUid());
        return Response.success();
    }

    @GetMapping("/details")
    public Response getDetails() {
        GetDetailsVO vo = userService.getDetailsByUid(userService.getCurrentUser().getUid());
        return Response.success(vo);
    }

    @PatchMapping("/nickname")
    public Response setNickname(@Validated @RequestBody SetNicknameDTO dto) {
        userService.setNickname(userService.getCurrentUser().getUid(), dto.getNewNickname());
        return Response.success();
    }

    @PostMapping("/emailVerifyCode/updateEmail")
    public Response getEmailVerifyCodeForSettingEmail(@Validated @RequestBody GetEmailVerifyCodeForSettingEmailDTO dto) {
        userService.getEmailVerifyCodeForSettingEmail(dto.getNewEmail(), dto.getCaptchaUUID(), dto.getCaptchaCode());
        return Response.success();
    }

    @PatchMapping("/email")
    public Response setEmail(@Validated @RequestBody SetEmailDTO dto) {
        userService.setEmail(userService.getCurrentUser().getUid(), dto.getNewEmail(), dto.getVerifyCode());
        return Response.success();
    }

    @PatchMapping("/password")
    public Response setPassword(@Validated @RequestBody SetPasswordDTO dto) {
        userService.setPassword(userService.getCurrentUser().getUid(), dto.getOldPassword(), dto.getNewPassword());
        return Response.success();
    }

    @GetMapping("/list/uidAndNickname")
    public Response getUidAndNicknameList(@RequestParam(required = false) Integer uid,
                                          @RequestParam(required = false) String nicknameKeyword) {
        if (uid != null && nicknameKeyword.isBlank()) {
            return Response.success(userService.queryUserUidAndNicknameByUid(uid));
        } else if (uid == null && !nicknameKeyword.isBlank()) {
            return Response.success(userService.queryUserUidAndNicknameByNickname(nicknameKeyword));
        } else {
            throw new ForumIllegalArgumentException("参数非法");
        }
    }
}
