package com.acautomaton.forum.vo.artist;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetCreationDashboardDataVO {
    Integer topics;
    Integer articles;
    Integer fans;
    Integer tippings;
}
