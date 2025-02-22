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
    public void increaseVisitsById(Integer id) {
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId, id);
        lambdaUpdateWrapper.setIncrBy(Article::getVisits, 1);
        articleMapper.update(lambdaUpdateWrapper);
        synchronizeArticleToElasticSearchById(id);
        log.info("[Async]文章 {} 浏览量+1", id);
    }

    @Async
    public void synchronizeDataToElasticSearchFully() {
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
            log.info("全量同步文章数据到 Elastic Search ({} / {})", Math.min(i + 100, count), count);
        }
    }

    @Async
    public void synchronizeArticleToElasticSearchById(Integer id) {
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
    }
}
