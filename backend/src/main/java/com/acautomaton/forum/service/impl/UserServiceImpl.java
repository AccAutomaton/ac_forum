package com.acautomaton.forum.service.impl;

import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.service.inte.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public User getUserByUid(String uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return this.getOne(queryWrapper);
    }

    @Override
    public User getUserByUid(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        return this.getOne(queryWrapper);
    }

    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }

    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return this.getOne(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User registerUser(User user) {
        this.save(user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) {
        this.saveOrUpdate(user);
    }
}
