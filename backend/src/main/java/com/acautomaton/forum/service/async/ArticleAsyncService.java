package com.acautomaton.forum.service.async;

import com.acautomaton.forum.entity.Article;
import com.acautomaton.forum.mapper.ArticleMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ArticleAsyncService {
    ArticleMapper articleMapper;

    @Autowired
    public ArticleAsyncService(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Async
    public void increaseVisitsById(Integer id) {
        LambdaUpdateWrapper<Article> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Article::getId, id);
        lambdaUpdateWrapper.setIncrBy(Article::getVisits, 1);
        articleMapper.update(lambdaUpdateWrapper);
        log.info("[Async]文章 {} 浏览量+1", id);
    }
}
