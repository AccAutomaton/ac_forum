package com.acautomaton.forum.service;

import com.acautomaton.forum.dto.login.*;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.UserStatus;
import com.acautomaton.forum.enumerate.UserType;
import com.acautomaton.forum.exception.ForumException;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.exception.ForumVerifyException;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.service.util.EmailService;
import com.acautomaton.forum.service.util.JwtService;
import com.acautomaton.forum.vo.login.LoginAuthorizationVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class LoginService {
    private final PasswordEncoder passwordEncoder;
    UserMapper userMapper;
    EmailService emailService;
    CaptchaService captchaService;
    JwtService jwtService;

    @Autowired
    public LoginService(UserMapper userMapper, EmailService emailService, CaptchaService captchaService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.emailService = emailService;
        this.captchaService = captchaService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public void getEmailVerifyCodeForRegister(GetEmailVerifyCodeForRegisterDTO dto) {
        if (!captchaService.checkCaptcha(dto.getCaptchaUUID(), dto.getCaptchaCode())) {
            throw new ForumVerifyException("图形验证码错误");
        }
        if (userEmailExists(dto.getEmail())) {
            throw new ForumIllegalArgumentException("该邮箱已注册");
        }
        emailService.sendVerifycode("注册", dto.getEmail());
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        if (usernameExists(dto.getUsername())) {
            throw new ForumIllegalArgumentException("该用户名已注册");
        }
        if (userEmailExists(dto.getEmail())) {
            throw new ForumIllegalArgumentException("该邮箱已注册");
        }
        if (!emailService.judgeVerifyCode(dto.getEmail(), dto.getVerifycode())) {
            throw new ForumVerifyException("邮箱验证码错误");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Date now = new Date();
        User user = new User(null, dto.getUsername(), passwordEncoder.encode(dto.getPassword()),
                dto.getEmail(), UserStatus.NORMAL, UserType.USER, dto.getUsername(), "default-avatar.png", now, now, 0);
        userMapper.insert(user);
        if (user.getUid() != null && user.getUid() > 10000000) {
            log.info("用户 {} 注册成功", user.getUsername());
            emailService.deleteVerifyCode(dto.getEmail());
        } else {
            log.warn("用户 {} 注册失败", user.getUsername());
            throw new ForumException("注册失败，请稍后再试");
        }
    }

    public LoginAuthorizationVO login(LoginDTO dto) {
        User user = getUserByUsername(dto.getUsername());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ForumVerifyException("用户名或密码错误");
        }
        String authorization = "Bearer " + jwtService.getToken(
                Map.ofEntries(
                        Map.entry("uid", user.getUid().toString()),
                        Map.entry("username", user.getUsername())
                )
        );
        log.info("用户 {} 登陆成功", user.getUsername());
        return new LoginAuthorizationVO(authorization);
    }

    public void getEmailVerifyCodeForFindingBackPassword(GetEmailVerifyCodeForFindingBackPasswordDTO dto) {
        if (!captchaService.checkCaptcha(dto.getCaptchaUUID(), dto.getCaptchaCode())) {
            throw new ForumVerifyException("图形验证码错误");
        }
        if (!verifyUserEmail(dto.getUsername(), dto.getEmail())) {
            throw new ForumVerifyException("用户名或邮箱错误");
        }
        emailService.sendVerifycode("重置密码", dto.getEmail());
    }

    @Transactional(rollbackFor = Exception.class)
    public void findBackPassword(FindBackPasswordDTO dto) {
        if (!usernameExists(dto.getUsername())) {
            throw new ForumExistentialityException("用户名或验证码错误");
        }
        User user = getUserByUsername(dto.getUsername());
        if (!emailService.judgeVerifyCode(user.getEmail(), dto.getVerifyCode())) {
            throw new ForumExistentialityException("用户名或验证码错误");
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new ForumVerifyException("新密码不得与原密码一致");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdateTime(new Date());
        userMapper.updateById(user);
        emailService.deleteVerifyCode(user.getEmail());
        log.info("用户 {} 成功找回了密码", user.getUsername());
    }

    private User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    private boolean usernameExists(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.exists(queryWrapper);
    }

    private boolean userEmailExists(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userMapper.exists(queryWrapper);
    }

    private boolean verifyUserEmail(String username, String email) {
        User user = getUserByUsername(username);
        return user != null && user.getEmail().equals(email);
    }
}
