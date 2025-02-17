package com.acautomaton.forum.service;

import com.acautomaton.forum.entity.Topic;
import com.acautomaton.forum.entity.User;
import com.acautomaton.forum.enumerate.CosFolderPath;
import com.acautomaton.forum.exception.ForumExistentialityException;
import com.acautomaton.forum.exception.ForumIllegalAccountException;
import com.acautomaton.forum.mapper.TopicMapper;
import com.acautomaton.forum.service.async.TopicAsyncService;
import com.acautomaton.forum.service.util.CosService;
import com.acautomaton.forum.service.util.RedisService;
import com.acautomaton.forum.vo.topic.GetTopicListVO;
import com.acautomaton.forum.vo.topic.GetTopicVO;
import com.acautomaton.forum.vo.util.PageHelperVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TopicService {
    TopicAsyncService topicAsyncService;
    TopicMapper topicMapper;
    CosService cosService;
    RedisService redisService;

    @Autowired
    public TopicService(TopicMapper topicMapper, CosService cosService, RedisService redisService, TopicAsyncService topicAsyncService) {
        this.topicMapper = topicMapper;
        this.cosService = cosService;
        this.redisService = redisService;
        this.topicAsyncService = topicAsyncService;
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
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTopic(Integer id, Integer operatorUid, String title, String description) {
        UpdateWrapper<Topic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", id);
        updateWrapper.eq("administrator", operatorUid);
        if (!topicMapper.exists(updateWrapper)) {
            throw new ForumIllegalAccountException("您没有权限修改该话题");
        }
        updateWrapper.set("title", title);
        updateWrapper.set("description", description);
        topicMapper.update(updateWrapper);
        log.info("用户 {} 修改了话题 {}", operatorUid, id);
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

    public GetTopicListVO getTopicList(Integer pageNumber, Integer pageSize, String queryType, String keyword) {
        PageHelperVO<GetTopicVO> pageHelperVO = new PageHelperVO<>(
                PageHelper.startPage(pageNumber, pageSize < 20 ? pageSize : 20).doSelectPageInfo(() -> getTopicList(queryType, keyword))
        );
        return new GetTopicListVO(pageHelperVO, CosFolderPath.TOPIC_AVATAR.getPath());
    }

    private List<GetTopicVO> getTopicList(String queryType, String keyword) {
        MPJLambdaWrapper<Topic> mpjLambdaWrapper = new MPJLambdaWrapper<>();
        mpjLambdaWrapper
                .select(Topic::getId, Topic::getTitle, Topic::getDescription, Topic::getArticles, Topic::getVisits, Topic::getCreateTime, Topic::getAvatar)
                .selectAs(Topic::getAdministrator, GetTopicVO::getAdministratorId)
                .selectAs(User::getNickname, GetTopicVO::getAdministratorNickname)
                .innerJoin(User.class, User::getUid, Topic::getAdministrator)
                .likeIfExists(Topic::getTitle, keyword).or().likeIfExists(Topic::getDescription, keyword);
        switch (queryType) {
            case "visitsByDesc":
                mpjLambdaWrapper.orderByDesc(Topic::getVisits);
                break;
            case "visitsByAsc":
                mpjLambdaWrapper.orderByAsc(Topic::getVisits);
                break;
            case "createTimeByDesc":
                mpjLambdaWrapper.orderByDesc(Topic::getCreateTime);
                break;
            case "createTimeByAsc":
                mpjLambdaWrapper.orderByAsc(Topic::getCreateTime);
                break;
            case "articlesByDesc":
                mpjLambdaWrapper.orderByDesc(Topic::getArticles);
                break;
            case "articlesByAsc":
                mpjLambdaWrapper.orderByAsc(Topic::getArticles);
                break;
            case "synthesize":
            default:
                mpjLambdaWrapper.orderByDesc(Topic::getVisits, Topic::getArticles, Topic::getCreateTime);
        }
        return topicMapper.selectJoinList(GetTopicVO.class, mpjLambdaWrapper);
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
