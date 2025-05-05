package com.acautomaton.forum.dto.article;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddCommentDTO {
    @Min(value = 10000001, message = "目标评论不存在")
    Integer targetCommentId;
    @NotBlank(message = "评论不能为空")
    @Size(max = 1024, message = "评论不能超过1024个字")
    String content;
}
