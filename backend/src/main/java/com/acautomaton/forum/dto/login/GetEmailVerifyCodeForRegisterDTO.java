package com.acautomaton.forum.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GetEmailVerifyCodeForRegisterDTO {
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    String email;
    @NotBlank(message = "图形验证码 UUID 不能为空")
    String captchaUUID;
    @NotBlank(message = "图形验证码不能为空")
    String captchaCode;
}
