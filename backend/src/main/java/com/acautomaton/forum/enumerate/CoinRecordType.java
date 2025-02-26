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
public enum CoinRecordType {
    RECHARGE(-2, "充值"),
    REFUND(-1, "退款收入"),
    OTHER(0, "其它"),
    NORMAL_INCOME(1, "普通收益"),
    NORMAL_PAYOUT(2, "普通支出"),
    COIN_INCOME(3, "投币收益"),
    COIN_PAYOUT(4, "投币支出"),
    SIGN_IN_INCOME(5, "签到收益"),
    BUY_VIP_PAYOUT(6, "兑换会员");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static CoinRecordType getById(Integer index) throws ForumIllegalArgumentException {
        for (CoinRecordType value: values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 CoinRecordType index 枚举值: " + index);
    }
}
