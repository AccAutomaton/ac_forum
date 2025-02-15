package com.acautomaton.forum.vo.topic;

import com.acautomaton.forum.vo.util.PageHelperVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetTopicListVO {
    PageHelperVO<GetTopicVO> topicList;
    String avatarPrefix;
}
