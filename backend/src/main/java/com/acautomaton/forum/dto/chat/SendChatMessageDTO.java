package com.acautomaton.forum.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendChatMessageDTO {
    @NotBlank(message = "内容不能为空")
    @Size(max = 1024, message = "内容不能超过1024个字")
    String content;
}
