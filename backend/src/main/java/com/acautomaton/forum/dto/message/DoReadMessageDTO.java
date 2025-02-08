package com.acautomaton.forum.dto.message;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DoReadMessageDTO {
    @NotNull(message = "消息ID不能为空")
    @Min(value = 10000001, message = "消息不存在")
    Integer messageId;
}
