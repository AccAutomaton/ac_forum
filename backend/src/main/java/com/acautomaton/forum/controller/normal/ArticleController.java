package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.dto.article.AddCommentDTO;
import com.acautomaton.forum.dto.article.CreateArticleDTO;
import com.acautomaton.forum.enumerate.ArticleQueryType;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.ArticleService;
import com.acautomaton.forum.service.UserService;
import com.acautomaton.forum.vo.article.GetEsArticalListVO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @PutMapping("")
    public Response createArticle(@RequestBody @Validated CreateArticleDTO dto) {
        Integer articleId = articleService.createArticle(
                userService.getCurrentUser().getUid(), dto.getTopic(), dto.getTitle(), dto.getContent()
        );
        return Response.success(Map.of("articleId", articleId));
    }

    @GetMapping("/{articleId}")
    public Response getOneArticle(@PathVariable Integer articleId) {
        return Response.success(articleService.getAriticleById(userService.getCurrentUser().getUid(), articleId));
    }

    @PatchMapping("/{articleId}")
    public Response updateArticle(@PathVariable Integer articleId, @RequestBody @Validated CreateArticleDTO dto) {
        articleService.updateArticleById(userService.getCurrentUser().getUid(), articleId, dto.getTitle(), dto.getContent(), dto.getTopic());
        return Response.success();
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

    @GetMapping("/image/authorization/upload")
    public Response getImageUploadAuthorization() {
        return Response.success(articleService.getArticleImageUploadAuthorization(userService.getCurrentUser().getUid()));
    }

    @PutMapping("/{articleId}/thumbsUp")
    public Response thumbsUp(@PathVariable Integer articleId) {
        articleService.thumbsUp(userService.getCurrentUser(), articleId);
        return Response.success();
    }

    @DeleteMapping("/{articleId}/thumbsUp")
    public Response unThumbsUp(@PathVariable Integer articleId) {
        articleService.unThumbsUp(userService.getCurrentUser().getUid(), articleId);
        return Response.success();
    }

    @PutMapping("/{articleId}/collect")
    public Response collect(@PathVariable Integer articleId) {
        articleService.collect(userService.getCurrentUser().getUid(), articleId);
        return Response.success();
    }

    @DeleteMapping("/{articleId}/collect")
    public Response unCollect(@PathVariable Integer articleId) {
        articleService.uncollect(userService.getCurrentUser().getUid(), articleId);
        return Response.success();
    }

    @PostMapping("/{articleId}/tipping")
    public Response tipping(@PathVariable Integer articleId, @RequestParam @Max(10000000) @Min(0) Integer volume) {
        articleService.tipping(userService.getCurrentUser(), articleId, volume);
        return Response.success();
    }

    @PatchMapping("/{articleId}/forward")
    public Response forward(@PathVariable Integer articleId) {
        articleService.forward(userService.getCurrentUser().getUid(), articleId);
        return Response.success();
    }

    @PutMapping("/{articleId}/comment")
    public Response addComment(@PathVariable Integer articleId, @RequestBody @Validated AddCommentDTO dto) {
        Integer commentId = articleService.addComment(userService.getCurrentUser(), articleId, dto.getTargetCommentId(), dto.getContent());
        return Response.success(Map.of("commentId", commentId));
    }

    @GetMapping("/{articleId}/comment/list")
    public Response getCommentList(@PathVariable Integer articleId,
                                   @RequestParam(defaultValue = "false") Boolean latest,
                                   @RequestParam(defaultValue = "1") Integer pageNumber,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return Response.success(articleService.getCommentListByArticleId(userService.getCurrentUser().getUid(), articleId, latest, pageNumber, pageSize));
    }

    @GetMapping("/list/visit")
    public Response getArticleListVisitTopX(@RequestParam Integer topX) {
        return Response.success(articleService.getArticleVisitTopXListByOwnerId(userService.getCurrentUser().getUid(), topX));
    }

    @GetMapping("/list/own")
    public Response getOwnArticleList(@RequestParam(defaultValue = "0") Integer pageNumber,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(defaultValue = "2") Integer queryType) {
        return Response.success(articleService.getEsArticalListByOwnerUid(userService.getCurrentUser().getUid(), ArticleQueryType.getById(queryType), pageNumber, pageSize));
    }

    @DeleteMapping("/{articleId}")
    public Response deleteArticle(@PathVariable Integer articleId) {
        articleService.deleteArticleById(userService.getCurrentUser().getUid(), articleId);
        return Response.success();
    }
}
