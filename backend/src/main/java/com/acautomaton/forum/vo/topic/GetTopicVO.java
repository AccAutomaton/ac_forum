package com.acautomaton.forum.vo.topic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetTopicVO {
    Integer id;
    String title;
    String description;
    Integer administratorId;
    String administratorNickname;
    String administratorAvatar;
    Integer articles;
    Integer visits;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date createTime;
    String avatar;
}
