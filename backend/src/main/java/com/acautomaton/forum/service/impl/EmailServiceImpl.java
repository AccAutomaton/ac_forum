package com.acautomaton.forum.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
public class EmailServiceImpl {
    @Value("${spring.profiles.active}")
    private String env;
    JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerifycode(String project, String receiveAddress, String verifyCode) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
        helper.setFrom("ac_forum@mail.acautomaton.com", "AC论坛");
        helper.setTo(receiveAddress);
        helper.setSubject("AC论坛 - 验证码");
        helper.setText(getEmailText(project, verifyCode), true);

        if ("dev".equals(env)) {
            log.debug("-- dev mode -- 成功向 {} 发送 {} 验证码 {}", receiveAddress, project, verifyCode);
        }
        else {
            javaMailSender.send(mimeMessage);
            log.info("成功向 {} 发送 {} 验证码", receiveAddress, project);
        }
    }

    private String getEmailText(String project, String verifyCode) {
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
