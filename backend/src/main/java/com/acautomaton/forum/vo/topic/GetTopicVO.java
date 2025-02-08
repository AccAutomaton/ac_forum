package com.acautomaton.forum.vo.topic;

import com.acautomaton.forum.exception.ForumException;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
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
    Integer visits;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    Date createTime;
    Object avatar;

    public void setAvatar(Object avatar) {
        if ((avatar != null) && !(avatar instanceof String || avatar instanceof CosAuthorizationVO)) {
            log.info("构造 {} 时 avatar 参数异常: 既不是 [String]，也不是 [CosAuthorizationVO]", GetTopicVO.class);
            throw new ForumException("系统错误，请稍后重试");
        }
        this.avatar = avatar;
    }
}
