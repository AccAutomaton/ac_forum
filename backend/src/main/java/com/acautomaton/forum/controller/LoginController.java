package com.acautomaton.forum.controller;

import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.exception.ForumVerifyException;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.EmailService;
import com.acautomaton.forum.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@Slf4j
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

    @Data
    public static class RegisterRB {
        @NotBlank(message = "用户名不能为空")
        @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$", message = "用户名不合法: 应由4~16位字母/数字/下划线/连接符构成")
        String username;
        @NotBlank(message = "密码不能为空")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "密码不合法：应至少8位，且同时包含字母和数字(不含特殊符号)")
        String password;
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        String email;
        @NotBlank(message = "验证码不能为空")
        @Pattern(regexp = "^\\d{6}$", message = "验证码不合法")
        String verifycode;
    }

    @PostMapping("/register")
    public Response register(@Validated @RequestBody RegisterRB requestBody) {
        if (userService.usernameExists(requestBody.getUsername())) {
            throw new ForumIllegalArgumentException("该用户名已注册");
        }
        if (userService.userEmailExists(requestBody.getEmail())) {
            throw new ForumIllegalArgumentException("该邮箱已注册");
        }
        if (!emailService.judgeVerifyCode(requestBody.getEmail(), requestBody.getVerifycode())) {
            throw new ForumVerifyException("邮箱验证码错误");
        }
        if (userService.register(requestBody.getUsername(), requestBody.getPassword(), requestBody.getEmail())) {
            emailService.deleteVerifyCode(requestBody.getEmail());
            log.info("用户 {} 注册成功", requestBody.getUsername());
            return Response.success();
        } else {
            log.warn("用户 {} 注册失败", requestBody.getUsername());
            return Response.error("注册失败，请稍后再试");
        }
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
        log.info("邮箱 {} 请求了一个验证码", requestBody.getEmail());
        emailService.sendVerifycode("注册", requestBody.getEmail());
        return Response.success();
    }
}
