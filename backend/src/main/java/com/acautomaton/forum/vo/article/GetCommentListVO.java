package com.acautomaton.forum.vo.article;

import com.acautomaton.forum.vo.comment.GetCommentVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GetCommentListVO {
    PageHelperVO<GetCommentVO> commentList;
    String commenterAvatarPrefix;
}
