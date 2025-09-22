package com.x.pojo.vo;

import com.x.common.enumeration.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationVO implements Serializable {
    private Long noticeId;
    private Integer days;
    private NotificationType type;
    private String title;
    private  Long bookId;
}
