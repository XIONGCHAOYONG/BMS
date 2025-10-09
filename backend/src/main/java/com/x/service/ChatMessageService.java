package com.x.service;

import com.x.pojo.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    List<ChatMessage> getChatMessage();

    void addChatMessage(ChatMessage chatMessage);
}
