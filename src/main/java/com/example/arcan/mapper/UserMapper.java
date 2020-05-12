package com.example.arcan.mapper;

import com.example.arcan.dao.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Insert("insert into user(id,mailaddress, username,password) values(#{id},#{mailaddress},#{username},#{password})")
    void insert(User user);

    @Select("select * from user where mailaddress=#{mailaddress}")
    User findUserByMail(String mailaddress);

    @Select("select * from user where id=#{id}")
    User findUserById(String id);

    @Update("update user set username=#{username}, password=#{password}, avatar=#{avatar}, validate=#{validate} where id=#{id}")
    void updateUser(User user);

    @Update("update user set avatar=#{avatar} where id=#{userId}")
    void updateAvatar(@Param("avatar") String avatar, @Param("userId") String userId);
}
