package com.acautomaton.forum.dto.vip;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BuyVipDTO {
    @NotNull(message = "会员参数不能为空")
    @Max(value = 4, message = "会员参数非法")
    @Min(value = 0, message = "会员参数非法")
    Integer targetVipIndex;
    @NotNull(message = "购买方式不能为空")
    @Max(value = 1, message = "购买方式非法")
    @Min(value = 0, message = "购买方式非法")
    Integer mode;
}
