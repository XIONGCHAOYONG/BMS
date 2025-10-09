package com.x.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage implements Serializable {
    private Long messageId;
    private Long userId;
    private String content;
    private Role role;  // user / ai
    private LocalDate createTime;

    public enum Role {
        user,
        ai
    }
}
