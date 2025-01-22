package com.acautomaton.forum.controller;

import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.EmailService;
import com.acautomaton.forum.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@Validated
@RestController
public class LoginController {
    EmailService emailService;
    UserService userService;

    @Autowired
    public LoginController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public Response login(@RequestParam("username") String username, @RequestParam("password") String password) {
        return Response.success();
    }

    @Data
    public static class GetEmailVerifyCodeForRegisterRB {
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        String email;
    }
    @PostMapping("/getEmailVerifyCode/register")
    public Response getEmailVerifyCodeForRegister(@Validated @RequestBody GetEmailVerifyCodeForRegisterRB requestBody) throws MessagingException, UnsupportedEncodingException {
        if (userService.userEmailExists(requestBody.getEmail())) {
            throw new ForumIllegalArgumentException("该邮箱已注册");
        }
        emailService.sendVerifycode("注册", requestBody.getEmail());
        return Response.success();
    }
}
