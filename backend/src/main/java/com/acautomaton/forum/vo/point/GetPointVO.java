package com.acautomaton.forum.vo.point;

import com.acautomaton.forum.enumerate.UserLevel;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPointVO {
    Integer points;
    UserLevel userLevel;
}
