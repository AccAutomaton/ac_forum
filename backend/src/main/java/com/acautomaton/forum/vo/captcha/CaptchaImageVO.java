package com.acautomaton.forum.vo.captcha;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CaptchaImageVO {
    String captchaUUID;
    String base64Image;
}
