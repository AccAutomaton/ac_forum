package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.*;
import com.acautomaton.forum.enumerate.ThumbsUpType;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.vo.artist.GetArtistStatisticDataVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ArtistService {
    UserMapper userMapper;

    @Autowired
    public ArtistService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public GetArtistStatisticDataVO getStatisticData(Integer uid) {
        MPJLambdaWrapper<User> userLambdaWrapper = new MPJLambdaWrapper<>();
        userLambdaWrapper
                .eq(User::getUid, uid)
                .selectAs(User::getUid, GetArtistStatisticDataVO::getUid)
                .selectAs(User::getNickname, GetArtistStatisticDataVO::getNickname)
                .selectAs(User::getAvatar, GetArtistStatisticDataVO::getAvatar)
                .selectAs(User::getPoints, GetArtistStatisticDataVO::getPoints)
                .selectAs(User::getCreateTime, GetArtistStatisticDataVO::getCreateTime)
                .selectAs(Artist::getArticles, GetArtistStatisticDataVO::getArticles)
                .selectAs(Artist::getCollections, GetArtistStatisticDataVO::getCollections)
                .selectAs(Artist::getFollows, GetArtistStatisticDataVO::getFollows)
                .selectAs(Artist::getFans, GetArtistStatisticDataVO::getFans)
                .selectSub(Topic.class, i -> i
                                .selectCount(Topic::getId).eq(Topic::getAdministrator, uid),
                        GetArtistStatisticDataVO::getTopics)
                .selectSub(ThumbsUp.class, i -> i
                                .selectCount(ThumbsUp::getId).eq(ThumbsUp::getThumbsUper, uid).eq(ThumbsUp::getType, ThumbsUpType.ARTICLE),
                        GetArtistStatisticDataVO::getThumbsUp)
                .innerJoin(Artist.class, Artist::getUid, User::getUid);
        GetArtistStatisticDataVO vo = userMapper.selectJoinOne(GetArtistStatisticDataVO.class, userLambdaWrapper);
        if (vo == null) {
            throw new ForumExistentialityException("用户不存在");
        }
        return vo;
    }

    public PageHelperVO<GetArtistStatisticDataVO> getFollows(Integer uid, Integer pageNumber, Integer pageSize) {
        return new PageHelperVO<>(
                PageHelper.startPage(pageNumber, pageSize > 20 ? 20 : pageSize).doSelectPageInfo(() -> {
                    MPJLambdaWrapper<User> userLambdaWrapper = new MPJLambdaWrapper<>();
                    userLambdaWrapper
                            .selectAs(User::getUid, GetArtistStatisticDataVO::getUid)
                            .selectAs(User::getNickname, GetArtistStatisticDataVO::getNickname)
                            .selectAs(User::getAvatar, GetArtistStatisticDataVO::getAvatar)
                            .selectAs(Artist::getFollows, GetArtistStatisticDataVO::getFollows)
                            .selectAs(Artist::getFans, GetArtistStatisticDataVO::getFans)
                            .eq(Follow::getFollower, uid)
                            .innerJoin(Artist.class, Artist::getUid, User::getUid)
                            .innerJoin(Follow.class, Follow::getBeFollowed, User::getUid);
                    userMapper.selectJoinList(GetArtistStatisticDataVO.class, userLambdaWrapper);
                })
        );
    }

    public PageHelperVO<GetArtistStatisticDataVO> getFans(Integer uid, Integer pageNumber, Integer pageSize) {
        return new PageHelperVO<>(
                PageHelper.startPage(pageNumber, pageSize > 20 ? 20 : pageSize).doSelectPageInfo(() -> {
                    MPJLambdaWrapper<User> userLambdaWrapper = new MPJLambdaWrapper<>();
                    userLambdaWrapper
                            .selectAs(User::getUid, GetArtistStatisticDataVO::getUid)
                            .selectAs(User::getNickname, GetArtistStatisticDataVO::getNickname)
                            .selectAs(User::getAvatar, GetArtistStatisticDataVO::getAvatar)
                            .selectAs(Artist::getFollows, GetArtistStatisticDataVO::getFollows)
                            .selectAs(Artist::getFans, GetArtistStatisticDataVO::getFans)
                            .eq(Follow::getBeFollowed, uid)
                            .innerJoin(Artist.class, Artist::getUid, User::getUid)
                            .innerJoin(Follow.class, Follow::getFollower, User::getUid);
                    userMapper.selectJoinList(GetArtistStatisticDataVO.class, userLambdaWrapper);
                })
        );
    }
}
