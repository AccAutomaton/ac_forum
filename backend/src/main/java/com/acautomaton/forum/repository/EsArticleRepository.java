package com.acautomaton.forum.repository;

import com.acautomaton.forum.entity.EsArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface EsArticleRepository extends ElasticsearchRepository<EsArticle, Integer> {
    Page<EsArticle> findByTitleOrContent(String title, String content, Pageable pageable);
    Page<EsArticle> findByTopic(Integer topicId, Pageable pageable);

    @Query(""" 
              {
                "bool": {
                  "must": [
                    {
                      "match": {
                        "topic": ?0
                      }
                    },
                    {
                      "bool": {
                        "should": [
                          {
                            "query_string": {
                              "query": "?1",
                              "fields": [
                                "title"
                              ]
                            }
                          },
                          {
                            "query_string": {
                              "query": "?2",
                              "fields": [
                                "content"
                              ]
                            }
                          }
                        ]
                      }
                    }
                  ]
                }
              }
            """)
    Page<EsArticle> findByTopicAndTitleOrContent(Integer topic, String title, String content, Pageable pageable);
}
