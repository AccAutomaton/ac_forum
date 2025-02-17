package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.Article;
import com.acautomaton.forum.entity.Artist;
import com.acautomaton.forum.entity.Topic;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.BrowseRecordType;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.mapper.ArticleMapper;
import com.acautomaton.forum.mapper.ArtistMapper;
import com.acautomaton.forum.mapper.TopicMapper;
import com.acautomaton.forum.service.async.ArticleAsyncService;
import com.acautomaton.forum.service.async.BrowseRecordAsyncService;
import com.acautomaton.forum.service.async.TopicAsyncService;
import com.acautomaton.forum.service.util.RedisService;
import com.acautomaton.forum.vo.article.GetArticleVO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ArticleService {
    ArticleMapper articleMapper;
    ArtistMapper artistMapper;
    TopicMapper topicMapper;
    ArticleAsyncService articleAsyncService;
    BrowseRecordAsyncService browseRecordAsyncService;
    TopicAsyncService topicAsyncService;
    RedisService redisService;

    @Autowired
    public ArticleService(ArticleMapper articleMapper, ArtistMapper artistMapper, TopicMapper topicMapper,
                          ArticleAsyncService articleAsyncService, BrowseRecordAsyncService browseRecordAsyncService,
                          TopicAsyncService topicAsyncService, RedisService redisService) {
        this.articleMapper = articleMapper;
        this.artistMapper = artistMapper;
        this.topicMapper = topicMapper;
        this.articleAsyncService = articleAsyncService;
        this.browseRecordAsyncService = browseRecordAsyncService;
        this.topicAsyncService = topicAsyncService;
        this.redisService = redisService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer createArticle(Integer owner, Integer topic, String title, String content) {
        LambdaUpdateWrapper<Topic> topicLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        topicLambdaUpdateWrapper.eq(Topic::getId, topic);
        if (!topicMapper.exists(topicLambdaUpdateWrapper)) {
            throw new ForumExistentialityException("话题不存在");
        }
        Date now = new Date();
        Article article = new Article(null, owner, topic, title, content, 0, 0, 0, 0, 0, now, now, 0);
        articleMapper.insert(article);
        LambdaUpdateWrapper<Artist> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        articleLambdaUpdateWrapper.eq(Artist::getUid, owner);
        articleLambdaUpdateWrapper.setIncrBy(Artist::getArticles, 1);
        artistMapper.update(articleLambdaUpdateWrapper);
        topicLambdaUpdateWrapper.setIncrBy(Topic::getArticles, 1);
        topicMapper.update(topicLambdaUpdateWrapper);
        return article.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public GetArticleVO getAriticleById(Integer uid, Integer id) {
        MPJLambdaWrapper<Article> articleLambdaWrapper = new MPJLambdaWrapper<>();
        articleLambdaWrapper.eq(Article::getId, id);
        if (!articleMapper.exists(articleLambdaWrapper)) {
            throw new ForumExistentialityException("文章不存在");
        }
        articleLambdaWrapper
                .select(Article::getId, Article::getOwner, Article::getTopic, Article::getTitle, Article::getContent, Article::getVisits, Article::getThumbsUp, Article::getCollections, Article::getTipping, Article::getForwards, Article::getCreateTime, Article::getUpdateTime)
                .selectAs(User::getNickname, GetArticleVO::getOwnerNickname)
                .selectAs(User::getAvatar, GetArticleVO::getOwnerAvatar)
                .selectAs(Topic::getTitle, GetArticleVO::getTopicTitle)
                .selectAs(Topic::getAvatar, GetArticleVO::getTopicAvatar)
                .innerJoin(User.class, User::getUid, Article::getOwner)
                .innerJoin(Topic.class, Topic::getId, Article::getTopic);
        GetArticleVO vo = articleMapper.selectJoinOne(GetArticleVO.class, articleLambdaWrapper);
        vo.setOwnerAvatar(CosFolderPath.AVATAR + vo.getOwnerAvatar());
        vo.setTopicAvatar(CosFolderPath.TOPIC_AVATAR + vo.getTopicAvatar());
        if (!hasVisitedArticle(uid, id, 60)) {
            articleAsyncService.increaseVisitsById(id);
            topicAsyncService.increaseVisitsById(vo.getTopic());
        }
        browseRecordAsyncService.createBrowseRecord(uid, BrowseRecordType.ARTICLE, id);
        return vo;
    }

    private Boolean hasVisitedArticle(Integer uid, Integer articleId, Integer intervalSeconds) {
        String key = "has_visited_article_" + articleId + "_uid_" + uid + "_interval_" + intervalSeconds;
        if (redisService.hasKey(key)) {
            redisService.set(key, true, intervalSeconds);
            return true;
        } else {
            redisService.set(key, true, intervalSeconds);
            return false;
        }
    }
}
