package com.acautomaton.forum.controller.normal;

import com.acautomaton.forum.dto.chat.SendChatMessageDTO;
import com.acautomaton.forum.response.Response;
import com.acautomaton.forum.service.ChatService;
import com.acautomaton.forum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/chat")
public class ChatController {
    ChatService chatService;
    UserService userService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @PutMapping("")
    public Response createChat(@RequestParam Integer receiver) {
        return Response.success(chatService.createChat(userService.getCurrentUser(), receiver));
    }

    @GetMapping("/{chatId}")
    public Response getChat(@PathVariable Integer chatId) {
        return Response.success(chatService.getChatById(userService.getCurrentUser().getUid(), chatId));
    }

    @GetMapping("/list")
    public Response getChatList(@RequestParam(defaultValue = "1") Integer pageNumber,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return Response.success(chatService.getChatList(userService.getCurrentUser().getUid(), pageNumber, pageSize));
    }

    @GetMapping("/{chatId}/message/list")
    public Response getChatMessageList(@PathVariable Integer chatId,
                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                       @RequestParam @Nullable Integer before) {
        return Response.success(chatService.getChatMessageList(userService.getCurrentUser().getUid(), chatId, pageSize, before));
    }

    @PutMapping("/{chatId}/message")
    public Response sendChatMessage(@PathVariable Integer chatId, @RequestBody @Validated SendChatMessageDTO dto) {
        return Response.success(chatService.sendChatNormalMessage(userService.getCurrentUser(), chatId, dto.getContent()));
    }

    @PatchMapping("/{chatId}/read")
    public Response readChat(@PathVariable Integer chatId) {
        chatService.readChat(userService.getCurrentUser().getUid(), chatId);
        return Response.success();
    }

    @GetMapping("/{chatId}/image/authorization/upload")
    public Response getImageUploadAuthorization(@PathVariable Integer chatId) {
        return Response.success(chatService.getImageMessageUploadAuthorization(userService.getCurrentUser().getUid(), chatId));
    }

    @GetMapping("/{chatId}/image/authorization/download")
    public Response getImageDownloadAuthorization(@PathVariable Integer chatId) {
        return Response.success(chatService.getImageMessageDownloadAuthorization(userService.getCurrentUser().getUid(), chatId));
    }

    @PutMapping("/{chatId}/message/image")
    public Response sendChatImageMessage(@PathVariable Integer chatId, @RequestParam String imageFileName) {
        return Response.success(chatService.sendChatImageMessage(userService.getCurrentUser(), chatId, imageFileName));
    }
}
