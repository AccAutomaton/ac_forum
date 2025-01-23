package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.UserStatus;
import com.acautomaton.forum.enumerate.UserType;
import com.acautomaton.forum.exception.ForumVerifyException;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.util.JwtUtil;
import com.acautomaton.forum.vo.login.LoginAuthorizationVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    private User getUserByUid(String uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return this.getOne(queryWrapper);
    }

    private User getUserByUid(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return this.getOne(queryWrapper);
    }

    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }

    private User getUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return this.getOne(queryWrapper);
    }

    public boolean usernameExists(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.exists(queryWrapper);
    }

    public boolean userEmailExists(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return this.exists(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean register(String username, String password, String email) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Date now = new Date();
        User user = new User(null, username, passwordEncoder.encode(password), email,
                UserStatus.NORMAL, "", UserType.USER, "", now, now, 0);
        addUser(user);
        if (user.getUid() != null && user.getUid() > 10000000) {
            log.info("用户 {} 注册成功", user.getUsername());
            return true;
        } else {
            log.warn("用户 {} 注册失败", user.getUsername());
            return false;
        }
    }

    public LoginAuthorizationVO login(String username, String password) {
        User user = getUserByUsername(username);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new ForumVerifyException("用户名或密码错误");
        }
        String authorization = "Bearer " + JwtUtil.getToken(
                Map.ofEntries(
                        Map.entry("uid", user.getUid().toString()),
                        Map.entry("username", user.getUsername())
                )
        );
        log.info("用户 {} 登陆成功", user.getUsername());
        return new LoginAuthorizationVO(authorization);
    }

    public boolean verifyUserEmail(String username, String email) {
        User user = getUserByUsername(username);
        return user != null && user.getEmail().equals(email);
    }

    private void addUser(User user) {
        this.save(user);
    }

    private void updateUser(User user) {
        this.updateById(user);
    }
}
