package com.x.controller.user;

import cn.hutool.core.bean.BeanUtil;
import com.x.pojo.dto.RegisterDTO;
import com.x.pojo.dto.UserLoginDTO;
import com.x.pojo.entity.User;
import com.x.pojo.vo.UserLoginVO;
import com.x.properties.JwtProperties;
import com.x.service.UserService;
import com.x.utils.JwtUtil;
import com.x.utils.Result;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Tag(name = "用户接口")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        //获取用户信息
        User user=userService.login(userLoginDTO);
        //生成jwt令牌
        Map<String,Object> claims=new HashMap<>();
        claims.put("userId",user.getUserId());
        String token= JwtUtil.createJWT(
                jwtProperties.getSecretKey(),
                jwtProperties.getTtl(),
                claims);
        //给返回对象VO赋值
        UserLoginVO res= new UserLoginVO(user,token);
        return Result.success(res);
    }
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public  Result<String> register(@RequestBody RegisterDTO registerDTO){
        userService.register(registerDTO);
        return Result.success("注册成功!");
    }
}
