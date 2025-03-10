package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.dto.article.CreateArticleDTO;
import com.acautomaton.forum.enumerate.ArticleQueryType;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.ArticleService;
import com.acautomaton.forum.service.UserService;
import com.acautomaton.forum.vo.article.GetEsArticalListVO;
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

    @PutMapping("/create")
    public Response createArticle(@RequestBody CreateArticleDTO dto) {
        Integer articleId = articleService.createArticle(
                userService.getCurrentUser().getUid(), dto.getTopic(), dto.getTitle(), dto.getContent()
        );
        return Response.success(Map.of("articleId", articleId));
    }

    @GetMapping("/{articleId}")
    public Response getOneArticle(@PathVariable Integer articleId) {
        return Response.success(articleService.getAriticleById(userService.getCurrentUser().getUid(), articleId));
    }

    @GetMapping("/list")
    public Response getList(@RequestParam(required = false) Integer topicId,
                            @RequestParam(defaultValue = "0") Integer pageNumber,
                            @RequestParam(defaultValue = "5") Integer pageSize,
                            @RequestParam(defaultValue = "0") Integer queryType,
                            @RequestParam String keyword) {
        GetEsArticalListVO vo;
        if (topicId == null) {
            vo = articleService.getEsArticleList(ArticleQueryType.getById(queryType), keyword, pageNumber, pageSize);
        } else {
            vo = articleService.getEsArticleListByTopicId(topicId, ArticleQueryType.getById(queryType), keyword, pageNumber, pageSize);
        }
        return Response.success(vo);
    }

    @GetMapping("/image/updateAuthorization")
    public Response getImageUpdateAuthorization() {
        return Response.success(articleService.getArticleImageUpdateAuthorization(userService.getCurrentUser().getUid()));
    }
}
