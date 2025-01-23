package com.acautomaton.forum.service;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.acautomaton.forum.exception.ForumObjectExpireException;
import com.acautomaton.forum.util.RedisUtil;
import com.acautomaton.forum.vo.captcha.CaptchaImageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class CaptchaService {
    @Value("${spring.profiles.active}")
    private String env;
    RedisUtil redisUtil;

    @Autowired
    public CaptchaService(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    private final static LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);

    public CaptchaImageVO getCaptcha() {
        lineCaptcha.createCode();
        String captchaUUID = UUID.randomUUID().toString();
        redisUtil.set("captcha_" + captchaUUID, lineCaptcha.getCode(), 5 * 60L);
        if ("dev".equals(env)) {
            log.debug("[dev] 生成图形验证码 {}: {}", captchaUUID, lineCaptcha.getCode());
        }
        else {
            log.info("生成图形验证码 {}", captchaUUID);
        }
        return new CaptchaImageVO(captchaUUID, lineCaptcha.getImageBase64Data());
    }

    public boolean checkCaptcha(String captchaUUID, String code) {
        String correctCode = (String) redisUtil.get("captcha_" + captchaUUID);
        if (correctCode == null) {
            throw new ForumObjectExpireException("图形验证码已过期，请重试");
        }
        redisUtil.deleteKeys("captcha_" + captchaUUID);
        if (correctCode.equalsIgnoreCase(code)) {
            log.info("校验图形验证码 {} 正确", captchaUUID);
            return true;
        } else {
            log.info("校验图形验证码 {} 错误", captchaUUID);
            return false;
        }
    }
}
