package com.x.service;

import com.x.pojo.dto.RegisterDTO;
import com.x.pojo.dto.UserLoginDTO;
import com.x.pojo.entity.User;

public interface UserService {
    User login(UserLoginDTO userLoginDTO);

    void register(RegisterDTO registerDTO);
}
