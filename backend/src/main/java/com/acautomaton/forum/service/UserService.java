package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.SecurityUser;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.CosActions;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.service.util.CosService;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.acautomaton.forum.vo.user.GetDetailsVO;
import com.acautomaton.forum.vo.user.GetNavigationBarInformationVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.tencent.cloud.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UserService {
    UserMapper userMapper;
    CosService cosService;

    @Autowired
    public UserService(UserMapper userMapper, CosService cosService) {
        this.userMapper = userMapper;
        this.cosService = cosService;
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
        return CosAuthorizationVO.keyAuthorization(credentials, expireSeconds, cosService.getBucketName(), cosService.getRegion(), avatarKey);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setAvatarCustomization(Integer uid) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("uid", uid);
        updateWrapper.set("avatar", "uid_" + uid + "_avatar.png");
        updateWrapper.set("updateTime", new Date());
        userMapper.update(updateWrapper);
    }

    public GetDetailsVO getDetails(Integer uid) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        User user = userMapper.selectOne(queryWrapper);
        return new GetDetailsVO(user);
    }
}
