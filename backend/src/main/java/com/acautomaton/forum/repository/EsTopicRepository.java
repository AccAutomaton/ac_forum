package com.acautomaton.forum.repository;

import com.acautomaton.forum.entity.EsTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface EsTopicRepository extends ElasticsearchRepository<EsTopic, Integer> {
    Page<EsTopic> findByTitleOrDescription(String title, String description, Pageable pageable);
    Page<EsTopic> findByAdministrator(Integer administrator, Pageable pageable);
}
