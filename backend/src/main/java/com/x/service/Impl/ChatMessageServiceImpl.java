package com.x.service.Impl;



import com.x.common.context.BaseContext;
import com.x.common.exception.BusinessException;
import com.x.mapper.ChatMessageMapper;
import com.x.pojo.entity.ChatMessage;
import com.x.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public List<ChatMessage> getChatMessage() {
        return chatMessageMapper.getChatMessage(BaseContext.getCurrentId());
    }

    @Override
    public void addChatMessage(ChatMessage chatMessage) {
        try {
            chatMessageMapper.addChatMessage(chatMessage);
        }catch (Exception e){
            throw new BusinessException("添加聊天记录失败");
        }
    }
}
