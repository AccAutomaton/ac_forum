package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.*;
import com.acautomaton.forum.enumerate.ArticleQueryType;
import com.acautomaton.forum.enumerate.BrowseRecordType;
import com.acautomaton.forum.enumerate.CosActions;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalAccountException;
import com.acautomaton.forum.mapper.ArticleMapper;
import com.acautomaton.forum.mapper.ArtistMapper;
import com.acautomaton.forum.mapper.TopicMapper;
import com.acautomaton.forum.repository.EsArticleRepository;
import com.acautomaton.forum.repository.EsTopicRepository;
import com.acautomaton.forum.service.async.ArticleAsyncService;
import com.acautomaton.forum.service.async.BrowseRecordAsyncService;
import com.acautomaton.forum.service.async.TopicAsyncService;
import com.acautomaton.forum.service.util.CosService;
import com.acautomaton.forum.service.util.RedisService;
import com.acautomaton.forum.entity.EsArticle;
import com.acautomaton.forum.vo.article.GetEsArticalListVO;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.tencent.cloud.Credentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ArticleService {
    ArticleMapper articleMapper;
    ArtistMapper artistMapper;
    TopicMapper topicMapper;
    EsArticleRepository esArticleRepository;
    EsTopicRepository esTopicRepository;
    ArticleAsyncService articleAsyncService;
    BrowseRecordAsyncService browseRecordAsyncService;
    TopicAsyncService topicAsyncService;
    RedisService redisService;
    CosService cosService;

    @Autowired
    public ArticleService(ArticleMapper articleMapper, ArtistMapper artistMapper, TopicMapper topicMapper,
                          ArticleAsyncService articleAsyncService, BrowseRecordAsyncService browseRecordAsyncService,
                          TopicAsyncService topicAsyncService, RedisService redisService, EsArticleRepository esArticleRepository,
                          CosService cosService, EsTopicRepository esTopicRepository) {
        this.articleMapper = articleMapper;
        this.artistMapper = artistMapper;
        this.topicMapper = topicMapper;
        this.articleAsyncService = articleAsyncService;
        this.browseRecordAsyncService = browseRecordAsyncService;
        this.topicAsyncService = topicAsyncService;
        this.redisService = redisService;
        this.esArticleRepository = esArticleRepository;
        this.cosService = cosService;
        this.esTopicRepository = esTopicRepository;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer createArticle(Integer owner, Integer topic, String title, String content) {
        LambdaUpdateWrapper<Topic> topicLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        topicLambdaUpdateWrapper.eq(Topic::getId, topic);
        if (!topicMapper.exists(topicLambdaUpdateWrapper)) {
            throw new ForumExistentialityException("话题不存在");
        }
        Date now = new Date();
        String firstImage = getFirstImageUrlOfMarkdown(content);
        Article article = new Article(null, owner, topic, title, content, firstImage, 0, 0, 0, 0, 0, now, now, 0);
        articleMapper.insert(article);
        LambdaUpdateWrapper<Artist> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        articleLambdaUpdateWrapper.eq(Artist::getUid, owner);
        articleLambdaUpdateWrapper.setIncrBy(Artist::getArticles, 1);
        artistMapper.update(articleLambdaUpdateWrapper);
        topicLambdaUpdateWrapper.setIncrBy(Topic::getArticles, 1);
        topicMapper.update(topicLambdaUpdateWrapper);
        esTopicRepository.save(new EsTopic(topicMapper.selectById(article.getTopic())));
        MPJLambdaWrapper<Article> queryWrapper = new MPJLambdaWrapper<>();
        queryWrapper
                .eq(Article::getId, article.getId())
                .select(Article::getId, Article::getOwner, Article::getTopic, Article::getTitle, Article::getContent, Article::getFirstImage, Article::getVisits, Article::getThumbsUp, Article::getCollections, Article::getTipping, Article::getForwards, Article::getCreateTime, Article::getUpdateTime)
                .selectAs(User::getNickname, EsArticle::getOwnerNickname)
                .selectAs(User::getAvatar, EsArticle::getOwnerAvatar)
                .selectAs(Topic::getTitle, EsArticle::getTopicTitle)
                .selectAs(Topic::getAvatar, EsArticle::getTopicAvatar)
                .innerJoin(User.class, User::getUid, Article::getOwner)
                .innerJoin(Topic.class, Topic::getId, Article::getTopic);
        esArticleRepository.save(articleMapper.selectJoinOne(EsArticle.class, queryWrapper));
        log.info("用户 {} 创建了文章 {}", owner, article.getId());
        return article.getId();
    }

    public EsArticle getAriticleById(Integer uid, Integer id) {
        MPJLambdaWrapper<Article> articleLambdaWrapper = new MPJLambdaWrapper<>();
        articleLambdaWrapper.eq(Article::getId, id);
        if (!articleMapper.exists(articleLambdaWrapper)) {
            throw new ForumExistentialityException("文章不存在");
        }
        articleLambdaWrapper
                .select(Article::getId, Article::getOwner, Article::getTopic, Article::getTitle, Article::getContent, Article::getFirstImage, Article::getVisits, Article::getThumbsUp, Article::getCollections, Article::getTipping, Article::getForwards, Article::getCreateTime, Article::getUpdateTime)
                .selectAs(User::getNickname, EsArticle::getOwnerNickname)
                .selectAs(User::getAvatar, EsArticle::getOwnerAvatar)
                .selectAs(Topic::getTitle, EsArticle::getTopicTitle)
                .selectAs(Topic::getAvatar, EsArticle::getTopicAvatar)
                .innerJoin(User.class, User::getUid, Article::getOwner)
                .innerJoin(Topic.class, Topic::getId, Article::getTopic);
        EsArticle vo = articleMapper.selectJoinOne(EsArticle.class, articleLambdaWrapper);
        vo.setOwnerAvatar(CosFolderPath.AVATAR + vo.getOwnerAvatar());
        vo.setTopicAvatar(CosFolderPath.TOPIC_AVATAR + vo.getTopicAvatar());
        if (!hasVisitedArticle(uid, id, 60)) {
            articleAsyncService.increaseVisitsById(id);
            topicAsyncService.increaseVisitsById(vo.getTopic());
        }
        browseRecordAsyncService.createBrowseRecord(uid, BrowseRecordType.ARTICLE, id);
        return vo;
    }

    public GetEsArticalListVO getEsArticleList(ArticleQueryType queryType, String keyword, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize > 20 ? 20 : pageSize, queryType.getSort());
        Page<EsArticle> esArticles;
        if (keyword.isBlank()) {
            esArticles = esArticleRepository.findAll(pageable);
        } else {
            esArticles = esArticleRepository.findByTitleOrContentOrOwnerNickname(keyword, keyword, keyword, pageable);
        }
        return new GetEsArticalListVO(new PageHelperVO<>(limitLengthOfContent(esArticles)), CosFolderPath.AVATAR.getPath(), CosFolderPath.ARTICLE_IMAGE.getPath(), CosFolderPath.TOPIC_AVATAR.getPath());
    }

    public GetEsArticalListVO getEsArticleListByTopicId(Integer topicId, ArticleQueryType queryType, String keyword, Integer pageNumber, Integer pageSize) {
        LambdaQueryWrapper<Topic> topicLambdaQueryWrapper = new LambdaQueryWrapper<>();
        topicLambdaQueryWrapper.eq(Topic::getId, topicId);
        if (!topicMapper.exists(topicLambdaQueryWrapper)) {
            throw new ForumExistentialityException("话题不存在");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize > 20 ? 20 : pageSize, queryType.getSort());
        Page<EsArticle> esArticles;
        if (keyword.isBlank()) {
            esArticles = esArticleRepository.findByTopic(topicId, pageable);
        } else {
            esArticles = esArticleRepository.findByTopicAndTitleOrContentOrOwnerNickname(topicId, keyword, keyword, keyword, pageable);
        }
        return new GetEsArticalListVO(new PageHelperVO<>(limitLengthOfContent(esArticles)), CosFolderPath.AVATAR.getPath(), CosFolderPath.ARTICLE_IMAGE.getPath(), CosFolderPath.TOPIC_AVATAR.getPath());
    }

    public void deleteArticleById(Integer uid, Integer topicId) {
        LambdaUpdateWrapper<Article> topicLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        topicLambdaUpdateWrapper.eq(Article::getId, topicId);
        Article article = articleMapper.selectOne(topicLambdaUpdateWrapper);
        if (article == null) {
            throw new ForumExistentialityException("文章不存在");
        }
        if (!article.getOwner().equals(uid)) {
            throw new ForumIllegalAccountException("没有权限进行此操作");
        }
        articleMapper.delete(topicLambdaUpdateWrapper);
        log.info("用户 {} 删除了文章 {}", uid, topicId);
        articleAsyncService.synchronizeDeleteArticleToElasticSearchByTopicId(topicId);
        topicAsyncService.synchronizeTopicToElasticSearchByArticleId(article.getId());
    }

    public void updateArticleById(Integer uid, Integer topicId, String title, String content) {
        LambdaUpdateWrapper<Article> topicLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        topicLambdaUpdateWrapper.eq(Article::getId, topicId);
        Article article = articleMapper.selectOne(topicLambdaUpdateWrapper);
        if (article == null) {
            throw new ForumExistentialityException("文章不存在");
        }
        if (!article.getOwner().equals(uid)) {
            throw new ForumIllegalAccountException("没有权限进行此操作");
        }
        topicLambdaUpdateWrapper.set(Article::getTitle, title);
        topicLambdaUpdateWrapper.set(Article::getContent, content);
        topicLambdaUpdateWrapper.set(Article::getUpdateTime, new Date());
        articleMapper.update(topicLambdaUpdateWrapper);
        log.info("用户 {} 修改了文章 {}", uid, topicId);
        articleAsyncService.synchronizeArticleToElasticSearchByArticleId(topicId);
        topicAsyncService.synchronizeTopicToElasticSearchByArticleId(article.getId());
    }

    public CosAuthorizationVO getArticleImageUpdateAuthorization(Integer uid) {
        Integer expireSeconds = 60;
        String imagePrefix;
        do {
            imagePrefix = CosFolderPath.ARTICLE_IMAGE.getPath() + UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() + ".png";
        } while (cosService.objectExists(imagePrefix));
        Credentials credentials = cosService.getCosAccessAuthorization(
                expireSeconds, CosActions.PUT_OBJECT, List.of(imagePrefix)
        );
        log.info("用户 {} 请求了图片 {} 上传权限", uid, imagePrefix);
        return CosAuthorizationVO.keyAuthorization(credentials, expireSeconds, cosService.getBucketName(), cosService.getRegion(), imagePrefix);
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

    private static String getFirstImageUrlOfMarkdown(String markdown) {
        String regex = "!\\[.*?]\\((.*?)\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(markdown);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    private static Page<EsArticle> limitLengthOfContent(Page<EsArticle> esArticles) {
        String regex = "!\\[.*?]\\((.*?)\\)";
        Pattern pattern = Pattern.compile(regex);
        for (int i = 0; i < esArticles.getContent().size(); i++) {
            int length = esArticles.getContent().get(i).getContent().length();
            esArticles.getContent().get(i).setContent(pattern.matcher(esArticles.getContent().get(i).getContent()).replaceAll("[图片]"));
            if (length > 200) {
                esArticles.getContent().get(i).setContent(esArticles.getContent().get(i).getContent().substring(0, 200));
            }
            esArticles.getContent().get(i).setContent(esArticles.getContent().get(i).getContent() + " ...");
        }
        return esArticles;
    }
}
