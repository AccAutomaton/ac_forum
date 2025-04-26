package com.acautomaton.forum.vo.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetTippingsIncreamentVO {
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date date;
    Integer count;
}
