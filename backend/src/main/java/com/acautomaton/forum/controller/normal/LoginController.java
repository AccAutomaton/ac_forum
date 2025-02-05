package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.dto.login.*;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.util.CaptchaService;
import com.acautomaton.forum.service.util.EmailService;
import com.acautomaton.forum.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class LoginController {
    EmailService emailService;
    LoginService loginService;
    CaptchaService captchaService;

    @Autowired
    public LoginController(EmailService emailService, LoginService loginService, CaptchaService captchaService) {
        this.emailService = emailService;
        this.loginService = loginService;
        this.captchaService = captchaService;
    }

    @PostMapping("/register")
    public Response register(@Validated @RequestBody RegisterDTO dto) {
        loginService.register(dto);
        return Response.success();
    }

    @PostMapping("/getEmailVerifyCode/register")
    public Response getEmailVerifyCodeForRegister(@Validated @RequestBody GetEmailVerifyCodeForRegisterDTO dto) {
        loginService.getEmailVerifyCodeForRegister(dto);
        return Response.success();
    }

    @GetMapping("/getCaptcha")
    public Response getCaptcha() {
        return Response.success(captchaService.getCaptcha());
    }

    @PostMapping("/login")
    public Response login(@Validated @RequestBody LoginDTO dto) {
        return Response.success(loginService.login(dto));
    }

    @PostMapping("/getEmailVerifyCode/findBackPassword")
    public Response getEmailVerifyCodeForFindingBackPassword(@Validated @RequestBody GetEmailVerifyCodeForFindingBackPasswordDTO dto) {
        loginService.getEmailVerifyCodeForFindingBackPassword(dto);
        return Response.success();
    }

    @PatchMapping("/findBackPassword")
    public Response findBackPassword(@Validated @RequestBody FindBackPasswordDTO dto) {
        loginService.findBackPassword(dto);
        return Response.success();
    }
}
