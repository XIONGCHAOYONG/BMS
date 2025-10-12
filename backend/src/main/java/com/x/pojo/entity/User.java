package com.x.pojo.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long userId;
    private String username;
    private String account;
    private String password;
    private String realName;
    private String phone;
    private String address;
    private String avatar;
    private Byte role;
    private Byte status;
    private LocalDateTime createTime;
    private Integer points;
}
