package com.x.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification implements Serializable {
    private Long noticeId;
    private Long userId;
    private String message;
    private LocalDateTime createTime;
}
