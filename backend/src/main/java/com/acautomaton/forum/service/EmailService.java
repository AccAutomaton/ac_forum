package com.acautomaton.forum.service;

import com.acautomaton.forum.exception.ForumRequestTooFrequentException;
import com.acautomaton.forum.util.FrequencyCheckUtil;
import com.acautomaton.forum.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Random;

@Slf4j
@Service
public class EmailService {
    @Value("${spring.profiles.active}")
    private String env;
    JavaMailSender javaMailSender;
    FrequencyCheckUtil frequencyCheckUtil;
    RedisUtil redisUtil;

    @Autowired
    public EmailService(JavaMailSender javaMailSender, FrequencyCheckUtil frequencyCheckUtil, RedisUtil redisUtil) {
        this.javaMailSender = javaMailSender;
        this.frequencyCheckUtil = frequencyCheckUtil;
        this.redisUtil = redisUtil;
    }

    public void sendVerifycode(String project, String receiveAddress) throws MessagingException, UnsupportedEncodingException {
        log.info("邮箱 {} 正试图请求一个验证码", receiveAddress);

        if (!frequencyCheckUtil.publicProjectFrequencyAccess("EmailVerifyCodePerMinute_" + receiveAddress,
                60L, 1, 60L)) {
            throw new ForumRequestTooFrequentException("一分钟内只能请求一次邮箱验证码，请稍后再试");
        }
        if (!frequencyCheckUtil.publicProjectFrequencyAccess("EmailVerifyCodePerHour_" + receiveAddress,
                60 * 60L, 5, 60 * 60L)) {
            log.warn("邮箱 {} 请求验证码频率过高已被封禁 1 小时", receiveAddress);
            throw new ForumRequestTooFrequentException("请求邮箱验证码的频率过高，请稍后再试");
        }

        Random random = new Random(System.currentTimeMillis() + 114514L);
        String verifyCode = Integer.toString(random.nextInt(899999) + 100000);
        redisUtil.set("emailVerifyCode_" + receiveAddress, verifyCode, 15 * 60L);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
        helper.setFrom("ac_forum@mail.acautomaton.com", "AC论坛");
        helper.setTo(receiveAddress);
        helper.setSubject("AC论坛 - 验证码");
        helper.setText(getEmailText(project, verifyCode), true);

        if ("dev".equals(env)) {
            log.debug("[dev] 成功向 {} 发送 {} 验证码 {}", receiveAddress, project, verifyCode);
        }
        else {
            javaMailSender.send(mimeMessage);
            log.info("成功向 {} 发送 {} 验证码", receiveAddress, project);
        }
    }

    public boolean judgeVerifyCode(String receiveAddress, String verifyCode) {
        if (verifyCode.equals(redisUtil.get("emailVerifyCode_" + receiveAddress))) {
            log.info("校验邮箱 {} 验证码正确", receiveAddress);
            return true;
        } else {
            log.info("校验邮箱 {} 验证码错误", receiveAddress);
            return false;
        }
    }

    public void deleteVerifyCode(String receiveAddress) {
        log.info("删除邮箱 {} 验证码（使用结束）", receiveAddress);
        redisUtil.deleteKeys("emailVerifyCode_" + receiveAddress);
    }

    private static String getEmailText(String project, String verifyCode) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                
                <head>
                    <meta charset="UTF-8">
                    <meta http-equiv="X-UA-Compatible" content="IE=edge">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>AC论坛 - 验证码</title>
                </head>
                
                <body>
                    <div style="margin-bottom: 1em;">【验证码】</div>
                    <div style="margin-bottom: 1em;">您好！欢迎使用AC论坛, 您的
                """
                + project + " " +
                """
                        验证码为</div>
                            <br>
                            <div style="font-size: 2em;margin-bottom: 1em;color: grey;font-weight: 800;">
                        """
                + verifyCode +
                """
                            </div>
                            <br>
                            <div style="margin-bottom: 1em;">验证码15分钟内有效, 请及时使用。</div>
                        </body>
                        
                        </html>
                        """;
    }
}
