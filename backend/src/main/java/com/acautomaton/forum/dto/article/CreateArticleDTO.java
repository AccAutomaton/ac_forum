package com.acautomaton.forum.dto.article;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateArticleDTO {
    @NotNull(message = "话题不能为空")
    @Min(value = 10000001, message = "话题不存在")
    Integer topic;
    @NotBlank(message = "标题不能为空")
    @Size(max = 32, message = "标题不能超过32个字")
    String title;
    @NotBlank(message = "内容不能为空")
    @Size(max = 100000, message = "内容不能超过100000个字")
    String content;
}
