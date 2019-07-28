package com.maxwang.buycar.dao;

import com.maxwang.buycar.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserDao {

    @Select("select u.* ,r.name from user u LEFT JOIN userandrole s on u.id= s.user_id LEFT JOIN role r on s.role_id=r.id  where u.username=#{username}")
    User findByUserName(@Param("username") String username);

    @Select("select * from user where username=#{username}")
    User getByName(@Param("username") String username);

    @Select("select * from user where id=#{id}")
    User getById(@Param("id") Integer id);

    @Insert("insert into user(username,password,salt,tel,role) values(#{username},#{password},#{salt},#{tel},#{role})")
    @SelectKey(keyColumn="id", keyProperty="id", resultType=int.class, before=false, statement="select last_insert_id()")
    int userAdd(User user);


    @Select("select * from user")
    List<User> getUserList();

    @Update("update user set username=#{username},role=#{role} ,tel=#{tel}where id=#{id} ")
    int userUpdate(User user);

    @Delete("delete from user where id=#{id}")
    int deleteUser(@Param("id") Integer id);

    @Select("select * from user where username=#{username}")
    User getUsername(@Param("username") String username);

    @Select("select * from user where tel=#{tel}")
    User getTel(@Param("tel") String tel);
}
