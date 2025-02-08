package com.acautomaton.forum.dto.topic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateTopicDTO {
    @NotNull(message = "话题ID不能为空")
    Integer id;
    @NotBlank(message = "话题名不能为空")
    @Size(max = 32, message = "话题名不能超过32个字")
    String title;
    @NotNull(message = "描述不能为null")
    @Size(max = 1024, message = "描述不能超过32个字")
    String description;
}
