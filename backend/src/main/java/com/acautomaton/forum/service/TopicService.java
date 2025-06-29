package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.EsTopic;
import com.acautomaton.forum.entity.Topic;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.CosActions;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.enumerate.PointRecordType;
import com.acautomaton.forum.enumerate.TopicQueryType;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalAccountException;
import com.acautomaton.forum.mapper.TopicMapper;
import com.acautomaton.forum.repository.EsTopicRepository;
import com.acautomaton.forum.service.async.ArticleAsyncService;
import com.acautomaton.forum.service.async.TopicAsyncService;
import com.acautomaton.forum.service.util.CosService;
import com.acautomaton.forum.service.util.RedisService;
import com.acautomaton.forum.vo.cos.CosAuthorizationVO;
import com.acautomaton.forum.vo.topic.GetTopicListVO;
import com.acautomaton.forum.vo.topic.GetTopicVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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

@Slf4j
@Service
public class TopicService {
    TopicAsyncService topicAsyncService;
    ArticleAsyncService articleAsyncService;
    TopicMapper topicMapper;
    CosService cosService;
    RedisService redisService;
    PointService pointService;
    EsTopicRepository esTopicRepository;

    @Autowired
    public TopicService(TopicMapper topicMapper, CosService cosService, RedisService redisService, TopicAsyncService topicAsyncService,
                        ArticleAsyncService articleAsyncService, EsTopicRepository esTopicRepository, PointService pointService) {
        this.topicMapper = topicMapper;
        this.cosService = cosService;
        this.redisService = redisService;
        this.topicAsyncService = topicAsyncService;
        this.articleAsyncService = articleAsyncService;
        this.esTopicRepository = esTopicRepository;
        this.pointService = pointService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer createTopic(String title, String description, Integer administrator) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        if (topicMapper.exists(queryWrapper)) {
            throw new ForumExistentialityException("话题已存在，请更换话题名称");
        }
        Topic topic = new Topic(null, title, description, administrator, 0, 0, new Date(), "", 0);
        topicMapper.insert(topic);
        log.info("用户 {} 创建了话题 {}", administrator, topic.getId());
        esTopicRepository.save(new EsTopic(topic));
        pointService.addPoint(administrator, PointRecordType.NORMAL_INCOME, 10, "发起话题", title);
        return topic.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTopic(Integer id, Integer operatorUid) {
        UpdateWrapper<Topic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.eq("administrator", operatorUid);
        if (!topicMapper.exists(updateWrapper)) {
            throw new ForumIllegalAccountException("您没有权限删除该话题");
        }
        topicMapper.delete(updateWrapper);
        log.info("用户 {} 删除了话题 {}", operatorUid, id);
        articleAsyncService.synchronizeArticleToElasticSearchByTopicId(id);
        topicAsyncService.synchronizeDeleteTopicToElasticSearchByTopicId(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTopic(Integer id, Integer operatorUid, String title, String description) {
        QueryWrapper<Topic> topicQueryWrapper = new QueryWrapper<>();
        topicQueryWrapper.eq("id", id);
        topicQueryWrapper.eq("administrator", operatorUid);
        Topic topic = topicMapper.selectOne(topicQueryWrapper);
        if (topic == null) {
            throw new ForumIllegalAccountException("您没有权限修改该话题");
        }
        if (!topic.getTitle().equals(title)) {
            QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("title", title);
            if (topicMapper.exists(queryWrapper)) {
                throw new ForumExistentialityException("话题已存在，请更换话题名称");
            }
        }
        topic.setTitle(title);
        topic.setDescription(description);
        topicMapper.updateById(topic);
        esTopicRepository.save(new EsTopic((topic)));
        log.info("用户 {} 修改了话题 {}", operatorUid, id);
        articleAsyncService.synchronizeArticleToElasticSearchByTopicId(id);
    }

    public CosAuthorizationVO getTopicAvatarUploadAuthorization(Integer uid) {
        Integer expireSeconds = 60;
        String avatarKey;
        do {
            avatarKey = CosFolderPath.TOPIC_AVATAR + UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() + ".png";
        } while (cosService.objectExists(avatarKey));
        Credentials credentials = cosService.getCosAccessAuthorization(
                expireSeconds, CosActions.PUT_OBJECT, List.of(avatarKey)
        );
        log.info("用户 {} 请求了图片 {} 上传权限", uid, avatarKey);
        return CosAuthorizationVO.keyAuthorization(credentials, expireSeconds, cosService.getBucketName(), cosService.getRegion(), avatarKey);
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateTopicAvatarByTopicId(Integer uid, Integer topicId, String avatarFileName) {
        QueryWrapper<Topic> topicQueryWrapper = new QueryWrapper<>();
        topicQueryWrapper.eq("id", topicId);
        topicQueryWrapper.eq("administrator", uid);
        Topic topic = topicMapper.selectOne(topicQueryWrapper);
        if (topic == null) {
            throw new ForumIllegalAccountException("您没有权限修改该话题");
        }
        String avatarKey = CosFolderPath.TOPIC_AVATAR + "/" + avatarFileName;
        if (!cosService.objectExists(avatarKey)) {
            throw new ForumExistentialityException("上传失败，请重试");
        }
        topic.setAvatar(avatarFileName);
        topicMapper.updateById(topic);
        return avatarKey;
    }

    public GetTopicVO getTopicById(Integer uid, Integer topidId) {
        MPJLambdaWrapper<Topic> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper.eq(Topic::getId, topidId);
        if (!topicMapper.exists(mpjLambdaWrapper)) {
            throw new ForumExistentialityException("话题不存在");
        }
        mpjLambdaWrapper
                .select(Topic::getId, Topic::getTitle, Topic::getDescription, Topic::getArticles, Topic::getVisits, Topic::getCreateTime, Topic::getAvatar)
                .selectAs(Topic::getAdministrator, GetTopicVO::getAdministratorId)
                .selectAs(User::getNickname, GetTopicVO::getAdministratorNickname)
                .selectAs(User::getAvatar, GetTopicVO::getAdministratorAvatar)
                .innerJoin(User.class, User::getUid, Topic::getAdministrator);
        GetTopicVO vo = topicMapper.selectJoinOne(GetTopicVO.class, mpjLambdaWrapper);
        if (vo.getAvatar().isEmpty()) {
            vo.setAvatar(null);
        } else {
            vo.setAvatar(CosFolderPath.TOPIC_AVATAR + vo.getAvatar());
        }
        if (vo.getAdministratorAvatar().isEmpty()) {
            vo.setAdministratorAvatar(null);
        } else {
            vo.setAdministratorAvatar(CosFolderPath.AVATAR + vo.getAdministratorAvatar());
        }
        if (!hasVisitedTopic(uid, topidId, 60)) {
            topicAsyncService.increaseVisitsById(topidId);
        }
        return vo;
    }

    public GetTopicListVO getTopicList(TopicQueryType queryType, String keyword, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize > 20 ? 20 : pageSize, queryType.getSort());
        Page<EsTopic> esTopics;
        if (keyword.isBlank()) {
            esTopics = esTopicRepository.findAll(pageable);
        } else {
            esTopics = esTopicRepository.findByTitleOrDescription(keyword, keyword, pageable);
        }
        return new GetTopicListVO(new PageHelperVO<>(esTopics), CosFolderPath.TOPIC_AVATAR.getPath());
    }

    public GetTopicListVO getTopicListByAdministratorUid(Integer administratorUid, TopicQueryType queryType, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize > 20 ? 20 : pageSize, queryType.getSort());
        Page<EsTopic> esTopics = esTopicRepository.findByAdministrator(administratorUid, pageable);
        return new GetTopicListVO(new PageHelperVO<>(esTopics), CosFolderPath.TOPIC_AVATAR.getPath());
    }

    public List<EsTopic> getTopicIdAndTitleList(String keyword) {
        Pageable pageable = PageRequest.of(0, 20, TopicQueryType.VISITS_DESC.getSort());
        Page<EsTopic> esTopics;
        if (keyword.isBlank()) {
            esTopics = esTopicRepository.findAll(pageable);
        } else {
            esTopics = esTopicRepository.findByTitleOrDescription(keyword, keyword, pageable);
        }
        return EsTopic.getIdAndTitle(esTopics.getContent());
    }

    public List<EsTopic> getTopicVisitTopXByAdministratorId(Integer administratorId, Integer topX) {
        MPJLambdaWrapper<Topic> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper
                .selectAs(Topic::getId, EsTopic::getId)
                .selectAs(Topic::getTitle, EsTopic::getTitle)
                .selectAs(Topic::getVisits, EsTopic::getVisits)
                .eq(Topic::getAdministrator, administratorId)
                .orderByDesc(Topic::getVisits)
                .last("limit " + (topX > 0 && topX <= 20 ? topX : 20));
        return topicMapper.selectJoinList(EsTopic.class, mpjLambdaWrapper);
    }

    private Boolean hasVisitedTopic(Integer uid, Integer topicId, Integer intervalSeconds) {
        String key = "has_visited_topic_" + topicId + "_uid_" + uid + "_interval_" + intervalSeconds;
        if (redisService.hasKey(key)) {
            redisService.set(key, true, intervalSeconds);
            return true;
        } else {
            redisService.set(key, true, intervalSeconds);
            return false;
        }
    }
}
