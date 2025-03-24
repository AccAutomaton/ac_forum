package com.acautomaton.forum.vo.comment;

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
public class GetCommentVO {
    private Integer id;
    private Integer commenter;
    private String commenterNickname;
    private String commenterAvatar;
    private String content;
    private Integer targetArticle;
    private String targetArticleTitle;
    private Integer targetComment;
    private Integer targetCommenter;
    private String targetCommenterNickname;
    private Integer thumbsUp;
    private Boolean alreadyThumbsUp;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
