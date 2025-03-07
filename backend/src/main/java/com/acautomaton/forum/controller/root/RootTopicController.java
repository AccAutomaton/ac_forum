package com.acautomaton.forum.controller.root;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.async.TopicAsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/root/topic")
public class RootTopicController {
    TopicAsyncService topicAsyncService;

    @Autowired
    public RootTopicController(TopicAsyncService topicAsyncService) {
        this.topicAsyncService = topicAsyncService;
    }

    @PostMapping("/elasticSearch/synchronize/fully")
    public Response synchronizeDataToElasticSearchFully() {
        topicAsyncService.synchronizeTopicDataToElasticSearchFully();
        return Response.success();
    }
}
