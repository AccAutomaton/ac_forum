package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.EsTopic;
import com.acautomaton.forum.entity.Topic;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.enumerate.TopicQueryType;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalAccountException;
import com.acautomaton.forum.mapper.TopicMapper;
import com.acautomaton.forum.repository.EsTopicRepository;
import com.acautomaton.forum.service.async.ArticleAsyncService;
import com.acautomaton.forum.service.async.TopicAsyncService;
import com.acautomaton.forum.service.util.CosService;
import com.acautomaton.forum.service.util.RedisService;
import com.acautomaton.forum.vo.topic.GetTopicListVO;
import com.acautomaton.forum.vo.topic.GetTopicVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TopicService {
    TopicAsyncService topicAsyncService;
    ArticleAsyncService articleAsyncService;
    TopicMapper topicMapper;
    CosService cosService;
    RedisService redisService;
    EsTopicRepository esTopicRepository;

    @Autowired
    public TopicService(TopicMapper topicMapper, CosService cosService, RedisService redisService, TopicAsyncService topicAsyncService,
                        ArticleAsyncService articleAsyncService, EsTopicRepository esTopicRepository) {
        this.topicMapper = topicMapper;
        this.cosService = cosService;
        this.redisService = redisService;
        this.topicAsyncService = topicAsyncService;
        this.articleAsyncService = articleAsyncService;
        this.esTopicRepository = esTopicRepository;
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
