package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.Artist;
import com.acautomaton.forum.entity.Follow;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.mapper.ArtistMapper;
import com.acautomaton.forum.mapper.FollowMapper;
import com.acautomaton.forum.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class FollowService {
    FollowMapper followMapper;
    UserMapper userMapper;
    ArtistMapper artistMapper;

    public FollowService(FollowMapper followMapper, UserMapper userMapper, ArtistMapper artistMapper) {
        this.followMapper = followMapper;
        this.userMapper = userMapper;
        this.artistMapper = artistMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public void follow(Integer followerUid, Integer beFollowedUid) {
        if (followerUid.equals(beFollowedUid)) {
            throw new ForumIllegalArgumentException("不能关注自己");
        }
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getUid, beFollowedUid);
        if (!userMapper.exists(userQueryWrapper)) {
            throw new ForumExistentialityException("目标用户不存在");
        }
        LambdaQueryWrapper<Follow> followQueryWrapper = new LambdaQueryWrapper<>();
        followQueryWrapper.eq(Follow::getFollower, followerUid);
        followQueryWrapper.eq(Follow::getBeFollowed, beFollowedUid);
        if (followMapper.exists(followQueryWrapper)) {
            return;
        }
        followMapper.insert(new Follow(null, followerUid, beFollowedUid, new Date()));
        LambdaUpdateWrapper<Artist> artistLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        artistLambdaUpdateWrapper
                .eq(Artist::getUid, beFollowedUid)
                .setIncrBy(Artist::getFans, 1);
        artistMapper.update(artistLambdaUpdateWrapper);
        artistLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        artistLambdaUpdateWrapper
                .eq(Artist::getUid, followerUid)
                .setIncrBy(Artist::getFollows, 1);
        artistMapper.update(artistLambdaUpdateWrapper);
        log.info("用户 {} 关注了 {}", followerUid, beFollowedUid);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unfollow(Integer followerUid, Integer beFollowedUid) {
        LambdaQueryWrapper<Follow> followQueryWrapper = new LambdaQueryWrapper<>();
        followQueryWrapper.eq(Follow::getFollower, followerUid);
        followQueryWrapper.eq(Follow::getBeFollowed, beFollowedUid);
        followMapper.delete(followQueryWrapper);
        LambdaUpdateWrapper<Artist> artistLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        artistLambdaUpdateWrapper
                .eq(Artist::getUid, beFollowedUid)
                .setDecrBy(Artist::getFans, 1);
        artistMapper.update(artistLambdaUpdateWrapper);
        artistLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        artistLambdaUpdateWrapper
                .eq(Artist::getUid, followerUid)
                .setDecrBy(Artist::getFollows, 1);
        artistMapper.update(artistLambdaUpdateWrapper);
        log.info("用户 {} 取消关注 {}", followerUid, beFollowedUid);
    }
}
