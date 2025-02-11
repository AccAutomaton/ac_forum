package com.acautomaton.forum.enumerate;

import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum RechargeChannel {
    OTHER(0, "其它"),
    CASH(1, "现金"),
    ALI_PAY(2, "支付宝"),
    WECHAT_PAY(3, "微信支付");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static RechargeChannel getById(Integer index) throws ForumIllegalArgumentException {
        for (RechargeChannel value : values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 RechargeChannel index 枚举值: " + index);
    }
}
