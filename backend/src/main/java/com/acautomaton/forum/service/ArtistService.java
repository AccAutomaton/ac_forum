package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.*;
import com.acautomaton.forum.enumerate.CoinRecordType;
import com.acautomaton.forum.enumerate.ThumbsUpType;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.mapper.ArtistMapper;
import com.acautomaton.forum.mapper.CoinRecordMapper;
import com.acautomaton.forum.mapper.TopicMapper;
import com.acautomaton.forum.mapper.UserMapper;
import com.acautomaton.forum.vo.artist.GetArtistStatisticDataVO;
import com.acautomaton.forum.vo.artist.GetCreationDashboardDataVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ArtistService {
    UserMapper userMapper;
    TopicMapper topicMapper;
    ArtistMapper artistMapper;
    CoinRecordMapper coinRecordMapper;

    @Autowired
    public ArtistService(UserMapper userMapper, TopicMapper topicMapper, ArtistMapper artistMapper, CoinRecordMapper coinRecordMapper) {
        this.userMapper = userMapper;
        this.topicMapper = topicMapper;
        this.artistMapper = artistMapper;
        this.coinRecordMapper = coinRecordMapper;
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
                            .orderByDesc(Follow::getTime)
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
                            .orderByDesc(Follow::getTime)
                            .innerJoin(Artist.class, Artist::getUid, User::getUid)
                            .innerJoin(Follow.class, Follow::getFollower, User::getUid);
                    userMapper.selectJoinList(GetArtistStatisticDataVO.class, userLambdaWrapper);
                })
        );
    }

    public GetCreationDashboardDataVO getCreationDashboardData(Integer uid) {
        CompletableFuture<Integer> topicsCompletableFuture = CompletableFuture.supplyAsync(() -> {
            LambdaQueryWrapper<Topic> topicLambdaWrapper = new LambdaQueryWrapper<>();
            topicLambdaWrapper.eq(Topic::getAdministrator, uid);
            return topicMapper.selectCount(topicLambdaWrapper).intValue();
        }, command -> Thread.ofVirtual().name("virtual-thread-topic-jdbc-", 1).start(command));
        CompletableFuture<Artist> artistCompletableFuture = CompletableFuture.supplyAsync(() -> {
            LambdaQueryWrapper<Artist> artistLambdaWrapper = new LambdaQueryWrapper<>();
            artistLambdaWrapper
                    .select(Artist::getArticles, Artist::getFans)
                    .eq(Artist::getUid, uid);
            return artistMapper.selectOne(artistLambdaWrapper);
        }, command -> Thread.ofVirtual().name("virtual-thread-artist-jdbc-", 1).start(command));
        CompletableFuture<Integer> tippingsCompletableFuture = CompletableFuture.supplyAsync(() -> {
            QueryWrapper<CoinRecord> coinRecordQueryWrapper = new QueryWrapper<>();
            coinRecordQueryWrapper
                    .select("SUM(coin_volume) AS tippings")
                    .eq("uid", uid)
                    .eq("type", CoinRecordType.COIN_INCOME.getIndex());
            if (coinRecordMapper.selectMaps(coinRecordQueryWrapper).getFirst() == null) {
                return 0;
            }
            return ((BigDecimal) coinRecordMapper.selectMaps(coinRecordQueryWrapper).getFirst().get("tippings")).intValue();
        }, command -> Thread.ofVirtual().name("virtual-thread-coin-record-jdbc-", 1).start(command));

        Integer topics = topicsCompletableFuture.join();
        Artist artist = artistCompletableFuture.join();
        Integer tippings = tippingsCompletableFuture.join();
        return new GetCreationDashboardDataVO(topics, artist.getArticles(), artist.getFans(), tippings);
    }
}
