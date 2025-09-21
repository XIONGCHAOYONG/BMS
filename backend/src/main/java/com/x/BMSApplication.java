package com.x;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling  // 开启定时任务
public class BMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(BMSApplication.class, args);
    }
}
