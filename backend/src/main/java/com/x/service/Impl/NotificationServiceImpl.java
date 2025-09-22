package com.x.service.Impl;
import com.x.common.context.BaseContext;
import com.x.mapper.NotificationMapper;
import com.x.pojo.vo.NotificationVO;
import com.x.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Override
    public List<NotificationVO> getAllNotification() {
        return notificationMapper.getAllNotification(BaseContext.getCurrentId());
    }
}
