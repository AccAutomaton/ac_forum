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
public enum CoinRecordStatus {
    RUNNING(-1, "交易进行中"),
    SUCCESS(0, "交易成功"),
    FAILED(1, "交易失败"),
    CANCEL(2, "交易取消"),
    REFUND(3, "已退回"),
    OTHER(4, "其它");

    @EnumValue
    private final Integer index;
    private final String value;

    @JsonCreator
    public static CoinRecordStatus getById(Integer index) throws ForumIllegalArgumentException {
        for (CoinRecordStatus value : values()) {
            if (value.index.equals(index)) {
                return value;
            }
        }
        throw new ForumIllegalArgumentException("非法的 CoinRecordStatus index 枚举值: " + index);
    }
}
