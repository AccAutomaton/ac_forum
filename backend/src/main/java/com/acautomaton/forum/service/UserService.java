package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.CosActions;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.service.util.CosService;
import com.acautomaton.forum.vo.user.GetNavigationBarInformationVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tencent.cloud.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
