package com.x.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.x.common.exception.BusinessException;
import com.x.mapper.UserMapper;
import com.x.pojo.dto.RegisterDTO;
import com.x.pojo.dto.UserLoginDTO;
import com.x.pojo.dto.UserUpdateDTO;
import com.x.pojo.entity.User;
import com.x.service.UserService;
import com.x.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service


public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        User user;
        //判断是手机号还是账号登录
        if(userLoginDTO.getIdentifier().matches("^1[3-9]\\d{9}$"))
            user=userMapper.getUserByPhone(userLoginDTO.getIdentifier());
        else if(userLoginDTO.getIdentifier().length()==6)
            user=userMapper.getUserByAccount(userLoginDTO.getIdentifier());
        else
            throw new BusinessException("请输入正确的手机号或账号!");
        //判断用户是否存在
        if(user==null)
            throw new BusinessException("用户不存在!");
        else  if(!bCryptPasswordEncoder.matches(userLoginDTO.getPassword(), user.getPassword()))
            throw new BusinessException("密码错误!");
        else if(user.getStatus()==1)
            throw new BusinessException("用户被禁用,请联系管理员qq1846326845!");

        return user;
    }

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        //先根据phone判断是否已注册
        User user=userMapper.getUserByPhone(registerDTO.getPhone());
        if(user!=null)
            throw new BusinessException("该手机号已注册!");
        //构造新用户对象插入数据库
        User newUser= BeanUtil.copyProperties(registerDTO,User.class);
        newUser.setPassword(bCryptPasswordEncoder.encode(registerDTO.getPassword()));
        userMapper.insert(newUser);
        //插入后再根据用户id生成账号，防止并发问题
        Long id=newUser.getUserId();
        String account=String.format("%06d", id);
        userMapper.updateAccountById(id,account);
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.getUserById(userId);
    }

    @Override
    public void updateUser(UserUpdateDTO userUpdateDTO) {
        userMapper.updateUser(userUpdateDTO);
    }

    @Override
    public void sendCode(String phone) {
        //生成六位数验证码,模拟短信登录
        String code=String.format("%06d", (int)(Math.random()*1000000));
        //将验证码存入redis，并且设置过期时间
        RedisUtil.set(phone,code,60);
        System.out.println("验证码:"+code);
    }

    @Override
    public User codeLogin(String phone, String code) {

        //到redis中查找手机号对应的验证码
        String redisCode=RedisUtil.get(phone);
        //未找到
        if (redisCode==null){
            throw new BusinessException("验证码不存在或已过期，请重试!");
        }
        //找到
        if(redisCode.equals(code)){
            //先判断手机号是否已注册
            User user=userMapper.getUserByPhone(phone);
            if(user==null){
                //未注册，则注册,默认密码123,用户名user_ phone
                RegisterDTO registerDTO=new RegisterDTO(phone,"123","user_"+ phone);
                register(registerDTO);
                user=userMapper.getUserByPhone(phone);
            }
            return  user;
        }
        else{
            throw new BusinessException("验证码错误!");
        }

    }
}
