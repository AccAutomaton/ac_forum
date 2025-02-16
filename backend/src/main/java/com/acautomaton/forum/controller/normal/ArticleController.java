package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.dto.article.CreateArticleDTO;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.ArticleService;
import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/article")
public class ArticleController {
    ArticleService articleService;
    UserService userService;

    @Autowired
    public ArticleController(ArticleService articleService, UserService userService) {
        this.articleService = articleService;
        this.userService = userService;
    }

    @RequestMapping("/create")
    public Response createArticle(@RequestBody CreateArticleDTO dto) {
        Integer articleId = articleService.createArticle(
                userService.getCurrentUser().getUid(), dto.getTopic(), dto.getTitle(), dto.getContent()
        );
        return Response.success(Map.of("articleId", articleId));
    }

    @GetMapping("/get/one")
    public Response getOneArticle(@RequestParam Integer articleId) {
        return Response.success(articleService.getAriticleById(userService.getCurrentUser().getUid(), articleId));
    }
}
