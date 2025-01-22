package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.UserStatus;
import com.acautomaton.forum.enumerate.UserType;
import com.acautomaton.forum.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    public User getUserByUid(String uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return this.getOne(queryWrapper);
    }

    public User getUserByUid(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return this.getOne(queryWrapper);
    }

    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }

    public User getUserByEmail(String email) {
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
        return user.getUid() != null && user.getUid() > 10000000;
    }

    private void addUser(User user) {
        this.save(user);
    }

    private void updateUser(User user) {
        this.updateById(user);
    }
}
