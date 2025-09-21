package com.x.controller.user;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/notification")
@Tag(name = "通知接口")
public class NotificationController {
    //  获得所有通知
}
