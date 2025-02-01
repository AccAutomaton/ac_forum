package com.acautomaton.forum.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetNicknameDTO {
    @NotBlank(message = "昵称不能为空")
    @Size(max = 16, message = "昵称不能超过16个字")
    String newNickname;
}
