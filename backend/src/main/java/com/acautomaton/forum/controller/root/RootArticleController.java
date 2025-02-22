package com.acautomaton.forum.controller.root;

import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.async.ArticleAsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/root/article")
public class RootArticleController {
    ArticleAsyncService articleAsyncService;

    @Autowired
    public RootArticleController(ArticleAsyncService articleAsyncService) {
        this.articleAsyncService = articleAsyncService;
    }

    @PostMapping("/elasticSearch/synchronize/fully")
    public Response synchronizeDataToElasticsearchFully() {
        articleAsyncService.synchronizeDataToElasticSearchFully();
        return Response.success();
    }
}
