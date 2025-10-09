package com.x.mapper;

import com.x.pojo.entity.ChatMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    @Select("select * from chat_message where user_id = #{userId}")
    List<ChatMessage> getChatMessage(Long userId);

    @Insert("insert into chat_message ( user_id, content, role, create_time)" +
            " values ( #{userId}, #{content}, #{role}, #{createTime})")
    void addChatMessage(ChatMessage chatMessage);
}
