package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.enumerate.ArticleQueryType;
import com.acautomaton.forum.enumerate.TopicQueryType;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.ArticleService;
import com.acautomaton.forum.service.ArtistService;
import com.acautomaton.forum.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/artist")
public class ArtistController {
    ArtistService artistService;
    ArticleService articleService;
    TopicService topicService;

    @Autowired
    public ArtistController(ArtistService artistService, ArticleService articleService, TopicService topicService) {
        this.artistService = artistService;
        this.articleService = articleService;
        this.topicService = topicService;
    }

    @GetMapping("/{artistId}/statistic")
    public Response getStatisticData(@PathVariable Integer artistId) {
        return Response.success(artistService.getStatisticData(artistId));
    }

    @GetMapping("/{artistId}/article")
    public Response getArticleList(@PathVariable Integer artistId,
                                   @RequestParam(defaultValue = "0") Integer pageNumber,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "2") Integer queryType) {
        return Response.success(articleService.getEsArticalListByOwnerUid(artistId, ArticleQueryType.getById(queryType), pageNumber, pageSize));
    }

    @GetMapping("/{artistId}/topic")
    public Response getTopicList(@PathVariable Integer artistId,
                                 @RequestParam(defaultValue = "0") Integer pageNumber,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestParam(defaultValue = "2") Integer queryType) {
        return Response.success(topicService.getTopicListByAdministratorUid(artistId, TopicQueryType.getById(queryType), pageNumber, pageSize));
    }

    @GetMapping("/{artistId}/thumbsUp")
    public Response getThumbsUpList(@PathVariable Integer artistId,
                                    @RequestParam(defaultValue = "1") Integer pageNumber,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        return Response.success(articleService.getEsArticleListByThumbsUperUid(artistId, pageNumber, pageSize));
    }

    @GetMapping("/{artistId}/collection")
    public Response getCollectionList(@PathVariable Integer artistId,
                                      @RequestParam(defaultValue = "1") Integer pageNumber,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        return Response.success(articleService.getEsArticleListByCollectorUid(artistId, pageNumber, pageSize));
    }

    @GetMapping("/{artistId}/follows")
    public Response getFollowsList(@PathVariable Integer artistId,
                                   @RequestParam(defaultValue = "1") Integer pageNumber,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        return Response.success(artistService.getFollows(artistId, pageNumber, pageSize));
    }

    @GetMapping("/{artistId}/fans")
    public Response getFansList(@PathVariable Integer artistId,
                                @RequestParam(defaultValue = "1") Integer pageNumber,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return Response.success(artistService.getFans(artistId, pageNumber, pageSize));
    }
}
