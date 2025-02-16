package com.acautomaton.forum.service.async;

import com.acautomaton.forum.entity.Topic;
import com.acautomaton.forum.mapper.TopicMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TopicAsyncService {
    TopicMapper topicMapper;

    @Autowired
    public TopicAsyncService(TopicMapper topicMapper) {
        this.topicMapper = topicMapper;
    }

    @Async
    public void increaseVisitsById(Integer id) {
        LambdaUpdateWrapper<Topic> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Topic::getId, id);
        lambdaUpdateWrapper.setIncrBy(Topic::getVisits, 1);
        topicMapper.update(lambdaUpdateWrapper);
        log.info("[Async] 话题 {} 浏览量+1", id);
    }
}
