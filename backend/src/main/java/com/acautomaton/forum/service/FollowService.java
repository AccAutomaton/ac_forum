package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.Artist;
import com.acautomaton.forum.entity.Follow;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.PointRecordType;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.mapper.ArtistMapper;
import com.acautomaton.forum.mapper.FollowMapper;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.vo.follow.GetFansIncreamentVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class FollowService {
    FollowMapper followMapper;
    UserMapper userMapper;
    ArtistMapper artistMapper;
    PointService pointService;

    public FollowService(FollowMapper followMapper, UserMapper userMapper, ArtistMapper artistMapper, PointService pointService) {
        this.followMapper = followMapper;
        this.userMapper = userMapper;
        this.artistMapper = artistMapper;
        this.pointService = pointService;
    }

    @Transactional(rollbackFor = Exception.class)
    public void follow(Integer followerUid, Integer beFollowedUid) {
        if (followerUid.equals(beFollowedUid)) {
            throw new ForumIllegalArgumentException("不能关注自己");
        }
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getUid, beFollowedUid);
        User beFollowedUser = userMapper.selectOne(userQueryWrapper);
        if (beFollowedUser == null) {
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
        pointService.addPoint(followerUid, PointRecordType.NORMAL_INCOME, 1, "关注用户", beFollowedUser.getNickname());
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

    public Boolean hadFollowed(Integer follower, Integer beFollowed) {
        LambdaQueryWrapper<Follow> followQueryWrapper = new LambdaQueryWrapper<>();
        followQueryWrapper
                .eq(Follow::getFollower, follower)
                .eq(Follow::getBeFollowed, beFollowed);
        return followMapper.exists(followQueryWrapper);
    }

    public List<GetFansIncreamentVO> getFansIncreamentNearly7Days(Integer beFollowed) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -6);
        Timestamp sevenDaysAgo = new Timestamp(calendar.getTimeInMillis());

        QueryWrapper<Follow> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DATE(time) as date", "COUNT(*) as count")
                .eq("be_followed", beFollowed)
                .ge("time", sevenDaysAgo)
                .groupBy("DATE(time)")
                .orderByAsc("DATE(time)");
        List<Map<String, Object>> increamentList = followMapper.selectMaps(queryWrapper);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> dateRange = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dateRange.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Map<String, Integer> resultMap = new HashMap<>();
        for (Map<String, Object> map : increamentList) {
            Date date = (Date) map.get("date");
            String dateStr = sdf.format(date);
            Integer count = ((Long) map.get("count")).intValue();
            resultMap.put(dateStr, count);
        }
        List<GetFansIncreamentVO> fansIncreamentList = new ArrayList<>();
        for (Date date : dateRange) {
            String dateStr = sdf.format(date);
            Integer count = resultMap.getOrDefault(dateStr, 0);
            fansIncreamentList.add(new GetFansIncreamentVO(date, count));
        }

        return fansIncreamentList;
    }
}
