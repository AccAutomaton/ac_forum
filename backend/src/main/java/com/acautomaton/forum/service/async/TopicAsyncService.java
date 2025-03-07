package com.acautomaton.forum.service.async;

import com.acautomaton.forum.entity.*;
import com.acautomaton.forum.mapper.TopicMapper;
import com.acautomaton.forum.repository.EsTopicRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TopicAsyncService {
    TopicMapper topicMapper;
    EsTopicRepository esTopicRepository;

    @Autowired
    public TopicAsyncService(TopicMapper topicMapper, EsTopicRepository esTopicRepository) {
        this.topicMapper = topicMapper;
        this.esTopicRepository = esTopicRepository;
    }

    @Async
    public void increaseVisitsById(Integer id) {
        LambdaUpdateWrapper<Topic> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Topic::getId, id);
        lambdaUpdateWrapper.setIncrBy(Topic::getVisits, 1);
        topicMapper.update(lambdaUpdateWrapper);
        synchronizeTopicToElasticSearchByArticleId(id);
        log.info("[Async] 话题 {} 浏览量+1", id);
    }

    @Async
    public void synchronizeTopicDataToElasticSearchFully() {
        LambdaQueryWrapper<Topic> queryWrapper = new LambdaQueryWrapper<>();
        long count = topicMapper.selectCount(queryWrapper);
        List<EsTopic> topics;
        for (long i = 0; i < count; i += 100) {
            queryWrapper.last("limit " + i + ", 100");
            topics = EsTopic.toEsTopicList(topicMapper.selectList(queryWrapper));
            esTopicRepository.saveAll(topics);
            log.info("[Async]全量同步话题数据到 ElasticSearch ({} / {})", Math.min(i + 100, count), count);
        }
    }

    @Async
    public void synchronizeTopicToElasticSearchByArticleId(Integer id) {
        esTopicRepository.save(new EsTopic(topicMapper.selectById(id)));
        log.info("[Async]同步话题 {} 数据到 ElasticSearch", id);
    }

    @Async
    public void synchronizeDeleteTopicToElasticSearchByTopicId(Integer id) {
        esTopicRepository.deleteById(id);
        log.info("[Async]同步删除 ElasticSearch 话题 {}", id);
    }
}
