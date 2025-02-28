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
public enum RechargeStatus {
    WAITING(-1, "等待支付"),
    SUCCESS(0, "交易成功"),
    FAILED(1, "交易失败"),
    CANCEL(2, "交易取消"),
    REFUND(3, "已退款"),
    OTHER(4, "其它");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static RechargeStatus getById(Integer index) throws ForumIllegalArgumentException {
        for (RechargeStatus value : values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 RechargeStatus index 枚举值: " + index);
    }
}
