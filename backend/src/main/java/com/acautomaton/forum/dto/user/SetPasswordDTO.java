package com.acautomaton.forum.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SetPasswordDTO {
    @NotBlank(message = "旧密码不能为空")
    String oldPassword;
    @NotBlank(message = "新密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "新密码不合法：应至少8位，且同时包含字母和数字(不含特殊符号)")
    String newPassword;
}
