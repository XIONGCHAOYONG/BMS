package com.x.controller.user;


import com.x.pojo.entity.ChatMessage;
import com.x.service.ChatMessageService;
import com.x.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chatMessage")
@Tag(name = "聊天消息")
public class ChatMessageController  {
    @Autowired
    private ChatMessageService chatMessageService;


    @RequestMapping("/getChatMessage")
    @Operation(summary = "获取聊天消息")
    public Result<List<ChatMessage>> getChatMessage() {
        return Result.success(chatMessageService.getChatMessage());
    }

    @PutMapping("/addChatMessage")
    @Operation(summary = "添加聊天消息")
    public Result addChatMessage(@RequestBody ChatMessage chatMessage) {
        chatMessageService.addChatMessage(chatMessage);
        return Result.success();
    }
}
