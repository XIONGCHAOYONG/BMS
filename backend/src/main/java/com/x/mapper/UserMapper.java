package com.x.mapper;
import com.x.pojo.dto.UserUpdateDTO;
import com.x.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    @Select("select * from user where account = #{account}")
    User getUserByAccount(String account);
    @Select("select * from user where phone = #{phone}")
    User getUserByPhone(String phone);
    @Select("select count(*) from user ")
    Long getUserCount();
    void  insert(User user);
    @Update("update user set account = #{account} where user_id = #{userId}")
    void updateAccountById(Long userId, String account);

    @Select("select * from user where user_id = #{userId}")
    User getUserById(Integer userId);

    void updateUser(UserUpdateDTO userUpdateDTO);

    @Update("update user set points = points- #{points} where user_id = #{userId}")
    void subPoints(Long userId, Integer points);

    @Update("update user set points = 100 ")
    void recoverPoints();
}
