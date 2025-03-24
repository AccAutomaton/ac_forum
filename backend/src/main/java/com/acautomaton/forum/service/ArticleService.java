package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.*;
import com.acautomaton.forum.enumerate.*;
import com.acautomaton.forum.exception.ForumException;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalAccountException;
import com.acautomaton.forum.exception.ForumIllegalArgumentException;
import com.acautomaton.forum.mapper.*;
import com.acautomaton.forum.repository.EsArticleRepository;
import com.acautomaton.forum.repository.EsTopicRepository;
import com.acautomaton.forum.service.async.ArticleAsyncService;
import com.acautomaton.forum.service.async.BrowseRecordAsyncService;
import com.acautomaton.forum.service.async.TopicAsyncService;
import com.acautomaton.forum.service.util.CosService;
import com.acautomaton.forum.service.util.RedisService;
import com.acautomaton.forum.entity.EsArticle;
import com.acautomaton.forum.vo.article.GetArticleVO;
import com.acautomaton.forum.vo.article.GetCommentListVO;
import com.acautomaton.forum.vo.article.GetEsArticalListVO;
import com.acautomaton.forum.vo.comment.GetCommentVO;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
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
    FollowMapper followMapper;
    CommentMapper commentMapper;
    ThumbsUpMapper thumbsUpMapper;
    CollectionMapper collectionMapper;
    UserMapper userMapper;
    CoinRecordMapper coinRecordMapper;
    PointRecordMapper pointRecordMapper;
    EsArticleRepository esArticleRepository;
    EsTopicRepository esTopicRepository;
    ArticleAsyncService articleAsyncService;
    BrowseRecordAsyncService browseRecordAsyncService;
    TopicAsyncService topicAsyncService;
    RedisService redisService;
    CosService cosService;
    MessageService messageService;

    @Autowired
    public ArticleService(ArticleMapper articleMapper, ArtistMapper artistMapper, TopicMapper topicMapper,
                          ArticleAsyncService articleAsyncService, BrowseRecordAsyncService browseRecordAsyncService,
                          TopicAsyncService topicAsyncService, RedisService redisService, EsArticleRepository esArticleRepository,
                          CosService cosService, EsTopicRepository esTopicRepository, FollowMapper followMapper,
                          CommentMapper commentMapper, ThumbsUpMapper thumbsUpMapper, CollectionMapper collectionMapper,
                          UserMapper userMapper, CoinRecordMapper coinRecordMapper, MessageService messageService,
                          PointRecordMapper pointRecordMapper) {
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
        this.followMapper = followMapper;
        this.commentMapper = commentMapper;
        this.thumbsUpMapper = thumbsUpMapper;
        this.collectionMapper = collectionMapper;
        this.userMapper = userMapper;
        this.coinRecordMapper = coinRecordMapper;
        this.messageService = messageService;
        this.pointRecordMapper = pointRecordMapper;
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

    @Transactional(rollbackFor = Exception.class)
    public GetArticleVO getAriticleById(Integer uid, Integer id) {
        MPJLambdaWrapper<Article> articleLambdaWrapper = new MPJLambdaWrapper<>();
        articleLambdaWrapper.eq(Article::getId, id);
        if (!articleMapper.exists(articleLambdaWrapper)) {
            throw new ForumExistentialityException("文章不存在");
        }
        articleLambdaWrapper
                .select(Article::getId, Article::getOwner, Article::getTopic, Article::getTitle, Article::getContent, Article::getFirstImage, Article::getVisits, Article::getThumbsUp, Article::getCollections, Article::getTipping, Article::getForwards, Article::getCreateTime, Article::getUpdateTime)
                .selectAs(User::getNickname, GetArticleVO::getOwnerNickname)
                .selectAs(User::getAvatar, GetArticleVO::getOwnerAvatar)
                .selectAs(Topic::getTitle, GetArticleVO::getTopicTitle)
                .selectAs(Topic::getAvatar, GetArticleVO::getTopicAvatar)
                .selectAs(Topic::getArticles, GetArticleVO::getTopicArticles)
                .selectAs(Topic::getVisits, GetArticleVO::getTopicVisits)
                .selectAs(Artist::getFans, GetArticleVO::getOwnerFans)
                .selectAs(Artist::getArticles, GetArticleVO::getOwnerArticles)
                .innerJoin(User.class, User::getUid, Article::getOwner)
                .innerJoin(Topic.class, Topic::getId, Article::getTopic)
                .innerJoin(Artist.class, Artist::getUid, Article::getOwner);
        GetArticleVO vo = articleMapper.selectJoinOne(GetArticleVO.class, articleLambdaWrapper);

        LambdaQueryWrapper<Follow> followQueryWrapper = new LambdaQueryWrapper<>();
        followQueryWrapper
                .eq(Follow::getFollower, uid)
                .eq(Follow::getBeFollowed, vo.getOwner());
        vo.setAlreadyFollowOwner(followMapper.exists(followQueryWrapper));

        LambdaQueryWrapper<ThumbsUp> thumbsUpLambdaQueryWrapper = new LambdaQueryWrapper<>();
        thumbsUpLambdaQueryWrapper
                .eq(ThumbsUp::getThumbsUper, uid)
                .eq(ThumbsUp::getType, ThumbsUpType.ARTICLE)
                .eq(ThumbsUp::getBeThumbsUpedId, id);
        vo.setAlreadyThumbsUp(thumbsUpMapper.exists(thumbsUpLambdaQueryWrapper));

        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper
                .eq(Collection::getCollector, uid)
                .eq(Collection::getType, CollectionType.ARTICLE)
                .eq(Collection::getBeCollectedId, id);
        vo.setAlreadyCollected(collectionMapper.exists(collectionLambdaQueryWrapper));

        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getTargetArticle, vo.getId());
        vo.setComments(Math.toIntExact(commentMapper.selectCount(commentLambdaQueryWrapper)));

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

    @Transactional(rollbackFor = Exception.class)
    public void deleteArticleById(Integer uid, Integer topicId) {
        LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        articleLambdaUpdateWrapper.eq(Article::getId, topicId);
        Article article = articleMapper.selectOne(articleLambdaUpdateWrapper);
        if (article == null) {
            throw new ForumExistentialityException("文章不存在");
        }
        if (!article.getOwner().equals(uid)) {
            throw new ForumIllegalAccountException("没有权限进行此操作");
        }
        articleMapper.delete(articleLambdaUpdateWrapper);
        log.info("用户 {} 删除了文章 {}", uid, topicId);
        articleAsyncService.synchronizeDeleteArticleToElasticSearchByTopicId(topicId);
        topicAsyncService.synchronizeTopicToElasticSearchByArticleId(article.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateArticleById(Integer uid, Integer articleId, String title, String content) {
        LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        articleLambdaUpdateWrapper.eq(Article::getId, articleId);
        Article article = articleMapper.selectOne(articleLambdaUpdateWrapper);
        if (article == null) {
            throw new ForumExistentialityException("文章不存在");
        }
        if (!article.getOwner().equals(uid)) {
            throw new ForumIllegalAccountException("没有权限进行此操作");
        }
        articleLambdaUpdateWrapper.set(Article::getTitle, title);
        articleLambdaUpdateWrapper.set(Article::getContent, content);
        articleLambdaUpdateWrapper.set(Article::getUpdateTime, new Date());
        articleMapper.update(articleLambdaUpdateWrapper);
        log.info("用户 {} 修改了文章 {}", uid, articleId);
        articleAsyncService.synchronizeArticleToElasticSearchByArticleId(articleId);
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

    @Transactional(rollbackFor = Exception.class)
    public void thumbsUp(User user, Integer articleId) {
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLambdaQueryWrapper.eq(Article::getId, articleId);
        Article article = articleMapper.selectOne(articleLambdaQueryWrapper);
        if (article == null) {
            throw new ForumExistentialityException("目标文章不存在");
        }
        LambdaQueryWrapper<ThumbsUp> thumbsUpLambdaQueryWrapper = new LambdaQueryWrapper<>();
        thumbsUpLambdaQueryWrapper
                .eq(ThumbsUp::getThumbsUper, user.getUid())
                .eq(ThumbsUp::getType, ThumbsUpType.ARTICLE)
                .eq(ThumbsUp::getBeThumbsUpedId, articleId);
        if (thumbsUpMapper.exists(thumbsUpLambdaQueryWrapper)) {
            return;
        }
        thumbsUpMapper.insert(new ThumbsUp(null, user.getUid(), ThumbsUpType.ARTICLE, articleId, new Date()));
        log.info("用户 {} 点赞了文章 {}", user.getUid(), articleId);
        articleAsyncService.increaseThumbsUpById(articleId);
        messageService.createMessage(article.getOwner(), "您的文章收到来自用户 " + user.getNickname() + " 的点赞", MessageType.NORMAL,
                "文章: " + article.getTitle(), "/article/" + articleId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void unThumbsUp(Integer uid, Integer articleId) {
        LambdaQueryWrapper<ThumbsUp> thumbsUpLambdaQueryWrapper = new LambdaQueryWrapper<>();
        thumbsUpLambdaQueryWrapper
                .eq(ThumbsUp::getThumbsUper, uid)
                .eq(ThumbsUp::getType, ThumbsUpType.ARTICLE)
                .eq(ThumbsUp::getBeThumbsUpedId, articleId);
        if (thumbsUpMapper.exists(thumbsUpLambdaQueryWrapper)) {
            thumbsUpMapper.delete(thumbsUpLambdaQueryWrapper);
            log.info("用户 {} 取消点赞文章 {}", uid, articleId);
            articleAsyncService.decreaseThumbsUpById(articleId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void collect(Integer uid, Integer articleId) {
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLambdaQueryWrapper.eq(Article::getId, articleId);
        if (!articleMapper.exists(articleLambdaQueryWrapper)) {
            throw new ForumExistentialityException("目标文章不存在");
        }
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper
                .eq(Collection::getCollector, uid)
                .eq(Collection::getType, CollectionType.ARTICLE)
                .eq(Collection::getBeCollectedId, articleId);
        if (collectionMapper.exists(collectionLambdaQueryWrapper)) {
            return;
        }
        collectionMapper.insert(new Collection(null, uid, CollectionType.ARTICLE, articleId, new Date()));
        LambdaUpdateWrapper<Artist> artistLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        artistLambdaUpdateWrapper
                .eq(Artist::getUid, uid)
                .setIncrBy(Artist::getCollections, 1);
        artistMapper.update(artistLambdaUpdateWrapper);
        log.info("用户 {} 收藏了文章 {}", uid, articleId);
        articleAsyncService.increaseCollectionsById(articleId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void uncollect(Integer uid, Integer articleId) {
        LambdaQueryWrapper<Collection> collectionLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectionLambdaQueryWrapper
                .eq(Collection::getCollector, uid)
                .eq(Collection::getType, CollectionType.ARTICLE)
                .eq(Collection::getBeCollectedId, articleId);
        if (collectionMapper.exists(collectionLambdaQueryWrapper)) {
            collectionMapper.delete(collectionLambdaQueryWrapper);
            LambdaUpdateWrapper<Artist> artistLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            artistLambdaUpdateWrapper
                    .eq(Artist::getUid, uid)
                    .setDecrBy(Artist::getCollections, 1);
            artistMapper.update(artistLambdaUpdateWrapper);
            log.info("用户 {} 取消收藏文章 {}", uid, articleId);
            articleAsyncService.decreaseCollectionsById(articleId);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void tipping(User payUser, Integer articleId, Integer volume) {
        if (payUser.getCoins() < volume) {
            throw new ForumIllegalArgumentException("硬币余额不足");
        }
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLambdaQueryWrapper.eq(Article::getId, articleId);
        Article article = articleMapper.selectOne(articleLambdaQueryWrapper);
        if (article == null) {
            throw new ForumExistentialityException("目标文章不存在");
        }
        if (payUser.getUid().equals(article.getOwner())) {
            throw new ForumException("不能给自己的文章投币");
        }
        Date now = new Date();

        // Step 1: deduct payUser's coin and add payUser's point
        payUser.setCoins(payUser.getCoins() - volume);
        payUser.setPoints(payUser.getPoints() + volume);
        userMapper.updateById(payUser);
        // Step 2: add payUser's coin record
        coinRecordMapper.insert(new CoinRecord(null, payUser.getUid(), CoinRecordType.COIN_PAYOUT, volume,
                payUser.getCoins(), "文章投币", "投币给文章 " + article.getTitle(),
                CoinRecordStatus.SUCCESS, now, now, 0));
        // Step 3: add payUser's point record
        pointRecordMapper.insert(new PointRecord(null, payUser.getUid(), PointRecordType.COIN_INCOME, volume,
                payUser.getPoints(), "文章投币", "投币给文章 " + article.getTitle(),
                PointRecordStatus.SUCCESS, now, now, 0));
        // Step 4: add targetUser's coin
        LambdaQueryWrapper<User> targetUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        targetUserLambdaQueryWrapper.eq(User::getUid, article.getOwner());
        User targetUser = userMapper.selectOne(targetUserLambdaQueryWrapper);
        targetUser.setCoins(targetUser.getCoins() + volume);
        userMapper.updateById(targetUser);
        // Step 5: add targetUser's coin record
        coinRecordMapper.insert(new CoinRecord(null, article.getOwner(), CoinRecordType.COIN_INCOME, volume,
                targetUser.getCoins(), "文章投币", "来自用户 " + payUser.getNickname() + " 给文章 " + article.getTitle() + " 的投币",
                CoinRecordStatus.SUCCESS, now, now, 0));
        // Step 6: add article's coin
        articleAsyncService.increaseTippingById(articleId, volume);
        log.info("用户 {} 给文章 {} 投币 {}", payUser.getUid(), articleId, volume);
        // Step 7: send message to payUser
        messageService.createMessage(payUser.getUid(), "感谢您为文章 " + article.getTitle() + " 投币", MessageType.NORMAL,
                "账号经验 + " + volume, "");
        // Step 8: send message to targetUser
        messageService.createMessage(article.getOwner(), "您的文章收到来自用户 " + payUser.getNickname() + " 的投币", MessageType.NORMAL,
                "文章: " + article.getTitle() + ", 投币: " + volume, "/article/" + articleId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void forward(Integer uid, Integer articleId) {
        LambdaUpdateWrapper<Article> articleLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        articleLambdaUpdateWrapper.eq(Article::getId, articleId);
        if (!articleMapper.exists(articleLambdaUpdateWrapper)) {
            throw new ForumExistentialityException("目标文章不存在");
        }
        log.info("用户 {} 转发文章 {}", uid, articleId);
        articleAsyncService.increaseForwardsById(articleId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer addComment(User user, Integer articleId, Integer targetCommentId, String content) {
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLambdaQueryWrapper.eq(Article::getId, articleId);
        Article article = articleMapper.selectOne(articleLambdaQueryWrapper);
        if (article == null) {
            throw new ForumExistentialityException("目标文章不存在");
        }
        Comment targetComment = null;
        if (targetCommentId != null) {
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getId, targetCommentId);
            targetComment = commentMapper.selectOne(commentLambdaQueryWrapper);
            if (targetComment == null) {
                throw new ForumExistentialityException("目标评论不存在");
            }
        }
        Comment comment = new Comment(null, user.getUid(), content, articleId, targetCommentId, 0, new Date(), 0);
        commentMapper.insert(comment);
        log.info("用户 {} 评论了文章 {}", user.getUid(), articleId);
        if (targetCommentId == null) {
            messageService.createMessage(article.getOwner(), "用户 " + user.getNickname() + " 评论了你的文章 " + article.getTitle(),
                    MessageType.NORMAL, comment.getContent(), "/article/" + comment.getTargetArticle() + "?showComment=" + comment.getId());
        } else {
            messageService.createMessage(targetComment.getCommenter(), "用户 " + user.getNickname() + " 回复了你的评论", MessageType.NORMAL, comment.getContent(),
                    "/article/" + comment.getTargetArticle() + "?showComment=" + comment.getId());
        }
        return comment.getId();
    }

    public GetCommentListVO getCommentListByArticleId(Integer uid, Integer articleId, Boolean latest, Integer pageNumber, Integer pageSize) {
        LambdaQueryWrapper<Article> articleLambdaQueryWrapper = new LambdaQueryWrapper<>();
        articleLambdaQueryWrapper.eq(Article::getId, articleId);
        if (!articleMapper.exists(articleLambdaQueryWrapper)) {
            throw new ForumExistentialityException("文章不存在");
        }
        PageHelperVO<GetCommentVO> commentPageHelperVO = new PageHelperVO<>(
                PageHelper.startPage(pageNumber, pageSize < 20 ? pageSize : 20).doSelectPageInfo(() -> {
                    MPJLambdaWrapper<Comment> commentLambdaWrapper = new MPJLambdaWrapper<>();
                    commentLambdaWrapper
                            .eq("t.target_article", articleId)
                            .orderByDesc(latest, "t.create_time")
                            .orderByDesc(!latest, "t.thumbs_up")
                            .selectAs("t", Comment::getId, GetCommentVO::getId)
                            .selectAs("t", Comment::getCommenter, GetCommentVO::getCommenter)
                            .selectAs("t", Comment::getContent, GetCommentVO::getContent)
                            .selectAs("t", Comment::getTargetComment, GetCommentVO::getTargetComment)
                            .selectAs("t", Comment::getCreateTime, GetCommentVO::getCreateTime)
                            .selectAs("t", Comment::getThumbsUp, GetCommentVO::getThumbsUp)
                            .selectAs("comment_user", User::getNickname, GetCommentVO::getCommenterNickname)
                            .selectAs("comment_user", User::getAvatar, GetCommentVO::getCommenterAvatar)
                            .selectAs("target_comment", Comment::getCommenter, GetCommentVO::getTargetCommenter)
                            .selectAs("target_user", User::getNickname, GetCommentVO::getTargetCommenterNickname)
                            .selectAs("thumbs_up", ThumbsUp::getId, GetCommentVO::getAlreadyThumbsUp)
                            .leftJoin(Comment.class, "target_comment", Comment::getId, "t", Comment::getTargetComment)
                            .leftJoin(User.class, "target_user", User::getUid, "target_comment", Comment::getCommenter)
                            .innerJoin(User.class, "comment_user", User::getUid, "t", Comment::getCommenter)
                            .leftJoin(ThumbsUp.class, "thumbs_up", on -> on
                                    .eq(ThumbsUp::getBeThumbsUpedId, "t", Comment::getId)
                                    .eq(ThumbsUp::getThumbsUper, uid)
                                    .eq(ThumbsUp::getType, ThumbsUpType.COMMENT));
                    commentMapper.selectJoinList(GetCommentVO.class, commentLambdaWrapper);
                })
        );
        return new GetCommentListVO(commentPageHelperVO, CosFolderPath.AVATAR.getPath());
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
