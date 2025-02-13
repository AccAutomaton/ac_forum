package com.acautomaton.forum.vo.vip;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPriceVO {
    Integer ownCoins;
    Integer payCoins;
    Integer priceWithCoins;
    Integer priceWithoutCoins;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date targetExpireDate;
}
