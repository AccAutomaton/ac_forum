package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.SecurityUser;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.CosActions;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.exception.ForumVerifyException;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.service.util.CosService;
import com.acautomaton.forum.service.util.EmailService;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.acautomaton.forum.vo.user.GetDetailsVO;
import com.acautomaton.forum.vo.user.GetNavigationBarInformationVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tencent.cloud.Credentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserService {
    EmailService emailService;
    UserMapper userMapper;
    CosService cosService;
    CaptchaService captchaService;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserMapper userMapper, CosService cosService, CaptchaService captchaService, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.cosService = cosService;
        this.captchaService = captchaService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public User getCurrentUser() {
        SecurityUser securityUser = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return securityUser.getUser();
    }

    public GetNavigationBarInformationVO getNavigationBarInformation(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        Integer expireSeconds = 60;
        String avatarKey = CosFolderPath.AVATAR + user.getAvatar();
        Credentials credentials = cosService.getCosAccessAuthorization(
                expireSeconds, CosActions.GET_OBJECT, List.of(avatarKey)
        );
        return new GetNavigationBarInformationVO(
                user, avatarKey, credentials, expireSeconds, cosService.getBucketName(), cosService.getRegion()
        );
    }

    public CosAuthorizationVO getAvatarGetAuthorization(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        queryWrapper.select("avatar");
        String avatar = userMapper.selectOne(queryWrapper).getAvatar();
        Integer expireSeconds = 60;
        String avatarKey = CosFolderPath.AVATAR + avatar;
        Credentials credentials = cosService.getCosAccessAuthorization(
                expireSeconds, CosActions.GET_OBJECT, List.of(avatarKey)
        );
        log.info("用户 {} 请求了头像访问权限", uid);
        return CosAuthorizationVO.keyAuthorization(credentials, expireSeconds, cosService.getBucketName(), cosService.getRegion(), avatarKey);
    }

    @Transactional(rollbackFor = Exception.class)
    public CosAuthorizationVO getAvatarUpdateAuthorization(Integer uid) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("updateTime", new Date());
        userMapper.update(updateWrapper);
        Integer expireSeconds = 60;
        String avatarKey = CosFolderPath.AVATAR + "uid_" + uid + "_avatar.png";
        Credentials credentials = cosService.getCosAccessAuthorization(
                expireSeconds, CosActions.PUT_OBJECT, List.of(avatarKey)
        );
        log.info("用户 {} 请求了头像修改权限", uid);
        return CosAuthorizationVO.keyAuthorization(credentials, expireSeconds, cosService.getBucketName(), cosService.getRegion(), avatarKey);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setAvatarCustomization(Integer uid) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("avatar", "uid_" + uid + "_avatar.png");
        updateWrapper.set("updateTime", new Date());
        userMapper.update(updateWrapper);
        log.info("用户 {} 不再使用默认头像", uid);
    }

    public GetDetailsVO getDetails(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        return new GetDetailsVO(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setNickname(Integer uid, String newNickname) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("nickname", newNickname);
        updateWrapper.set("update_time", new Date());
        userMapper.update(updateWrapper);
        log.info("用户 {} 修改了昵称: {}", uid, newNickname);
    }

    public void getEmailVerifyCodeForSettingEmail(String newEmail, String captchaUUID, String captchaCode) {
        if (!captchaService.checkCaptcha(captchaUUID, captchaCode)) {
            throw new ForumVerifyException("图形验证码错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", newEmail);
        if (userMapper.exists(queryWrapper)) {
            throw new ForumIllegalArgumentException("该邮箱已绑定其它账号");
        }
        emailService.sendVerifycode("更换绑定邮箱", newEmail);
        log.info("用户 {} 请求了邮箱验证码以修改邮箱: {}", getCurrentUser().getUid(), newEmail);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setEmail(Integer uid, String newEmail, String verifyCode) {
        if (!emailService.judgeVerifyCode(newEmail, verifyCode)) {
            throw new ForumVerifyException("邮箱验证码错误");
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("email", newEmail);
        updateWrapper.set("update_time", new Date());
        userMapper.update(updateWrapper);
        log.info("用户 {} 修改了邮箱: {}", uid, newEmail);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setPassword(Integer uid, String oldPassword, String newPassword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ForumVerifyException("旧密码错误");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ForumVerifyException("新密码不得与旧密码一致");
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("password", passwordEncoder.encode(newPassword));
        updateWrapper.set("update_time", new Date());
        userMapper.update(updateWrapper);
        log.info("用户 {} 修改了密码", uid);
    }
}
