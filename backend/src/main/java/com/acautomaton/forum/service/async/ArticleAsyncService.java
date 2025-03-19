package com.acautomaton.forum.service.async;

import com.acautomaton.forum.entity.Article;
import com.acautomaton.forum.entity.EsArticle;
import com.acautomaton.forum.entity.Topic;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.mapper.ArticleMapper;
import com.acautomaton.forum.repository.EsArticleRepository;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class ArticleAsyncService {
    ArticleMapper articleMapper;
    EsArticleRepository esArticleRepository;

    @Autowired
    public ArticleAsyncService(ArticleMapper articleMapper, EsArticleRepository esArticleRepository) {
        this.articleMapper = articleMapper;
        this.esArticleRepository = esArticleRepository;
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void increaseVisitsById(Integer id) {
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId, id);
        lambdaUpdateWrapper.setIncrBy(Article::getVisits, 1);
        articleMapper.update(lambdaUpdateWrapper);
        log.info("[Async]文章 {} 浏览量+1", id);
        synchronizeArticleToElasticSearchByArticleId(id);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void increaseThumbsUpById(Integer id) {
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId, id);
        lambdaUpdateWrapper.setIncrBy(Article::getThumbsUp, 1);
        articleMapper.update(lambdaUpdateWrapper);
        log.info("[Async]文章 {} 点赞数+1", id);
        synchronizeArticleToElasticSearchByArticleId(id);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void decreaseThumbsUpById(Integer id) {
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId, id);
        lambdaUpdateWrapper.setDecrBy(Article::getThumbsUp, 1);
        articleMapper.update(lambdaUpdateWrapper);
        log.info("[Async]文章 {} 点赞数-1", id);
        synchronizeArticleToElasticSearchByArticleId(id);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void increaseCollectionsById(Integer id) {
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId, id);
        lambdaUpdateWrapper.setIncrBy(Article::getCollections, 1);
        articleMapper.update(lambdaUpdateWrapper);
        log.info("[Async]文章 {} 收藏数+1", id);
        synchronizeArticleToElasticSearchByArticleId(id);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void decreaseCollectionsById(Integer id) {
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId, id);
        lambdaUpdateWrapper.setDecrBy(Article::getCollections, 1);
        articleMapper.update(lambdaUpdateWrapper);
        log.info("[Async]文章 {} 收藏数-1", id);
        synchronizeArticleToElasticSearchByArticleId(id);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void increaseTippingById(Integer id, Integer volume) {
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId, id);
        lambdaUpdateWrapper.setIncrBy(Article::getTipping, volume);
        articleMapper.update(lambdaUpdateWrapper);
        log.info("[Async]文章 {} 投币数+{}", id, volume);
        synchronizeArticleToElasticSearchByArticleId(id);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public void increaseForwardsById(Integer id) {
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId, id);
        lambdaUpdateWrapper.setIncrBy(Article::getForwards, 1);
        articleMapper.update(lambdaUpdateWrapper);
        log.info("[Async]文章 {} 转发数+1", id);
        synchronizeArticleToElasticSearchByArticleId(id);
    }

    @Async
    public void synchronizeArticleDataToElasticSearchFully() {
        MPJLambdaWrapper<Article> queryWrapper = new MPJLambdaWrapper<>();
        long count = articleMapper.selectCount(queryWrapper);
        queryWrapper
                .select(Article::getId, Article::getOwner, Article::getTopic, Article::getTitle, Article::getContent, Article::getFirstImage, Article::getVisits, Article::getThumbsUp, Article::getCollections, Article::getTipping, Article::getForwards, Article::getCreateTime, Article::getUpdateTime)
                .selectAs(User::getNickname, EsArticle::getOwnerNickname)
                .selectAs(User::getAvatar, EsArticle::getOwnerAvatar)
                .selectAs(Topic::getTitle, EsArticle::getTopicTitle)
                .selectAs(Topic::getAvatar, EsArticle::getTopicAvatar)
                .innerJoin(User.class, User::getUid, Article::getOwner)
                .innerJoin(Topic.class, Topic::getId, Article::getTopic);
        List<EsArticle> articles;
        for (long i = 0; i < count; i += 100) {
            queryWrapper.last("limit " + i + ", 100");
            articles = articleMapper.selectJoinList(EsArticle.class, queryWrapper);
            esArticleRepository.saveAll(articles);
            log.info("[Async]全量同步文章数据到 ElasticSearch ({} / {})", Math.min(i + 100, count), count);
        }
    }

    @Async
    public void synchronizeArticleToElasticSearchByArticleId(Integer id) {
        MPJLambdaWrapper<Article> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .eq(Article::getId, id)
                .select(Article::getId, Article::getOwner, Article::getTopic, Article::getTitle, Article::getContent, Article::getFirstImage, Article::getVisits, Article::getThumbsUp, Article::getCollections, Article::getTipping, Article::getForwards, Article::getCreateTime, Article::getUpdateTime)
                .selectAs(User::getNickname, EsArticle::getOwnerNickname)
                .selectAs(User::getAvatar, EsArticle::getOwnerAvatar)
                .selectAs(Topic::getTitle, EsArticle::getTopicTitle)
                .selectAs(Topic::getAvatar, EsArticle::getTopicAvatar)
                .innerJoin(User.class, User::getUid, Article::getOwner)
                .innerJoin(Topic.class, Topic::getId, Article::getTopic);
        esArticleRepository.save(articleMapper.selectJoinOne(EsArticle.class, queryWrapper));
        log.info("[Async]同步文章 {} 数据到 ElasticSearch", id);
    }

    @Async
    public void synchronizeDeleteArticleToElasticSearchByTopicId(Integer id) {
        esArticleRepository.deleteById(id);
        log.info("[Async]同步删除 ElasticSearch 文章 {}", id);
    }

    @Async
    public void synchronizeArticleToElasticSearchByTopicId(Integer topicId) {
        MPJLambdaWrapper<Article> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper.eq(Article::getTopic, topicId);
        long count = articleMapper.selectCount(queryWrapper);
        queryWrapper
                .select(Article::getId, Article::getOwner, Article::getTopic, Article::getTitle, Article::getContent, Article::getFirstImage, Article::getVisits, Article::getThumbsUp, Article::getCollections, Article::getTipping, Article::getForwards, Article::getCreateTime, Article::getUpdateTime)
                .selectAs(User::getNickname, EsArticle::getOwnerNickname)
                .selectAs(User::getAvatar, EsArticle::getOwnerAvatar)
                .selectAs(Topic::getTitle, EsArticle::getTopicTitle)
                .selectAs(Topic::getAvatar, EsArticle::getTopicAvatar)
                .innerJoin(User.class, User::getUid, Article::getOwner)
                .innerJoin(Topic.class, Topic::getId, Article::getTopic);
        List<EsArticle> articles;
        for (long i = 0; i < count; i += 100) {
            queryWrapper.last("limit " + i + ", 100");
            articles = articleMapper.selectJoinList(EsArticle.class, queryWrapper);
            esArticleRepository.saveAll(articles);
            log.info("[Async]同步话题 {} 的文章数据到 ElasticSearch ({} / {})", topicId, Math.min(i + 100, count), count);
        }
    }
}
