package com.acautomaton.forum.controller.root;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.LoginService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/root")
public class RootLoginController {
    LoginService loginService;

    public RootLoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Data
    public static class RootRegisterDTO {
        @NotBlank(message = "用户名不能为空")
        @Pattern(regexp = "^[a-zA-Z0-9_-]{4,16}$", message = "用户名不合法: 应由4~16位字母/数字/下划线/连接符构成")
        String username;
        @NotBlank(message = "密码不能为空")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "密码不合法：应至少8位，且同时包含字母和数字(不含特殊符号)")
        String password;
        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        String email;
    }
    @PutMapping("/register")
    public Response register(@RequestBody @Validated RootRegisterDTO dto) {
        loginService.rootRegister(dto);
        return Response.success();
    }
}
