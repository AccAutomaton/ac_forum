package com.acautomaton.forum.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FindBackPasswordDTO {
    @NotBlank(message = "用户名不能为空")
    String username;
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "验证码不合法")
    String verifyCode;
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "密码不合法：应至少8位，且同时包含字母和数字(不含特殊符号)")
    String newPassword;
}
