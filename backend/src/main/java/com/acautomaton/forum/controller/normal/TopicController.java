package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.dto.topic.CreateTopicDTO;
import com.acautomaton.forum.dto.topic.UpdateTopicDTO;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.TopicService;
import com.acautomaton.forum.service.UserService;
import com.acautomaton.forum.vo.topic.GetTopicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PutMapping("/create")
    public Response createTopic(@Validated @RequestBody CreateTopicDTO dto) {
        Integer topicId = topicService.createTopic(dto.getTitle(), dto.getDescription(), userService.getCurrentUser().getUid());
        return Response.success(Map.of("topicId", topicId));
    }

    @DeleteMapping("/delete")
    public Response deleteTopic(@RequestParam Integer topicId) {
        topicService.deleteTopic(topicId, userService.getCurrentUser().getUid());
        return Response.success();
    }

    @PatchMapping("/update")
    public Response updateTopic(@Validated @RequestBody UpdateTopicDTO dto) {
        topicService.updateTopic(dto.getId(), userService.getCurrentUser().getUid(), dto.getTitle(), dto.getDescription());
        return Response.success();
    }

    @GetMapping("/get/one")
    public Response getOneTopic(@RequestParam Integer topicId) {
        GetTopicVO vo = topicService.getTopicById(userService.getCurrentUser().getUid(), topicId);
        return Response.success(vo);
    }

    @GetMapping("/get/list")
    public Response getTopicList(@RequestParam Integer pageNumber,
                                 @RequestParam Integer pageSize,
                                 @RequestParam String queryType,
                                 @RequestParam String keyword) {
        return Response.success(topicService.getTopicList(pageNumber, pageSize, queryType, keyword));
    }
}
