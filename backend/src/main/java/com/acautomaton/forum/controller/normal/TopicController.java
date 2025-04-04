package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.dto.topic.CreateTopicDTO;
import com.acautomaton.forum.dto.topic.UpdateTopicDTO;
import com.acautomaton.forum.enumerate.TopicQueryType;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.TopicService;
import com.acautomaton.forum.service.UserService;
import com.acautomaton.forum.vo.topic.GetTopicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/topic")
public class TopicController {
    UserService userService;
    TopicService topicService;

    @Autowired
    public TopicController(UserService userService, TopicService topicService) {
        this.userService = userService;
        this.topicService = topicService;
    }

    @PutMapping("")
    public Response createTopic(@Validated @RequestBody CreateTopicDTO dto) {
        Integer topicId = topicService.createTopic(dto.getTitle(), dto.getDescription(), userService.getCurrentUser().getUid());
        return Response.success(Map.of("topicId", topicId));
    }

    @DeleteMapping("")
    public Response deleteTopic(@RequestParam Integer topicId) {
        topicService.deleteTopic(topicId, userService.getCurrentUser().getUid());
        return Response.success();
    }

    @PatchMapping("/{topicId}")
    public Response updateTopic(@PathVariable Integer topicId, @Validated @RequestBody UpdateTopicDTO dto) {
        topicService.updateTopic(topicId, userService.getCurrentUser().getUid(), dto.getTitle(), dto.getDescription());
        return Response.success();
    }

    @GetMapping("/avatar/authorization/upload")
    public Response getTopicAvatarUploadAuthorization() {
        return Response.success(topicService.getTopicAvatarUploadAuthorization(userService.getCurrentUser().getUid()));
    }

    @PatchMapping("/{topicId}/avatar")
    public Response updateTopicAvatar(@PathVariable Integer topicId, @RequestParam String avatarFileName) {
        String avatarKey = topicService.updateTopicAvatarByTopicId(userService.getCurrentUser().getUid(), topicId, avatarFileName);
        return Response.success(Map.of("avatarKey", avatarKey));
    }

    @GetMapping("/{topicId}")
    public Response getOneTopic(@PathVariable Integer topicId) {
        GetTopicVO vo = topicService.getTopicById(userService.getCurrentUser().getUid(), topicId);
        return Response.success(vo);
    }

    @GetMapping("/list")
    public Response getTopicList(@RequestParam(defaultValue = "0") Integer pageNumber,
                                 @RequestParam(defaultValue = "15") Integer pageSize,
                                 @RequestParam(defaultValue = "0") Integer queryType,
                                 @RequestParam String keyword) {
        return Response.success(topicService.getTopicList(TopicQueryType.getById(queryType), keyword, pageNumber, pageSize));
    }

    @GetMapping("/list/idAndTitle")
    public Response getTopicIdAndTitleList(@RequestParam String keyword) {
        return Response.success(topicService.getTopicIdAndTitleList(keyword));
    }
}
