package com.acautomaton.forum.vo.vip;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuyVipVO {
    Boolean needPay;
    String pageRedirectionData;
}
