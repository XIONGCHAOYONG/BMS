package com.x.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO implements Serializable {
    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String address;
    private String avatar;
}
