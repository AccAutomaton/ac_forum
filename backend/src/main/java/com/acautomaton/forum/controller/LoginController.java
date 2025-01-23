package com.acautomaton.forum.controller;

import com.acautomaton.forum.dto.login.GetEmailVerifyCodeForRegisterDTO;
import com.acautomaton.forum.dto.login.GetEmailVerifyCodeForResettingPasswordDTO;
import com.acautomaton.forum.dto.login.LoginDTO;
import com.acautomaton.forum.dto.login.RegisterDTO;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.CaptchaService;
import com.acautomaton.forum.service.EmailService;
import com.acautomaton.forum.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/getEmailVerifyCode/resetPassword")
    public Response getEmailVerifyCodeForResettingPassword(@Validated @RequestBody GetEmailVerifyCodeForResettingPasswordDTO dto) {
        loginService.getEmailVerifyCodeForResettingPassword(dto);
        return Response.success();
    }
}
