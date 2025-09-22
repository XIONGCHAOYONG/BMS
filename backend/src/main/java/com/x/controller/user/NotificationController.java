package com.x.controller.user;

import com.x.pojo.vo.NotificationVO;
import com.x.service.NotificationService;
import com.x.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/notification")
@Tag(name = "通知接口")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    //  获得所有通知
    @GetMapping("/getAllNotification")
    @Operation(summary = "获得所有通知")
    public Result<List<NotificationVO>> getAllNotification(){
         return Result.success(notificationService.getAllNotification());
    }

}
